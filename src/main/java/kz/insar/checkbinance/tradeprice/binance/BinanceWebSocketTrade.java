package kz.insar.checkbinance.tradeprice.binance;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.insar.checkbinance.tradeprice.ITradePrice;
import kz.insar.checkbinance.tradeprice.binance.util.BinanceTradeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@JsonDeserialize(using = BinanceTradeDeserializer.class)
public class BinanceWebSocketTrade implements ITradePrice {

    private LocalDateTime time;
    private String symbol;
    private BigDecimal price;
    private BigDecimal quantity;

    @Override
    public String getItem() {
        return symbol;
    }
}
