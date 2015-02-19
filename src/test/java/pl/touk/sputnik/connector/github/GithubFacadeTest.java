package pl.touk.sputnik.connector.github;

import com.jcabi.github.*;
import com.jcabi.http.wire.RetryWire;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

@Slf4j
@Ignore
public class GithubFacadeTest {

    @Test
    public void shouldAddCommentToGithub() throws Exception {

        String oAuthKey = "???";
        Github github = new RtGithub(
                new RtGithub(oAuthKey)
                        .entry()
                        .through(RetryWire.class)
        );

        Repo repo = github.repos().get(new Coordinates.Simple("zygm0nt/sputnik"));
        Pull pull = repo.pulls().get(2);

        try {
            pull.comments().post("Wrong indentation",
                    "460c8ee5124f5b3cea5d175c20222c7e0a6b4e79",
                    "src/main/java/pl/touk/sputnik/connector/gerrit/GerritFacadeBuilder.java", 1);
        } catch (IOException e) {
            log.error("Error adding comment", e);
        }
    }
}
