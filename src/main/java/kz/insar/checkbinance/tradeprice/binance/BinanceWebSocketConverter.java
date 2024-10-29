package kz.insar.checkbinance.tradeprice.binance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.insar.checkbinance.tradeprice.ITradePrice;
import lombok.SneakyThrows;

public class BinanceWebSocketConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public ITradePrice toITradePrice(String trade) {
        return objectMapper.readValue(trade, BinanceWebSocketTrade.class);
    }

}
