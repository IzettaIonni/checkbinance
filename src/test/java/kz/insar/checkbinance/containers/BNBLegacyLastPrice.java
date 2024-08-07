package kz.insar.checkbinance.containers;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;

@Getter
@Builder(toBuilder = true)
public class BNBLegacyLastPrice {
    @NonNull
    private final String symbol;
    private final Long time;
    private final Integer price;
    private final Long BinanceId;
    private final BigDecimal qty;
    private final BigDecimal quoteQty;
    private final Boolean isBuyerMaker;
    private final Boolean isBestMatch;

    public static class BNBLegacyLastPriceBuilder {
        public BNBLegacyLastPriceBuilder symbol(@NonNull String symbol) {
            if (symbol.length() > 64) throw new IllegalArgumentException("Too long symbol");
            this.symbol = symbol;
            return this;
        }
    }
}
