package kz.insar.checkbinance.tradeprice.binance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.insar.checkbinance.tradeprice.ITradePrice;

public class BinanceWebSocketConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ITradePrice fromTrade(String trade) throws JsonProcessingException {
        BinanceWebSocketTrade bTrade = objectMapper.readValue(trade, BinanceWebSocketTrade.class);
        return bTrade;
    }

}
