package kz.insar.checkbinance.client;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
        String[] request_symbols = {"BNBBTC", "BTCUSDT"};
        System.out.println(service.getExchangeInfoBySymbols(request_symbols));
    }
}