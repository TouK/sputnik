package pl.touk.sputnik.processor.pmd;

import com.google.common.base.Joiner;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RulesetsFactoryUtils;
import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.datasource.DataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import pl.touk.sputnik.configuration.GeneralOption;

@Slf4j
public class PmdProcessor implements ReviewProcessor {
    private static final String SOURCE_NAME = "PMD";
    private static final char PMD_INPUT_PATH_SEPARATOR = ',';
    private Renderer renderer;

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review) {
        try {
            PMDConfiguration configuration = new PMDConfiguration();
            configuration.setReportFormat(CollectorRenderer.class.getCanonicalName());
            configuration.setRuleSets(getRulesets());
            configuration.setInputPaths(Joiner.on(PMD_INPUT_PATH_SEPARATOR).join(review.getIOFilenames()));
            doPMD(configuration);
        } catch (Throwable e) {
            log.error("PMD processor error", e);
        }
        return renderer != null ? ((CollectorRenderer)renderer).getReviewResult() : null;
    }

    @NotNull
    @Override
    public String getName() {
        return SOURCE_NAME;
    }

    @Nullable
    private String getRulesets() {
        String ruleSets = ConfigurationHolder.instance().getProperty(GeneralOption.PMD_RULESETS);
        log.info("Using PMD rulesets {}", ruleSets);
        return ruleSets;
    }

    /**
     * PMD has terrible design of process configuration. You must use report file with it.
     * I paste this method here and improve it.
     */
    public void doPMD(@NotNull PMDConfiguration configuration) {
        // Load the RuleSets
        long startLoadRules = System.nanoTime();
        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.getRulesetFactory(configuration);

        RuleSets ruleSets = RulesetsFactoryUtils.getRuleSets(configuration.getRuleSets(), ruleSetFactory, startLoadRules);
        if (ruleSets == null)
            return;

        Set<Language> languages = getApplicableLanguages(configuration, ruleSets);
        List<DataSource> files = PMD.getApplicableFiles(configuration, languages);

        long reportStart = System.nanoTime();
        try {
            renderer = configuration.createRenderer();
            List<Renderer> renderers = new LinkedList<>();
            renderers.add(renderer);
            renderer.start();

            Benchmarker.mark(Benchmark.Reporting, System.nanoTime() - reportStart, 0);

            RuleContext ctx = new RuleContext();

            PMD.processFiles(configuration, ruleSetFactory, files, ctx, renderers);

            reportStart = System.nanoTime();
            renderer.end();
        } catch (IOException e) {
            log.error("PMD analysis error", e);
        } finally {
            Benchmarker.mark(Benchmark.Reporting, System.nanoTime() - reportStart, 0);
        }
    }

    /**
     * Paste from PMD
     */
    private static Set<Language> getApplicableLanguages(PMDConfiguration configuration, RuleSets ruleSets) {
        Set<Language> languages = new HashSet<>();
        LanguageVersionDiscoverer discoverer = configuration.getLanguageVersionDiscoverer();

        for (Rule rule : ruleSets.getAllRules()) {
            Language language = rule.getLanguage();
            if (languages.contains(language))
                continue;
            LanguageVersion version = discoverer.getDefaultLanguageVersion(language);
            if (RuleSet.applies(rule, version)) {
                languages.add(language);
                log.debug("Using {} version: {}", language.getShortName(), version.getShortName());
            }
        }
        return languages;
    }
}
