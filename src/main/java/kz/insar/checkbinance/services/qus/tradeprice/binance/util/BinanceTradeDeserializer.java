package kz.insar.checkbinance.services.qus.tradeprice.binance.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import kz.insar.checkbinance.services.qus.tradeprice.binance.BinanceWebSocketTrade;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class BinanceTradeDeserializer extends StdDeserializer<BinanceWebSocketTrade> {

    public BinanceTradeDeserializer() {
        this(null);
    }

    public BinanceTradeDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public BinanceWebSocketTrade deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException { //todo try to use final types of values when tests are be ready
        String time;
        String symbol;
        String price;
        String quantity;
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
//todo {"stream":"btcusdt@trade","data":{"e":"trade","E":1732636519372,"s":"BTCUSDT","t":4156456888,"p":"93028.00000000","q":"0.00023000","T":1732636519372,"m":true,"M":true}}

        // Extract and map fields
        time = node.get("E").asText();
        symbol = node.get("s").asText();
        price = node.get("p").asText();
        quantity = node.get("q").asText();

        return new BinanceWebSocketTrade(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(time)), ZoneId.of("UTC")),
                symbol,
                BigDecimal.valueOf(Long.parseLong(price)),
                BigDecimal.valueOf(Long.parseLong(quantity)));
    }
}
