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
public class RecentTradeDTO {
    private Long id;
    private BigDecimal price;
    private BigDecimal qty;
    private BigDecimal quoteQty;
    private Long time;
    private Boolean isBuyerMaker;
    private Boolean isBestMatch;
}