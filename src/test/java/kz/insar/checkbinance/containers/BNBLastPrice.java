package kz.insar.checkbinance.containers;

import kz.insar.checkbinance.client.SymbolPriceDTO;
import kz.insar.checkbinance.helpers.symbol.TestSymbol;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;

@Getter
@Builder(toBuilder = true)
public class BNBLastPrice {
    @NonNull
    private final String symbol;
    private final Integer price;

    public SymbolPriceDTO toSymbolPriceDTO() {
        return SymbolPriceDTO.builder().symbol(symbol).price(BigDecimal.valueOf(price)).build();
    }

    public static BNBLastPrice of(String symbol, int price) {
        return BNBLastPrice.builder().symbol(symbol).price(price).build();
    }

    public static BNBLastPrice of(TestSymbol testSymbol, int price) {
        return of(testSymbol.getName(), price);
    }

    public static BNBLastPrice ofNullPrice(String symbol) {
        return BNBLastPrice.builder().symbol(symbol).build();
    }


    public static class BNBLastPriceBuilder {
        public BNBLastPriceBuilder symbol(@NonNull String symbol) {
            if (symbol.length() > 64) throw new IllegalArgumentException("Too long symbol");
            this.symbol = symbol;
            return this;
        }
    }
}
