package pl.touk.sputnik.processor.pmd;

import com.google.common.base.Joiner;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.datasource.DataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.touk.sputnik.Configuration;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PmdProcessor implements ReviewProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(PmdProcessor.class);
    private static final String SOURCE_NAME = "PMD";
    private static final String PMD_RULESETS = "pmd.ruleSets";
    private static final char PMD_INPUT_PATH_SEPARATOR = ',';
    private Renderer renderer;

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review) {
        try {
            PMDConfiguration configuration = new PMDConfiguration();
            configuration.setReportFormat("pl.touk.sputnik.pmd.CollectorRenderer");
            configuration.setRuleSets(getRulesets());
            configuration.setInputPaths(Joiner.on(PMD_INPUT_PATH_SEPARATOR).join(review.getIOFilenames()));
            doPMD(configuration);
        } catch (Throwable e) {
            LOG.error("PMD processor error", e);
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
        String ruleSets = Configuration.instance().getProperty(PMD_RULESETS);
        LOG.info("Using PMD rulesets {}", ruleSets);
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
            List<Renderer> renderers = new LinkedList<Renderer>();
            renderers.add(renderer);
            renderer.start();

            Benchmarker.mark(Benchmark.Reporting, System.nanoTime() - reportStart, 0);

            RuleContext ctx = new RuleContext();

            PMD.processFiles(configuration, ruleSetFactory, files, ctx, renderers);

            reportStart = System.nanoTime();
            renderer.end();
        } catch (IOException e) {
            LOG.error("PMD analysis error", e);
        } finally {
            Benchmarker.mark(Benchmark.Reporting, System.nanoTime() - reportStart, 0);
        }
    }

    /**
     * Paste from PMD
     */
    private static Set<Language> getApplicableLanguages(PMDConfiguration configuration, RuleSets ruleSets) {
        Set<Language> languages = new HashSet<Language>();
        LanguageVersionDiscoverer discoverer = configuration.getLanguageVersionDiscoverer();

        for (Rule rule : ruleSets.getAllRules()) {
            Language language = rule.getLanguage();
            if (languages.contains(language))
                continue;
            LanguageVersion version = discoverer.getDefaultLanguageVersion(language);
            if (RuleSet.applies(rule, version)) {
                languages.add(language);
                LOG.debug("Using " + language.getShortName() + " version: " + version.getShortName());
            }
        }
        return languages;
    }
}
