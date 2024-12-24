package kz.insar.checkbinance.tradeprice.binance;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BinanceWebSocketConverterTest {

    BinanceWebSocketConverter service;

    @BeforeEach
    void setUp() {
        service = new BinanceWebSocketConverter();
    }
    
    @Test
    void testDeserializer() {
        String testString = "{\"stream\":\"btcusdt@trade\",\"data\":{\"e\":\"trade\",\"E\":1732636519372,\"s\":\"BTCUSDT\",\"t\":4156456888,\"p\":\"93028.00000000\",\"q\":\"0.00023000\",\"T\":1732636519372,\"m\":true,\"M\":true}}";

        var actual = service.toITradePrice(testString);

        assertEquals("BTCUSDT",actual.getItem());
        assertEquals(new BigDecimal("93028.00000000"),actual.getPrice());
        assertEquals(LocalDateTime.parse("2024-11-26T15:55:19.372") ,actual.getTime());
        assertEquals(new BigDecimal("0.00023000"),actual.getQuantity());
    }

}
