package kz.insar.checkbinance.tradeprice.binance;

import kz.insar.checkbinance.tradeprice.ITradePrice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
public class BinanceWebSocketTrade implements ITradePrice {

    private String eventType;
    private LocalDateTime time;
    private String symbol;
    private Long tradeId;
    private BigDecimal price;
    private BigDecimal quantity;
    private Boolean isBuyerMaker;
    private Boolean ignore;

}
