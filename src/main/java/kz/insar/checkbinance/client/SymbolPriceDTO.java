package kz.insar.checkbinance.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SymbolPriceDTO {
    private String symbol;
    private BigDecimal price;

    public static class SymbolPriceDTOBuilder {
        public SymbolPriceDTOBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public SymbolPriceDTOBuilder price(String price) {
            return price(new BigDecimal(price));
        }

        public SymbolPriceDTOBuilder price(long price) {
            return price(BigDecimal.valueOf(price));
        }
    }
}
