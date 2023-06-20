package kz.insar.checkbinance.client;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SymbolPriceDTO {
    private String symbol;
    private BigDecimal price;
}
