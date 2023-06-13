package kz.insar.checkbinance.domain.exeptions;

public class ObjectNotFoundException extends CheckBinanceException{
    public ObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectNotFoundException(String message) {
        this(message, null);
    }

    public ObjectNotFoundException(Throwable cause) {
        this(null, cause);
    }

    public ObjectNotFoundException() {
        this(null, null);
    }
}
