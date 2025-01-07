package kz.insar.checkbinance.tradeprice.binance;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.insar.checkbinance.tradeprice.ITradePrice;
import kz.insar.checkbinance.tradeprice.binance.util.BinanceTradeDeserializer;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@JsonDeserialize(using = BinanceTradeDeserializer.class)
@EqualsAndHashCode
public class BinanceWebSocketTrade implements ITradePrice {
    //todo проблема сдесь, строка которая прилетает сюда неправильно десереализуется
//todo {"stream":"btcusdt@trade","data":{"e":"trade","E":1732636519372,"s":"BTCUSDT","t":4156456888,"p":"93028.00000000","q":"0.00023000","T":1732636519372,"m":true,"M":true}}

    private LocalDateTime time;
    private String symbol;
    private BigDecimal price;
    private BigDecimal quantity;

    @Override
    public String getItem() {
        return symbol;
    }
}
