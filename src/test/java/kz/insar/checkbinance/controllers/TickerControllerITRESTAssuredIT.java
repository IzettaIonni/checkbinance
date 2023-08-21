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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.LongFunction;
import java.util.function.Supplier;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private static LocalDateTime T(String time) {
        return LocalDateTime.parse(time);
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
        binanceAPIHelper.mockRequestLastPrice(
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
        binanceAPIHelper.mockRequestLastPrice(
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
    void testLegacyLastPrice_shouldReturnPricesIfOKWithLimit() {
        binanceAPIHelper.mockRequestLegacyLastPrice(
                "CHZBNB",
                2,
                List.of(
                        RecentTradeDTO.builder()
                                .id(51l)
                                .price(BigDecimal.valueOf(12))
                                .qty(BigDecimal.valueOf(44))
                                .quoteQty(BigDecimal.valueOf(76))
                                .time(1692271257000l)
                                .isBuyerMaker(true)
                                .isBestMatch(false)
                                .build(),
                        RecentTradeDTO.builder()
                                .id(52l)
                                .price(BigDecimal.valueOf(71))
                                .qty(BigDecimal.valueOf(47))
                                .quoteQty(BigDecimal.valueOf(70))
                                .time(1692277878000l)
                                .isBuyerMaker(false)
                                .isBestMatch(false)
                                .build())
        );
        binanceAPIHelper.mockRequestLegacyLastPrice(
                "BEAMUSDT",
                2,
                List.of(
                        RecentTradeDTO.builder()
                                .id(47l)
                                .price(BigDecimal.valueOf(89))
                                .qty(BigDecimal.valueOf(21))
                                .quoteQty(BigDecimal.valueOf(66))
                                .time(1692274412000l)
                                .isBuyerMaker(false)
                                .isBestMatch(true)
                                .build(),
                        RecentTradeDTO.builder()

                                .id(45l)
                                .price(BigDecimal.valueOf(33))
                                .qty(BigDecimal.valueOf(76))
                                .quoteQty(BigDecimal.valueOf(12))
                                .time(1692279824000l)
                                .isBuyerMaker(true)
                                .isBestMatch(true)
                                .build())
        );

        var symbolOne = createSymbol("CHZBNB");
        var symbolTwo = createSymbol("BEAMUSDT");
        tickerService.subscribeOnPrice(symbolOne);
        tickerService.subscribeOnPrice(symbolTwo);

        List<LastPriceDTO> actual = given()
                .param("sortKey", "ID")
                .param("sortDir", "DESC")
                .param("limit", "2")

        .when()
                .get("/ticker/legacylastprice")

        .then()
                .log().all() //log() works here
                .assertThat()
                .status(HttpStatus.OK)
                .contentType("application/json")
                .extract().body()
                // here's the magic
                .jsonPath().getList(".", LastPriceDTO.class);

        List<LastPriceDTO> expected = List.of(
                LastPriceDTO.builder()
                        .symbol("BEAMUSDT")
                        .id(symbolTwo.getId().getId())
                        .price(89)
                        .time(T("2023-08-17T12:13:32.000"))
                        .build(),
                LastPriceDTO.builder()
                        .symbol("BEAMUSDT")
                        .id(symbolTwo.getId().getId())
                        .price(33)
                        .time(T("2023-08-17T13:43:44.000"))
                        .build(),
                LastPriceDTO.builder()
                        .symbol("CHZBNB")
                        .id(symbolOne.getId().getId())
                        .price(12)
                        .time(T("2023-08-17T11:20:57.000"))
                        .build(),
                LastPriceDTO.builder()
                        .symbol("CHZBNB")
                        .id(symbolOne.getId().getId())
                        .price(71)
                        .time(T("2023-08-17T13:11:18.000"))
                        .build()
        );
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void testLegacyLastPrice_shouldReturnPricesIfOKWithoutLimit() {
        binanceAPIHelper.mockRequestLegacyLastPrice(
                "CHZBNB",
                1,
                List.of(
                        RecentTradeDTO.builder()
                                .id(51l)
                                .price(BigDecimal.valueOf(12))
                                .qty(BigDecimal.valueOf(44))
                                .quoteQty(BigDecimal.valueOf(76))
                                .time(1692271257000l)
                                .isBuyerMaker(true)
                                .isBestMatch(false)
                                .build(),
                        RecentTradeDTO.builder()
                                .id(52l)
                                .price(BigDecimal.valueOf(71))
                                .qty(BigDecimal.valueOf(47))
                                .quoteQty(BigDecimal.valueOf(70))
                                .time(1692277878000l)
                                .isBuyerMaker(false)
                                .isBestMatch(false)
                                .build())
        );
        binanceAPIHelper.mockRequestLegacyLastPrice(
                "BEAMUSDT",
                2,
                List.of(
                        RecentTradeDTO.builder()
                                .id(47l)
                                .price(BigDecimal.valueOf(89))
                                .qty(BigDecimal.valueOf(21))
                                .quoteQty(BigDecimal.valueOf(66))
                                .time(1692274412000l)
                                .isBuyerMaker(false)
                                .isBestMatch(true)
                                .build(),
                        RecentTradeDTO.builder()

                                .id(45l)
                                .price(BigDecimal.valueOf(33))
                                .qty(BigDecimal.valueOf(76))
                                .quoteQty(BigDecimal.valueOf(12))
                                .time(1692279824000l)
                                .isBuyerMaker(true)
                                .isBestMatch(true)
                                .build())
        );

        var symbolOne = createSymbol("CHZBNB");
        var symbolTwo = createSymbol("BEAMUSDT");
        tickerService.subscribeOnPrice(symbolOne);
        tickerService.subscribeOnPrice(symbolTwo);

        List<LastPriceDTO> actual = given()
                .param("sortKey", "ID")
                .param("sortDir", "DESC")
                .param("limit", "2")

                .when()
                .get("/ticker/legacylastprice")

                .then()
                .log().all() //log() works here
                .assertThat()
                .status(HttpStatus.OK)
                .contentType("application/json")
                .extract().body()
                // here's the magic
                .jsonPath().getList(".", LastPriceDTO.class);

        List<LastPriceDTO> expected = List.of(
                LastPriceDTO.builder()
                        .symbol("BEAMUSDT")
                        .id(symbolTwo.getId().getId())
                        .price(89)
                        .time(T("2023-08-17T12:13:32.000"))
                        .build(),
                LastPriceDTO.builder()
                        .symbol("BEAMUSDT")
                        .id(symbolTwo.getId().getId())
                        .price(33)
                        .time(T("2023-08-17T13:43:44.000"))
                        .build(),
                LastPriceDTO.builder()
                        .symbol("CHZBNB")
                        .id(symbolOne.getId().getId())
                        .price(12)
                        .time(T("2023-08-17T11:20:57.000"))
                        .build(),
                LastPriceDTO.builder()
                        .symbol("CHZBNB")
                        .id(symbolOne.getId().getId())
                        .price(71)
                        .time(T("2023-08-17T13:11:18.000"))
                        .build()
        );
        assertThat(actual).containsExactlyElementsOf(expected);
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
