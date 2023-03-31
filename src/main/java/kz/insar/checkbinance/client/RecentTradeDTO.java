package kz.insar.checkbinance.client;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecentTradeDTO {
    private Long id;
    private BigDecimal price;
    private BigDecimal qty;
    private BigDecimal quoteQty;
    private Long time;
    private Boolean isBuyerMaker;
    private Boolean isBestMatch;
}