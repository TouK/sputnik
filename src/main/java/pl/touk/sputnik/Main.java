package pl.touk.sputnik;

import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.checkstyle.CheckstyleProcessor;
import pl.touk.sputnik.gerrit.GerritFacade;
import pl.touk.sputnik.pmd.PmdProcessor;

import static org.apache.commons.lang3.Validate.notBlank;

public class Main {
    private Main() {}

    public static void main(String[] args) {
        Configuration.instance().init();

        String changeId = Configuration.instance().getProperty(GerritFacade.GERRIT_CHANGEID);
        String revisionId = Configuration.instance().getProperty(GerritFacade.GERRIT_REVISIONID);
        notBlank(changeId, "You must provide non blank Gerrit change Id");
        notBlank(revisionId, "You must provide non blank Gerrit revision Id");

        GerritFacade gerritFacade = createGerritFacade();
        Review review = new Review(gerritFacade.listFiles(changeId, revisionId));

        CheckstyleProcessor checkstyleProcessor = new CheckstyleProcessor();
        checkstyleProcessor.process(review);

        PmdProcessor pmdProcessor = new PmdProcessor();
        pmdProcessor.process(review);

//        gerritFacade.setReview(changeId, revisionId, review.toReviewInput());
    }

    @NotNull
    private static GerritFacade createGerritFacade() {
        String host = Configuration.instance().getProperty(GerritFacade.GERRIT_HOST);
        String port = Configuration.instance().getProperty(GerritFacade.GERRIT_PORT);
        String username = Configuration.instance().getProperty(GerritFacade.GERRIT_USERNAME);
        String password = Configuration.instance().getProperty(GerritFacade.GERRIT_PASSWORD);

        notBlank(host, "You must provide non blank Gerrit host");
        notBlank(port, "You must provide non blank Gerrit port");
        notBlank(username, "You must provide non blank Gerrit username");
        notBlank(password, "You must provide non blank Gerrit password");

        return new GerritFacade(host, Integer.valueOf(port), username, password);
    }
}
