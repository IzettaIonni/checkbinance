package kz.insar.checkbinance.client;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
public class SymbolPriceDTO {
    private String symbol;
    private BigDecimal price;
}
