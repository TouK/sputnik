package pl.touk.sputnik.connector.saas;

import pl.touk.sputnik.connector.http.HttpConnector;

import java.util.List;

public class SaasConnector {

    private final HttpConnector httpConnector;

    public SaasConnector(HttpConnector httpConnector) {
        this.httpConnector = httpConnector;
    }

    public List<String> getReviewFiles() {
        return null;
    }
}
