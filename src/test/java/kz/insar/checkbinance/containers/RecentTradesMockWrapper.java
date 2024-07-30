package kz.insar.checkbinance.containers;

import kz.insar.checkbinance.client.RecentTradeDTO;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class RecentTradesMockWrapper {
    private Long id;
    private BigDecimal price;
    private BigDecimal qty;
    private BigDecimal quoteQty;
    private Long time;
    private Boolean isBuyerMaker;
    private Boolean isBestMatch;

    public RecentTradeDTO toRecentTradeDTO() {
        return RecentTradeDTO.builder()
                .id(id)
                .price(price)
                .qty(qty)
                .quoteQty(quoteQty)
                .time(time)
                .isBuyerMaker(isBuyerMaker)
                .isBestMatch(isBestMatch)
                .build();
    }
}
