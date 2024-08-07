package kz.insar.checkbinance.controllers;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.api.SymbolParamsDTO;
import kz.insar.checkbinance.containers.*;
import kz.insar.checkbinance.helpers.AssertionHelper;
import kz.insar.checkbinance.helpers.CheckbinanceServiceHelper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
@ExtendWith(ContainerHolder.class)
@ActiveProfiles(value = {"test", "test1"})
public class TickerControllerRESTAssuredIT {

    @Autowired
    private MockMvc mvc;
    private static AssertionHelper assertionHelper;
    private static BinanceAPIHelper binanceAPIHelper;
    @Autowired
    private CheckbinanceServiceHelper checkbinanceServiceHelper;
    @Autowired
    private ApplicationContext applicationContext;

    @Deprecated
    private SymbolParamsDTO createSymbolParamDTO(String name) {
        SymbolParamsDTO symbol = new SymbolParamsDTO();
        symbol.setSymbol(UUID.randomUUID().toString());
        symbol.setStatus(checkbinanceServiceHelper.getRandomSymbolStatus());
        symbol.setBaseAsset(UUID.randomUUID().toString());
        symbol.setBaseAssetPrecision(ThreadLocalRandom.current().nextInt());
        symbol.setQuoteAsset(UUID.randomUUID().toString());
        symbol.setQuotePrecision(ThreadLocalRandom.current().nextInt());
        symbol.setQuoteAssetPrecision(ThreadLocalRandom.current().nextInt());
        return symbol;
    }

    private static LocalDateTime T(String time) {
        return LocalDateTime.parse(time);
    }

    @BeforeAll
    static void beforeClass() {
        binanceAPIHelper = ContainerHolder.createBinanceAPIHelper();
        assertionHelper = new AssertionHelper();
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
    void testLastPrice_shouldReturnPricesIfOK() {
        checkbinanceServiceHelper.
                createAndSubscribeSymbol("CHZBNB").createAndSubscribeSymbol("BEAMUSDT");

        var response = checkbinanceServiceHelper.createBNBLastPriceResponseBuilder()
                .addPrice(checkbinanceServiceHelper.getSymbol(-2), ThreadLocalRandom.current().nextInt())
                .addPrice(checkbinanceServiceHelper.getSymbol(-1), ThreadLocalRandom.current().nextInt())
                .build();

        binanceAPIHelper.mockRequestLastPrice(response);

        var startTime = LocalDateTime.now();

        List<LastPriceDTO> actual = given()
                .params("sortKey", "ID", "sortDir", "ASC")

        .when()
                .get("/ticker/lastprice")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.OK)
                .contentType("application/json")
                .extract().body()
                .jsonPath().getList(".", LastPriceDTO.class);

        var endTime = LocalDateTime.now();

        assertThat(actual).extracting(LastPriceDTO::getTime)
                .allSatisfy(localDateTime -> assertThat(localDateTime).isBetween(startTime, endTime));

        assertThat(actual).extracting(LastPriceDTO::getId).allSatisfy(Assertions::assertNotNull);

        var expected = response.toLastPriceDTO(actual);

        assertEquals(expected, actual);
    }

    @Test
    void testLastPrice_shouldReturnExceptionIfSymbolNotFound() {
        String symbolOne = "CHZBNB";
        String symbolTwo = "BEAMUSDT";
        binanceAPIHelper.mockRequestLastPriceErrorNotFound(List.of(symbolOne, symbolTwo));

        checkbinanceServiceHelper.
                createAndSubscribeSymbol(symbolOne).createAndSubscribeSymbol(symbolTwo);

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
                .body("isEmpty()", Matchers.is(true));
    }

    @Test
    void testLastPrice_shouldReturnServiceUnavailableIfBinanceAPIReturns503() {
        String symbolOne = "CHZBNB";
        String symbolTwo = "BEAMUSDT";
        binanceAPIHelper.mockRequestLastPriceErrorServiceUnavailable(List.of(symbolOne, symbolTwo));

        checkbinanceServiceHelper.
                createAndSubscribeSymbol(symbolOne).createAndSubscribeSymbol(symbolTwo);

        given()
                .params("sortKey", "ID", "sortDir", "DESC")

        .when()
                .get("/ticker/lastprice")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    void testLastPrice_shouldReturnNotFoundIfStockClientReturns404() {
        String symbolOne = "CHZBNB";
        String symbolTwo = "BEAMUSDT";
        binanceAPIHelper.mockRequestLastPriceErrorNotFound(List.of(symbolOne, symbolTwo));

        checkbinanceServiceHelper.
                createAndSubscribeSymbol(symbolOne).createAndSubscribeSymbol(symbolTwo);

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
    void testLastPrice_shouldReturnForbiddenIfStockClientReturnsWAFLimit() {
        String symbolOne = "CHZBNB";
        String symbolTwo = "BEAMUSDT";
        binanceAPIHelper.mockRequestLastPriceErrorWAFLimit(List.of(symbolOne, symbolTwo));

        checkbinanceServiceHelper.
                createAndSubscribeSymbol(symbolOne).createAndSubscribeSymbol(symbolTwo);

        given()
                .params("sortKey", "ID", "sortDir", "DESC")

                .when()
                .get("/ticker/lastprice")

                .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.FORBIDDEN);
    }

    @Test
    void testLegacyLastPrice_shouldReturnPricesIfOKWithLimit() {
        checkbinanceServiceHelper.createAndSubscribeSymbol("CHZBNB").createAndSubscribeSymbol("BEAMUSDT");

        var response = checkbinanceServiceHelper.createBNBLastPriceResponseBuilder()
                .addPrice(checkbinanceServiceHelper.getSymbol(-2), ThreadLocalRandom.current().nextInt())
                .addPrice(checkbinanceServiceHelper.getSymbol(-1), ThreadLocalRandom.current().nextInt())
                .build();

        binanceAPIHelper.mockRequestLegacyLastPrice(response);

        List<LastPriceDTO> actual = given()
                .param("sortKey", "ID")
                .param("sortDir", "DESC")
                .param("limit", "2")

        .when()
                .get("/ticker/legacylastprice")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.OK)
                .contentType("application/json")
                .extract().body()
                .jsonPath().getList(".", LastPriceDTO.class);


    }

    @Test
    void testLegacyLastPrice_shouldReturnPricesIfOKWithoutLimit() {
        String symbolOne = "CHZBNB";
        String symbolTwo = "BEAMUSDT";
        var recentTradesList = binanceAPIHelper.mockRequestLegacyLastPrice(List.of(symbolOne, symbolTwo));

        checkbinanceServiceHelper.
                createAndSubscribeSymbol(symbolOne).createAndSubscribeSymbol(symbolTwo);

        List<LastPriceDTO> actual = given()
                .param("sortKey", "ID")
                .param("sortDir", "DESC")

                .when()
                .get("/ticker/legacylastprice")

                .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.OK)
                .contentType("application/json")
                .extract().body()
                .jsonPath().getList(".", LastPriceDTO.class);

        assertionHelper.legacyLastPriceAssertion(recentTradesList, checkbinanceServiceHelper.getSymbols(), actual);
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
    void testLegacyLastPrice_shouldReturnServiceUnavailableIfBinanceAPIReturns503() {

    }
    @Test
    void testLegacyLastPrice_shouldReturnNotFoundIfStockClientReturns404() {

    }
    @Test
    void testLegacyLastPrice_shouldReturnForbiddenIfStockClientReturnsWAFLimit() {

    }

    @Test
    void testExchangeInfo_shouldReturnInfoIfOK() {
        checkbinanceServiceHelper.createRandomSymbols(2);

        var response = BNBExchangeInfoResponse.builder()
                .addSymbol(checkbinanceServiceHelper.getSymbol(-1))
                .addSymbol(checkbinanceServiceHelper.getSymbol(-2)).build();

        binanceAPIHelper.mockRequestExchangeInfo(response);

        var actual = given()
                .param("symbols", response.getRequestSymbols())

            .when()
                .get("/ticker/exchangeinfo")

            .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.OK)
                .contentType("application/json")
                .extract().body()
                .jsonPath().getObject(".", ExchangeInfoBySymbolsDTO.class);

        var expected = response.toExchangeInfoBySymbolsDTO();
        assertEquals(expected, actual);
    }

    @Test
    void testExchangeInfo_shouldReturnBadRequestIfSymbolsIsNull() {

    }

    @Test
    void testExchangeInfo_shouldReturnServiceUnavailableIfBinanceAPIReturns503() {

    }

    @Test
    void testExchangeInfo_shouldReturnNotFoundIfStockClientReturns404() {

    }

    @Test
    void testExchangeInfo_shouldReturnForbiddenIfStockClientReturnsWAFLimit() {
        
    }

    @Test
    void testExchangeAllInfo() {
        checkbinanceServiceHelper.createRandomSymbols(2);

        var response = BNBExchangeInfoResponse.builder()
                .addSymbol(checkbinanceServiceHelper.getSymbol(-1))
                .addSymbol(checkbinanceServiceHelper.getSymbol(-2)).build();

        binanceAPIHelper.mockRequestExchangeAllInfo(response);
        var actual = given()

        .when()
                .get("/ticker/exchangeallinfo")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.OK)
                .contentType("application/json")
                .extract().body()
                .jsonPath().getObject(".", ExchangeInfoBySymbolsDTO.class);

        var expected = response.toExchangeInfoBySymbolsDTO();
        assertEquals(expected, actual);
    }


//    @Test
//    void testSubscribeTicker() {
//        var symbolOne = createSymbol("CHZBNB");
//        given()
//                .params("name", symbolOne.getName())
//
//
//        .when()
//                .post("/ticker/subscribeticker")
//
//
//        .then()
//                .assertThat().status(HttpStatus.OK);
//        var expected = List.of(symbolOne);
//        var actual = tickerService.listSubscriptionOnPrices();
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testSubscribeTicker_InvalidId() {
//        given()
//                .params("id", 2147483647)
//
//
//                .when()
//                .post("/ticker/subscribeticker")
//
//
//                .then()
//                .assertThat().status(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    void testSubscribeTicker_InvalidName() {
//        given()
//                .params("name", "absolutely not valid name")
//
//
//                .when()
//                .post("/ticker/subscribeticker")
//
//
//                .then()
//                .assertThat().status(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    void testSubscribeTicker_BadRequestException() {
//        given()
//
//        .when()
//                .post("/ticker/subscribeticker")
//
//        .then()
//                .assertThat().status(HttpStatus.BAD_REQUEST);
//    }
//
//    @Test
//    void testUnsubscribeTicker() {
//        var symbolOne = createSymbol("CHZBNB");
//        var symbolTwo = createSymbol("BEAMUSDT");
//        tickerService.subscribeOnPrice(symbolOne);
//        tickerService.subscribeOnPrice(symbolTwo);
//        given()
//                .params("name", symbolOne.getName())
//
//
//        .when()
//                .post("/ticker/unsubscribeticker")
//
//
//        .then()
//                .assertThat().status(HttpStatus.OK);
//        var expected = List.of(symbolTwo);
//        var actual = tickerService.listSubscriptionOnPrices();
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testUnsubscribeTicker_BadRequestException() {
//        given()
//
//        .when()
//                .post("/ticker/unsubscribeticker")
//
//        .then()
//                .assertThat().status(HttpStatus.BAD_REQUEST);
//    }
//
//    @Test
//    void testSubscriptions() {
//        var symbolOne = createSymbol("BTCBNB");
//        var symbolTwo = createSymbol("BTCETH");
//        tickerService.subscribeOnPrice(symbolOne);
//        tickerService.subscribeOnPrice(symbolTwo);
//        given()
//
//
//        .when()
//                .get("/ticker/subscriptions")
//
//
//        .then()
//                .assertThat().status(HttpStatus.OK);
//        var expected = List.of(symbolOne, symbolTwo);
//        var actual = tickerService.listSubscriptionOnPrices();
//        assertEquals(expected, actual);
//    }

}
