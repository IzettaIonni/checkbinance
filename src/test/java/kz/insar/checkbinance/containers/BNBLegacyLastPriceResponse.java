package kz.insar.checkbinance.containers;

import kz.insar.checkbinance.client.RecentTradeDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.bouncycastle.pqc.jcajce.provider.bike.BIKEKeyFactorySpi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder(toBuilder = true)
public class BNBLegacyLastPriceResponse {

    @NonNull
    @Getter
    private final List<BNBLegacyLastPrice> prices;
    @NonNull
    private final Supplier<Long> idGenerator;

    public List<RecentTradeDTO> toRecentTradeDTOs() {
        return toRecentTradeDTOs(prices.stream().map((price) -> idGenerator.get()).collect(Collectors.toList()));
    }

    private List<RecentTradeDTO> toRecentTradeDTOs(List<Long> ids) {
        List<RecentTradeDTO> recentTradeDTOs = new ArrayList<>();
        for (int i = 0; i < prices.size(); i++) {
            var price = prices.get(i);
            recentTradeDTOs.add(
                    RecentTradeDTO.builder()
                            .id(ids.get(i))
                            .price(price.getPrice())
                            .qty(price.getQty())
                            .quoteQty(price.getQuoteQty())
                            .time(price.getTime())
                            .isBuyerMaker(price.getIsBuyerMaker())
                            .isBestMatch(price.getIsBestMatch())
                            .build()
            );
        }
        return recentTradeDTOs;
    }

    public static BNBLegacyLastPriceResponse of(BNBLegacyLastPrice price) {
        return BNBLegacyLastPriceResponse.builder().addPrice(price).build();
    }

    public static BNBLegacyLastPriceResponse of(List<BNBLegacyLastPrice> prices) {
        return BNBLegacyLastPriceResponse.builder().addPrices(prices).build();
    }

    public static class BNBLegacyLastPriceResponseBuilder {
        private List<BNBLegacyLastPrice> prices = new ArrayList<>();

        private BNBLegacyLastPriceResponseBuilder prices(List<BNBLegacyLastPrice> prices) {
            return addPrices(prices);
        }

        public BNBLegacyLastPriceResponseBuilder addPrice(BNBLegacyLastPrice price) {
            prices.add(price);
            return this;
        }

        public BNBLegacyLastPriceResponseBuilder addPrices(List<BNBLegacyLastPrice> prices) {
            this.prices.addAll(prices);
            return this;
        }

        public BNBLegacyLastPriceResponseBuilder withIdGeneratorFromList(List<Long> ids) {
            idGenerator = () -> ids.iterator().next();
            return this;
        }

        public BNBLegacyLastPriceResponseBuilder withoutIds() {
            idGenerator = () -> null;
            return this;
        }

        public BNBLegacyLastPriceResponseBuilder witRandomIds() {
            idGenerator = () -> ThreadLocalRandom.current().nextLong();
            return this;
        }
    }

}
