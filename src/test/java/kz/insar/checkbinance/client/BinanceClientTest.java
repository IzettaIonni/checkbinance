package kz.insar.checkbinance.client;

import kz.insar.checkbinance.containers.ContainerHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@AutoConfigureMockMvc
@Disabled
@SpringBootTest
//todo refactor
class BinanceClientTest {
    BinanceClient service;

    private final PostgreSQLContainer<?> postgreSQL = ContainerHolder.getPostgreSQL();

    @BeforeEach
    void setUp() {
        service = new BinanceClient("https://api.binance.com/api/v3");
    }

    @Test
    void test() {
        URI requestUri = UriComponentsBuilder.fromHttpUrl("https://api.binance.com/api/v3")
                .queryParam("symbols", "1")
                .pathSegment("exchangeInfo")
                .build()
                .toUri();
        System.out.println(requestUri);
    }

    @Test
    void testGetExchangeInfo() {
        List<String> request_symbols = List.of("BNBBTC", "BTCUSDT");
        System.out.println(service.getExchangeInfoBySymbols(request_symbols));
    }

    @Test
    void testSymbolPrice() {
        System.out.println(service.getPrice("ETHBTC"));
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