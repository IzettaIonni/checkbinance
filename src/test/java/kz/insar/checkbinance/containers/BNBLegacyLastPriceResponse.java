package kz.insar.checkbinance.containers;

import com.google.common.collect.Iterables;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.RecentTradeDTO;
import kz.insar.checkbinance.common.EpochMilisToTimeConverter;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.helpers.CheckbinanceServiceHelper;
import kz.insar.checkbinance.helpers.symbol.TestSymbol;
import kz.insar.checkbinance.helpers.symbol.TestSymbolRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
public class BNBLegacyLastPriceResponse {

    @NonNull
    private final List<BNBLegacyLastPrice> prices;
    @NonNull
    private final Supplier<Long> idGenerator;
    private final Function<String, SymbolId> symbolIdExtractor;

    public Integer getPricesQuantity() {
        return prices.size();
    }

    public String getCommonSymbolName() {
        var name = prices.get(0).getSymbol();
        if (!prices.stream().map(BNBLegacyLastPrice::getSymbol).allMatch(name::equals))
            throw new IllegalStateException(); //todo some uncertainty exception
        return name;
    }

    public List<RecentTradeDTO> toRecentTradeDTOs() {
        return toRecentTradeDTOs(prices.stream().map((price) -> idGenerator.get()).collect(Collectors.toList()));
    }

    public List<RecentTradeDTO> toRecentTradeDTOs(List<Long> ids) {
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

    public List<LastPriceDTO> toLastPriceDTO(Function<String, SymbolId> symbolIdExtractor) {
        List<LastPriceDTO> lastPriceDTOList = new ArrayList<>();
        for (int i = 0; i < prices.size(); i++) {
            var price = prices.get(i);
            lastPriceDTOList.add(LastPriceDTO.builder()
                            .symbol(price.getSymbol())
                            .price(price.getPrice())
                            .id(symbolIdExtractor.apply(price.getSymbol()).getId())
                            .time(new EpochMilisToTimeConverter().apply(price.getTime())) //todo review time on prod
                            .build());
        }
        return lastPriceDTOList;
    }

    public List<LastPriceDTO> toLastPriceDTO(TestSymbolRepository<?> symbolIdExtractor) {
        return toLastPriceDTO(symbolIdExtractor::getSymbolId);
    }

    public List<LastPriceDTO> toLastPriceDTO() {
        return toLastPriceDTO(symbolIdExtractor);
    }

    public static BNBLegacyLastPriceResponse of(BNBLegacyLastPrice price) {
        return BNBLegacyLastPriceResponse.builder().addPrice(price).build();
    }

    public static BNBLegacyLastPriceResponse of(List<BNBLegacyLastPrice> prices) {
        return BNBLegacyLastPriceResponse.builder().addPrices(prices).build();
    }

    public static class BNBLegacyLastPriceResponseBuilder {

        private List<BNBLegacyLastPrice> prices = new ArrayList<>();


        public BNBLegacyLastPriceResponseBuilder addPrice(BNBLegacyLastPrice price) {
            prices.add(price);
            return this;
        }

        public BNBLegacyLastPriceResponseBuilder addRandomPrice() {
            return addPrice(BNBLegacyLastPrice.builder()
                    .symbol(RandomStringUtils.randomAlphabetic(32))
                    .time(ThreadLocalRandom.current().nextLong(999999999))
                    .price(BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)))
                    .qty(BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)))
                    .quoteQty(BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)))
                    .isBuyerMaker(ThreadLocalRandom.current().nextBoolean())
                    .isBestMatch(ThreadLocalRandom.current().nextBoolean())
                    .build());
        }

        public BNBLegacyLastPriceResponseBuilder addRandomPrice(String symbol) {
            return addPrice(BNBLegacyLastPrice.builder()
                    .symbol(symbol)
                    .time(ThreadLocalRandom.current().nextLong(999999999))
                    .price(BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)))
                    .qty(BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)))
                    .quoteQty(BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)))
                    .isBuyerMaker(ThreadLocalRandom.current().nextBoolean())
                    .isBestMatch(ThreadLocalRandom.current().nextBoolean())
                    .build());
        }

        public BNBLegacyLastPriceResponseBuilder addRandomPrice(TestSymbol symbol) {
            return addRandomPrice(symbol.getName());
        }

        public BNBLegacyLastPriceResponseBuilder addPrices(List<BNBLegacyLastPrice> prices) {
            this.prices.addAll(prices);
            return this;
        }

        public BNBLegacyLastPriceResponseBuilder symbolIdExtractor(Function<String, SymbolId> symbolIdExtractor) {
            this.symbolIdExtractor = symbolIdExtractor;
            return this;
        }

        public BNBLegacyLastPriceResponseBuilder symbolIdExtractor(TestSymbolRepository<?> symbolIdExtractor) {
            return symbolIdExtractor(symbolIdExtractor::getSymbolId);
        }

        public BNBLegacyLastPriceResponseBuilder idGenerator(Supplier<Long> idGenerator) {
            this.idGenerator = idGenerator;
            return this;
        }

        public BNBLegacyLastPriceResponseBuilder idGenerator(CheckbinanceServiceHelper idGenerator) {
            return idGenerator(() -> idGenerator.createBinanceTradeId().getLastTradeId());
        }

        public BNBLegacyLastPriceResponseBuilder withRandomIdGenerator() {
            idGenerator = () -> ThreadLocalRandom.current().nextLong();
            return this;
        }
    }

}
