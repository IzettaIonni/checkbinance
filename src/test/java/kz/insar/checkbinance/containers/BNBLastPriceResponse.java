package kz.insar.checkbinance.containers;

import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.SymbolPriceDTO;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.helpers.symbol.TestSymbol;
import kz.insar.checkbinance.helpers.symbol.TestSymbolRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Builder(toBuilder = true)
public class BNBLastPriceResponse {
    @NonNull
    private final List<BNBLastPrice> prices;

    public List<String> getUniqueSymbol() {
        return new ArrayList<>(prices.stream().map(BNBLastPrice::getSymbol).collect(Collectors.toSet()));
    }

    public List<SymbolPriceDTO> toSymbolPriceDTO() {
        return this.getPrices().stream().map(BNBLastPrice::toSymbolPriceDTO).collect(Collectors.toList());
    }


    @Deprecated
    public List<LastPriceDTO> toLastPriceDTO(List<LastPriceDTO> actual) {
        List<LastPriceDTO> lastPriceDTOList = new ArrayList<>();
        for (int i = 0 ; i < prices.size(); i++) {
            var price = prices.get(i);
            var actualLastPrice = actual.get(i);
            lastPriceDTOList.add(
                    LastPriceDTO.builder().symbol(price.getSymbol()).price(price.getPrice()).id(actualLastPrice.getId()).time(actualLastPrice.getTime()).build()
            );
        }

        return lastPriceDTOList;
    }

    public List<LastPriceDTO> toLastPriceDTO(List<LastPriceDTO> actual, Function<String, SymbolId> symbolIdExtractor) {
        throw new UnsupportedOperationException();
    }

    public List<LastPriceDTO> toLastPriceDTO(List<LastPriceDTO> actual, TestSymbolRepository<?> symbolIdExtractor) {
        return toLastPriceDTO(actual, symbolIdExtractor::getSymbolId);
    }

    public static class BNBLastPriceResponseBuilder {
        private List<BNBLastPrice> prices = new ArrayList<BNBLastPrice>();

        public BNBLastPriceResponseBuilder addPrice(@NonNull BNBLastPrice price) {
            prices.add(price);
            return this;
        }

        public BNBLastPriceResponseBuilder addPrice(@NonNull String symbol, int price) {
            return addPrice(BNBLastPrice.of(symbol, price));
        }

        public BNBLastPriceResponseBuilder addPrice(@NonNull TestSymbol testSymbol, int price) {
            return addPrice(testSymbol.getName(), price);
        }

    }
}
