package kz.insar.checkbinance.client;

public class BinanceClientExeption extends RuntimeException {
    public BinanceClientExeption(String message, Throwable cause) {
        super(message, cause);
    }

    public BinanceClientExeption(String message) {
        super(message, null);
    }

    public BinanceClientExeption(Throwable cause) {
        super(null, cause);
    }

    public BinanceClientExeption() {
        super(null, null);
    }
}
