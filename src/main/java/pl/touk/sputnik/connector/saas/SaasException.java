package pl.touk.sputnik.connector.saas;

class SaasException extends RuntimeException {

    public SaasException(String message, Exception cause) {
        super(message, cause);
    }
}
