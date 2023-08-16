package kz.insar.checkbinance.controllers;

import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.RecentTradeDTO;
import kz.insar.checkbinance.client.SymbolPriceDTO;
import kz.insar.checkbinance.client.SymbolStatus;
import kz.insar.checkbinance.containers.BinanceAPIHelper;
import kz.insar.checkbinance.containers.ContainerHolder;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolCreate;
import kz.insar.checkbinance.services.SymbolService;
import kz.insar.checkbinance.services.impl.TickerServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
@ExtendWith(ContainerHolder.class)
public class TickerControllerITRESTAssuredIT {

    @Autowired
    private SymbolService symbolService;
    @Autowired
    private TickerServiceImpl tickerService;
    @Autowired
    private MockMvc mvc;

    private static BinanceAPIHelper binanceAPIHelper;

    private Symbol createSymbol(String name) {
        return symbolService.createSymbol(SymbolCreate.builder()
                .quotePrecision(567)
                .quoteAsset("asd")
                .baseAsset("sad")
                .name(name)
                .baseAssetPrecision(5678)
                .quoteAssetPrecision(77)
                .status(SymbolStatus.POST_TRADING)
                .build());
    }

    @BeforeAll
    static void beforeClass() {
        binanceAPIHelper = ContainerHolder.getBinanceAPIHelper();
    }

    @BeforeEach
    void setUp() {
        mockMvc(mvc);
    }

    @AfterEach
    void tearDown() {
        binanceAPIHelper.cleanUp();
    }

    @Test
    void testTickerLastPrice_shouldReturnPricesIfOK() {
        binanceAPIHelper.mockRequestTickerPrice(
                List.of("CHZBNB", "BEAMUSDT"),
                List.of(SymbolPriceDTO.builder().price(12).symbol("CHZBNB").build(),
                        SymbolPriceDTO.builder().price(26).symbol("BEAMUSDT").build())
        );

        var symbolOne = createSymbol("CHZBNB");
        var symbolTwo = createSymbol("BEAMUSDT");
        tickerService.subscribeOnPrice(symbolOne);
        tickerService.subscribeOnPrice(symbolTwo);

        given()
                .params("sortKey", "ID", "sortDir", "DESC")

                .when()
                .get("/ticker/lastprice")

                .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.OK)
                .contentType("application/json");
    }

    @Test
    void testTickerLastPrice_shouldReturnExceptionIfSymbolNotFound() {
        binanceAPIHelper.mockRequestTickerPrice(
                List.of("CHZBNB", "BEAMUSDT"), response().withStatusCode(404));

        var symbolOne = createSymbol("CHZBNB");
        var symbolTwo = createSymbol("BEAMUSDT");
        tickerService.subscribeOnPrice(symbolOne);
        tickerService.subscribeOnPrice(symbolTwo);

        given()
                .params("sortKey", "ID", "sortDir", "DESC")

        .when()
                .get("/ticker/lastprice")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.NOT_FOUND);
    }

    @Test
    void testLastPrice_shouldReturnNullIfSubscriptionsDoesNotExist() {
        given()
                .params("sortKey", "ID", "sortDir", "DESC")

        .when()
                .get("/ticker/lastprice")

        .then()
                .assertThat()
                .status(HttpStatus.OK)
                .extract().body().equals(null);
    }

    @Test
    void testLegacyLastPrice_shouldReturnPricesIfOK() {
        binanceAPIHelper.mockRequestTickerPrice(
                List.of("CHZBNB", "BEAMUSDT"),
                List.of(
                        SymbolPriceDTO.builder().price(12).symbol("CHZBNB").build(),
                        SymbolPriceDTO.builder().price(26).symbol("BEAMUSDT").build()
                )
        );

        var symbolOne = createSymbol("CHZBNB");
        var symbolTwo = createSymbol("BEAMUSDT");
        tickerService.subscribeOnPrice(symbolOne);
        tickerService.subscribeOnPrice(symbolTwo);

        List<LastPriceDTO> actual = given()
                .params("sortKey", "ID", "sortDir", "DESC")
//                .log().all()

        .when()
                .get("/ticker/legacylastprice")

        .then()
                .log().all() //log() works here
                .assertThat()
                .status(HttpStatus.OK)
                .contentType("application/json")
                .body("[0].symbol", equalTo(symbolTwo.getName()))
                .body("[0].id", equalTo(symbolTwo.getId().getId()))
                .body("[1].symbol", equalTo(symbolOne.getName()))
                .body("[1].id", equalTo(symbolOne.getId().getId()))
                //.extract().as(new TypeRef<List<LastPriceDTO>>(){});
                .extract().body()
                // here's the magic
                .jsonPath().getList(".", LastPriceDTO.class);
        List<LastPriceDTO> expected = List.of(
//                LastPriceDTO.builder()
//                        .symbol("BEAMUSDT")
//                        .id(symbolTwo.getId().getId())
//                        .price(26)
//                        .time()
        );
    }

    @Test
    void testLegacyLastPrice_shouldReturnExceptionIfSymbolNotFound() {

    }

    @Test
    void testLegacyLastPrice_shouldReturnNullIfSubscriptionsDoesNotExist() {
        given()
                .params("sortKey", "ID", "sortDir", "DESC")

        .when()
                .get("/ticker/legacylastprice")

        .then()
                .assertThat()
                .status(HttpStatus.OK)
                .extract().body().equals(null);
    }

    @Test
    void testExchangeInfo() {
        given()
                .param("symbols", asList("CHZBNB", "BEAMUSDT"))
//                .log().all()

            .when()
                .get("/ticker/exchangeinfo")

            .then()
                .log().all() //log() works here
                .assertThat()
                .status(HttpStatus.OK)
                .contentType("application/json")
                .body("serverTime", notNullValue())
                .body("symbols[0].symbol", anyOf(equalTo("CHZBNB"), equalTo("BEAMUSDT")))
                .body("symbols[0].status", notNullValue())
                .body("symbols[0].baseAsset", notNullValue())
                .body("symbols[0].baseAssetPrecision", notNullValue())
                .body("symbols[0].quoteAsset", notNullValue())
                .body("symbols[0].quotePrecision", notNullValue())
                .body("symbols[0].quoteAssetPrecision", notNullValue())
                .body("symbols[1].symbol", anyOf(equalTo("CHZBNB"), equalTo("BEAMUSDT")))
                .body("symbols[1].status", notNullValue())
                .body("symbols[1].baseAsset", notNullValue())
                .body("symbols[1].baseAssetPrecision", notNullValue())
                .body("symbols[1].quoteAsset", notNullValue())
                .body("symbols[1].quotePrecision", notNullValue())
                .body("symbols[1].quoteAssetPrecision", notNullValue());
    }

    @Test
    void testExchangeAllInfo() {
        given()
//                .log().all()
                .params("sortKey", "ID", "sortDir", "DESC")

        .when()
                .get("/ticker/exchangeallinfo")

        .then()
                .log().all() //log() works here
                .assertThat()
                .status(HttpStatus.OK)
                .contentType("application/json")
                .body("serverTime", notNullValue())
                .body("symbols[0].symbol", notNullValue())
                .body("symbols[0].status", notNullValue())
                .body("symbols[0].baseAsset", notNullValue())
                .body("symbols[0].baseAssetPrecision", notNullValue())
                .body("symbols[0].quoteAsset", notNullValue())
                .body("symbols[0].quotePrecision", notNullValue())
                .body("symbols[0].quoteAssetPrecision", notNullValue());
    }

    @Test
    void testSubscribeTicker() {
        var symbolOne = createSymbol("CHZBNB");
        given()
                .params("name", symbolOne.getName())


        .when()
                .get("/ticker/subscribeticker")


        .then()
                .assertThat().status(HttpStatus.OK);
        var expected = List.of(symbolOne);
        var actual = tickerService.listSubscriptionOnPrices();
        assertEquals(expected, actual);
    }

    @Test
    void testSubscribeTicker_BadRequestException() {
        given()

        .when()
                .get("/ticker/subscribeticker")

        .then()
                .assertThat().status(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testUnsubscribeTicker() {
        var symbolOne = createSymbol("CHZBNB");
        var symbolTwo = createSymbol("BEAMUSDT");
        tickerService.subscribeOnPrice(symbolOne);
        tickerService.subscribeOnPrice(symbolTwo);
        given()
                .params("name", symbolOne.getName())


        .when()
                .get("/ticker/unsubscribeticker")


        .then()
                .assertThat().status(HttpStatus.OK);
        var expected = List.of(symbolTwo);
        var actual = tickerService.listSubscriptionOnPrices();
        assertEquals(expected, actual);
    }

    @Test
    void testUnsubscribeTicker_BadRequestException() {
        given()

        .when()
                .get("/ticker/unsubscribeticker")

        .then()
                .assertThat().status(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testSubscriptions() {
        var symbolOne = createSymbol("BTCBNB");
        var symbolTwo = createSymbol("BTCETH");
        tickerService.subscribeOnPrice(symbolOne);
        tickerService.subscribeOnPrice(symbolTwo);
        given()


        .when()
                .get("/ticker/subscriptions")


        .then()
                .assertThat().status(HttpStatus.OK);
        var expected = List.of(symbolOne, symbolTwo);
        var actual = tickerService.listSubscriptionOnPrices();
        assertEquals(expected, actual);
    }

}
