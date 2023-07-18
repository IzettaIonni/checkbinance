package kz.insar.checkbinance.common;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.LongFunction;

public class EpochMilisToTimeConverter implements LongFunction<LocalDateTime> {

    private final ZoneOffset zoneOffset;

    public EpochMilisToTimeConverter(ZoneOffset zoneId) {

        if (zoneId == null) {
            throw new NullPointerException();
        }
        this.zoneOffset = zoneId;
    }

    public EpochMilisToTimeConverter() {
        this(ZoneOffset.UTC);
    }

    @Override
    public LocalDateTime apply(long value) {
        return LocalDateTime.ofEpochSecond(
                value/1000 , (int) (value % 1000) * 1000000, zoneOffset);
    }
}
