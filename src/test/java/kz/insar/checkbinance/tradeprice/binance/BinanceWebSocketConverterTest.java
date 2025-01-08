package kz.insar.checkbinance.tradeprice.binance;

import kz.insar.checkbinance.services.qus.tradeprice.binance.BinanceWebSocketConverter;
import kz.insar.checkbinance.services.qus.tradeprice.binance.BinanceWebSocketTrade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BinanceWebSocketConverterTest {

    BinanceWebSocketConverter service;

    @BeforeEach
    void setUp() {
        service = new BinanceWebSocketConverter();
    }
    
    @Test
    void testDeserializer() {
        String symbol = "BTCUSDT";
        Long time = 1732636519372L;
        String price = "93028.00000000";
        String quantity = "0.00023000";
        String testString = "{\"stream\":\"btcusdt@trade\",\"data\":{\"e\":\"trade\",\"E\":1732636519372,\"s\":\""+ symbol + "\",\"t\":4156456888,\"p\":\"" + price + "\",\"q\":\"" + quantity +"\",\"T\":" + time + ",\"m\":true,\"M\":true}}";

        var actual = service.toITradePrice(testString);

        var expected = new BinanceWebSocketTrade(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneOffset.UTC),
                symbol, new BigDecimal(price), new BigDecimal(quantity));
        assertEquals(expected, actual);
    }

}
