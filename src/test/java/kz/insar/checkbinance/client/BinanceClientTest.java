package kz.insar.checkbinance.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@AutoConfigureMockMvc
@SpringBootTest
class BinanceClientTest {
    BinanceClient service;

    @BeforeEach
    void setUp() {
        service = new BinanceClient();
    }


    @Test
    void testGetExchangeInfo() {
        List<String> request_symbols = List.of("BNBBTC", "BTCUSDT");
        System.out.println(service.getExchangeInfoBySymbols(request_symbols));
    }

    @Test
    void testSymbolPrice() {
        System.out.println(service.getPrice("LTCBTC"));
    }

    @Test
    void testSymbolPricesWithoutParams() {
        System.out.println(service.getPrices());
    }

    @Test
    void testSymbolPricesWithParams() {
        List<String> symbols = new ArrayList<>();
        symbols.add("BNBBTC");
        symbols.add("LTCBTC");
        System.out.println(service.getPrices(symbols));
    }
}