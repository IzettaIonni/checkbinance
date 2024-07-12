package kz.insar.checkbinance.client;

public class StockClientNotFoundException extends StockClientException{
    public StockClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public StockClientNotFoundException(String message) {
        super(message);
    }

    public StockClientNotFoundException(Throwable cause) {
        super(cause);
    }

    public StockClientNotFoundException() {
        super();
    }
}
