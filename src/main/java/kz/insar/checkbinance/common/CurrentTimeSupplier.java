package kz.insar.checkbinance.common;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Supplier;

public class CurrentTimeSupplier implements Supplier<LocalDateTime> {

    private final Clock clock;

    public CurrentTimeSupplier(Clock clock) {
        if (clock == null) {
            throw new NullPointerException();
        }
        this.clock = clock;
    }

    public CurrentTimeSupplier() {
        this(Clock.systemUTC());
    }
    @Override
    public LocalDateTime get() {
        return LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    }
}
