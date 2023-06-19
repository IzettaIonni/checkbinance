package kz.insar.checkbinance.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import kz.insar.checkbinance.services.TickerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;

import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureMockMvc
@SpringBootTest
class ClientTest {
    BinanceClient service;

    @BeforeEach
    void setUp() {
        service = new BinanceClient();
    }

    @Test
    @BeforeAll
    void testX() {
        given()
                .log().all()
                .accept(ContentType.JSON)
        .when()
                .get("https://api.binance.com/api/v3/exchangeInfo")
        .then()
                .log().all()
                .assertThat()
                .statusCode(200);
    }

    @Test
    void testGetExchangeInfo() {
        List<String> request_symbols = List.of("BNBBTC", "BTCUSDT");
        System.out.println(service.getExchangeInfoBySymbols(request_symbols));
    }

    @Test
    void testT() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> request_symbols = List.of("BNBBTC", "BTCUSDT");
        System.out.println(objectMapper.writeValueAsString(request_symbols));
    }

    @Test
    void testA() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(
                service.getRecentTrades("BNBBTC", 2)));
    }

    @Test
    void testB () throws NullPointerException {

    }
}