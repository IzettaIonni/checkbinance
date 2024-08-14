package kz.insar.checkbinance.containers;

import kz.insar.checkbinance.client.RecentTradeDTO;
import lombok.Builder;
import lombok.NonNull;
import org.bouncycastle.pqc.jcajce.provider.bike.BIKEKeyFactorySpi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder(toBuilder = true)
public class BNBLegacyLastPriceResponse {

    @NonNull
    private final List<BNBLegacyLastPrice> prices;
    private Supplier<Long> idGenerator;

    public List<RecentTradeDTO> toRecentTradeDTOs() {
        if (idGenerator == null) throw new IllegalStateException("No idGenerator given");
        return toRecentTradeDTOs(prices.stream().map((price) -> idGenerator.get()).collect(Collectors.toList()));
    }

    public List<RecentTradeDTO> toRecentTradeDTOs(List<Long> ids) {
        if (prices.size() != ids.size()) throw new IllegalArgumentException();
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

    }

}
