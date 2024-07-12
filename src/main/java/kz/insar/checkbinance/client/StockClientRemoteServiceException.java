package kz.insar.checkbinance.client;

public class StockClientRemoteServiceException extends StockClientException{
    public StockClientRemoteServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public StockClientRemoteServiceException(String message) {
        super(message);
    }

    public StockClientRemoteServiceException(Throwable cause) {
        super(cause);
    }

    public StockClientRemoteServiceException() {
    }
}
