package kz.insar.checkbinance.client;

public class StockClientAccessException extends StockClientException{
    public StockClientAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public StockClientAccessException(String message) {
        super(message);
    }

    public StockClientAccessException(Throwable cause) {
        super(cause);
    }

    public StockClientAccessException() {
        super();
    }
}
