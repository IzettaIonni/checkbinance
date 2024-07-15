package kz.insar.checkbinance.client;

public class StockClientPartialSuccessException extends StockClientException{
    public StockClientPartialSuccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public StockClientPartialSuccessException(String message) {
        super(message);
    }

    public StockClientPartialSuccessException(Throwable cause) {
        super(cause);
    }

    public StockClientPartialSuccessException() {
        super();
    }
}
