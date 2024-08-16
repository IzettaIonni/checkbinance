package kz.insar.checkbinance.containers;

import kz.insar.checkbinance.client.RecentTradeDTO;
import kz.insar.checkbinance.helpers.symbol.TestSymbol;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Builder(toBuilder = true)
public class BNBLegacyLastPrice {
    @NonNull
    private final String symbol;
    private final Long time;
    private final BigDecimal price;
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

        public BNBLegacyLastPriceBuilder withRandomParams() {
            symbol = RandomStringUtils.random(32);
            time = ThreadLocalRandom.current().nextLong(999999999); //Caused by DateTime boundaries
            price = BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
            qty = BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
            quoteQty = BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
            isBuyerMaker = ThreadLocalRandom.current().nextBoolean();
            isBestMatch = ThreadLocalRandom.current().nextBoolean();
            return this;
        }
    }
}
