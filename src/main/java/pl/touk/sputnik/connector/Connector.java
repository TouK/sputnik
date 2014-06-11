package pl.touk.sputnik.connector;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;

public interface Connector {

    @NotNull
    String listFiles() throws URISyntaxException, IOException;

    @NotNull
    String sendReview(String reviewInputAsJson) throws URISyntaxException, IOException;
}
