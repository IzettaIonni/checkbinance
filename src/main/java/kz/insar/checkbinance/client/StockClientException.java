package kz.insar.checkbinance.client;

public class StockClientException extends RuntimeException {
    public StockClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public StockClientException(String message) {
        super(message, null);
    }

    public StockClientException(Throwable cause) {
        super(null, cause);
    }

    public StockClientException() {
        super(null, null);
    }
}
