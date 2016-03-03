package pl.touk.sputnik.connector.http;

import org.apache.http.HttpResponse;

public class HttpException extends RuntimeException {

    private final HttpResponse response;

    public HttpException(HttpResponse response) {
        this.response = response;
    }

    public int getStatusCode() {
        return response.getStatusLine().getStatusCode();
    }

    public String getMessage() {
        return "Response status [" + getStatusCode() + "]";
    }
}
