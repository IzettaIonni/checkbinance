package kz.insar.checkbinance.domain.exeptions;

public class CheckBinanceException extends RuntimeException{
    public CheckBinanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckBinanceException(String message) {
        this(message, null);
    }

    public CheckBinanceException(Throwable cause) {
        this(null, cause);
    }

    public CheckBinanceException() {
        this(null, null);
    }

}
