package kz.insar.checkbinance.controllers;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.api.SymbolShortDTO;
import kz.insar.checkbinance.containers.*;
import kz.insar.checkbinance.helpers.CheckbinanceServiceHelper;
import org.apache.commons.lang3.RandomStringUtils;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
@TestMethodOrder(MethodOrderer.Random.class)
public class TickerControllerRESTAssuredIT {

    @Autowired
    private MockMvc mvc;
    private static BinanceAPIHelper binanceAPIHelper;
    @Autowired
    private CheckbinanceServiceHelper checkbinanceServiceHelper;
    @Autowired
    private ApplicationContext applicationContext;

    private static LocalDateTime T(String time) {
        return LocalDateTime.parse(time);
    }

    @BeforeAll
    static void beforeClass() {
        binanceAPIHelper = ContainerHolder.createBinanceAPIHelper();
    }

    @BeforeEach
    void setUp() {
        mockMvc(mvc);
    }

    @AfterEach
    void tearDown() {
        binanceAPIHelper.cleanUp();
        checkbinanceServiceHelper.cleanUp();
    }

    @Test
    void testLastPrice_shouldReturnPricesIfOK() {
        checkbinanceServiceHelper.createAndSubscribeRandomSymbols(2);

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
    void testLastPrice_shouldReturnNullIfSubscriptionsDoesNotExist() {
        given()
                .params("sortKey", "ID", "sortDir", "DESC")

        .when()
                .get("/ticker/lastprice")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.OK)
                .body("isEmpty()", Matchers.is(true));
    }

    @Test
    void testLastPrice_shouldReturnNotFoundIfStockClientReturnsNotFound() {
        checkbinanceServiceHelper.createAndSubscribeRandomSymbols(2);

        binanceAPIHelper.mockRequestLastPriceErrorNotFound(checkbinanceServiceHelper.getSymbolNames(-2, -1));

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
        checkbinanceServiceHelper.createAndSubscribeRandomSymbols(2);

        binanceAPIHelper.mockRequestLastPriceErrorWAFLimit(checkbinanceServiceHelper.getSymbolNames(-2, -1));

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
    void testLastPrice_shouldReturnForbiddenIfBinanceAPIReturnsRateLimit() {
        checkbinanceServiceHelper.createAndSubscribeRandomSymbols(2);

        binanceAPIHelper.mockRequestLastPriceErrorRateLimit(checkbinanceServiceHelper.getSymbolNames(-2, -1));

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
    void testLastPrice_shouldReturnInternalServiceErrorIfBinanceAPIReturnsPartialSuccess() {
        checkbinanceServiceHelper.createAndSubscribeRandomSymbols(2);

        binanceAPIHelper.mockRequestLastPriceErrorPartialSuccess(checkbinanceServiceHelper.getSymbolNames(-2, -1));

        given()
                .params("sortKey", "ID", "sortDir", "ASC")

        .when()
                .get("/ticker/lastprice")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testLastPrice_shouldReturnServiceUnavailableIfBinanceAPIReturnsServiceUnavailable() {
        checkbinanceServiceHelper.createAndSubscribeRandomSymbols(2);

        binanceAPIHelper.mockRequestLastPriceErrorServiceUnavailable(checkbinanceServiceHelper.getSymbolNames(-2, -1));

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
    void testLegacyLastPrice_shouldReturnPricesIfOKWithLimit() {
        checkbinanceServiceHelper.createAndSubscribeRandomSymbols(2);

        var responses = List.of(
                checkbinanceServiceHelper.createBNBLegacyLastPriceResponseBuilder()
                            .addRandomPrice(checkbinanceServiceHelper.getSymbol(-2))
                            .addRandomPrice(checkbinanceServiceHelper.getSymbol(-2)).build(),
                checkbinanceServiceHelper.createBNBLegacyLastPriceResponseBuilder()
                            .addRandomPrice(checkbinanceServiceHelper.getSymbol(-1))
                            .addRandomPrice(checkbinanceServiceHelper.getSymbol(-1)).build()
        );

        binanceAPIHelper.mockRequestLegacyLastPrice(responses);

        List<LastPriceDTO> actual = given()
                .param("sortKey", "ID")
                .param("sortDir", "ASC")
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

        assertThat(actual).extracting(LastPriceDTO::getId).allSatisfy(Assertions::assertNotNull);

        var expected = responses.stream().flatMap(response -> response.toLastPriceDTO().stream()).collect(Collectors.toList());

        assertEquals(expected, actual);
    }

    @Test
    void testLegacyLastPrice_shouldReturnPricesIfOKWithoutLimit() {
        checkbinanceServiceHelper.createAndSubscribeRandomSymbols(2);

        var responses = List.of(
                checkbinanceServiceHelper.createBNBLegacyLastPriceResponseBuilder()
                        .addRandomPrice(checkbinanceServiceHelper.getSymbol(-2)).build(),
                checkbinanceServiceHelper.createBNBLegacyLastPriceResponseBuilder()
                        .addRandomPrice(checkbinanceServiceHelper.getSymbol(-1)).build()
        );

        binanceAPIHelper.mockRequestLegacyLastPrice(responses);

        List<LastPriceDTO> actual = given()
                .param("sortKey", "ID")
                .param("sortDir", "ASC")

        .when()
                .get("/ticker/legacylastprice")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.OK)
                .contentType("application/json")
                .extract().body()
                .jsonPath().getList(".", LastPriceDTO.class);

        assertThat(actual).extracting(LastPriceDTO::getId).allSatisfy(Assertions::assertNotNull);

        var expected = responses.stream().flatMap(response -> response.toLastPriceDTO().stream()).collect(Collectors.toList());

        assertEquals(expected, actual);
    }

    @Test
    void testLegacyLastPrice_shouldReturnEmptyIfSubscriptionsDoesNotExist() {
        given()
                .params("sortKey", "ID", "sortDir", "ASC")

        .when()
                .get("/ticker/legacylastprice")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.OK)
                .body("isEmpty()", Matchers.is(true));
    }

    @Test
    void testLegacyLastPrice_shouldReturnNotFoundIfBinanceAPIReturnsNotFound() {
        checkbinanceServiceHelper.createAndSubscribeRandomSymbol();

        binanceAPIHelper.mockRequestLegacyLastPriceErrorNotFound(
                checkbinanceServiceHelper.getSymbolName(-1));

        given()
                .params("sortKey", "ID", "sortDir", "ASC")

        .when()
                .get("/ticker/legacylastprice")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.NOT_FOUND);
    }

    @Test
    void testLegacyLastPrice_shouldReturnForbiddenIfBinanceAPIReturnsWAFLimit() {
        checkbinanceServiceHelper.createAndSubscribeRandomSymbol();

        binanceAPIHelper.mockRequestLegacyLastPriceErrorWAFLimit(
                checkbinanceServiceHelper.getSymbolName(-1));

        given()
                .params("sortKey", "ID", "sortDir", "ASC")

        .when()
                .get("/ticker/legacylastprice")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.FORBIDDEN);
    }

    @Test
    void testLegacyLastPrice_shouldReturnForbiddenIfBinanceAPIReturnsRateLimit() {
        checkbinanceServiceHelper.createAndSubscribeRandomSymbol();

        binanceAPIHelper.mockRequestLegacyLastPriceErrorRateLimit(
                checkbinanceServiceHelper.getSymbolName(-1));

        given()
                .params("sortKey", "ID", "sortDir", "ASC")

        .when()
                .get("/ticker/legacylastprice")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.FORBIDDEN);
    }

    @Test
    void testLegacyLastPrice_shouldReturnInternalServerErrorIfBinanceAPIReturnsPartialSuccess() {
        checkbinanceServiceHelper.createAndSubscribeRandomSymbol();

        binanceAPIHelper.mockRequestLegacyLastPriceErrorPartialSuccess(
                checkbinanceServiceHelper.getSymbolName(-1));

        given()
                .params("sortKey", "ID", "sortDir", "ASC")

        .when()
                .get("/ticker/legacylastprice")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testLegacyLastPrice_shouldReturnNotFoundIfBinanceAPIReturnsServiceUnavailable() {
        checkbinanceServiceHelper.createAndSubscribeRandomSymbol();

        binanceAPIHelper.mockRequestLegacyLastPriceErrorServiceUnavailable(
                checkbinanceServiceHelper.getSymbolName(-1));

        given()
                .params("sortKey", "ID", "sortDir", "ASC")

        .when()
                .get("/ticker/legacylastprice")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.SERVICE_UNAVAILABLE);
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
    void testExchangeInfo_shouldReturnNotFoundIfStockClientReturnsNotFound() {
        var requestSymbols = List.of(
                RandomStringUtils.randomAlphabetic(32),
                RandomStringUtils.randomAlphabetic(32));

        binanceAPIHelper.mockRequestExchangeInfoErrorNotFound(requestSymbols);

        given()
                .param("symbols", requestSymbols)

        .when()
                .get("/ticker/exchangeinfo")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.NOT_FOUND);
    }

    @Test
    void testExchangeInfo_shouldReturnForbiddenIfBinanceAPIReturnsWAFLimit() {
        var requestSymbols = List.of(
                RandomStringUtils.randomAlphabetic(32),
                RandomStringUtils.randomAlphabetic(32));

        binanceAPIHelper.mockRequestExchangeInfoErrorWAFLimit(requestSymbols);

        given()
                .param("symbols", requestSymbols)

                .when()
                .get("/ticker/exchangeinfo")

                .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.FORBIDDEN);
    }

    @Test
    void testExchangeInfo_shouldReturnForbiddenIfBinanceAPIReturnsRateLimit() {
        var requestSymbols = List.of(
                RandomStringUtils.randomAlphabetic(32),
                RandomStringUtils.randomAlphabetic(32));

        binanceAPIHelper.mockRequestExchangeInfoErrorRateLimit(requestSymbols);

        given()
                .param("symbols", requestSymbols)

                .when()
                .get("/ticker/exchangeinfo")

                .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.FORBIDDEN);
    }

    @Test
    void testExchangeInfo_shouldReturnInternalServerErrorIfBinanceAPIReturnsPartialSuccess() {
        var requestSymbols = List.of(
                RandomStringUtils.randomAlphabetic(32),
                RandomStringUtils.randomAlphabetic(32));

        binanceAPIHelper.mockRequestExchangeInfoErrorPartialSuccess(requestSymbols);

        given()
                .param("symbols", requestSymbols)

                .when()
                .get("/ticker/exchangeinfo")

                .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testExchangeInfo_shouldReturnServiceUnavailableIfBinanceAPIReturnsServiceUnavailable() {
        var requestSymbols = List.of(
                RandomStringUtils.randomAlphabetic(32),
                RandomStringUtils.randomAlphabetic(32));

        binanceAPIHelper.mockRequestExchangeInfoErrorServiceUnavailable(requestSymbols);

        given()
                .param("symbols", requestSymbols)

                .when()
                .get("/ticker/exchangeinfo")

                .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.SERVICE_UNAVAILABLE);
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

    @Test
    void testAllExchangeInfo_shouldReturnForbiddenIfBinanceAPIReturnsNotFound() {
        binanceAPIHelper.mockRequestExchangeAllInfoErrorNotFound();

        given()

        .when()
                .get("/ticker/exchangeallinfo")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.NOT_FOUND);
    }

    @Test
    void testAllExchangeInfo_shouldReturnForbiddenIfBinanceAPIReturnsWAFLimit() {
        binanceAPIHelper.mockRequestExchangeAllInfoErrorWAFLimit();

        given()

        .when()
                .get("/ticker/exchangeallinfo")

        .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.FORBIDDEN);
    }

    @Test
    void testAllExchangeInfo_shouldReturnForbiddenIfBinanceAPIReturnsRateLimit() {
        binanceAPIHelper.mockRequestExchangeAllInfoErrorRateLimit();

        given()

                .when()
                .get("/ticker/exchangeallinfo")

                .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.FORBIDDEN);
    }

    @Test
    void testAllExchangeInfo_shouldReturnInternalServerErrorIfBinanceAPIReturnsPartialSuccess() {
        binanceAPIHelper.mockRequestExchangeAllInfoErrorPartialSuccess();

        given()

                .when()
                .get("/ticker/exchangeallinfo")

                .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testAllExchangeInfo_shouldReturnServiceUnavailableIfBinanceAPIReturnsServiceUnavailable() {
        binanceAPIHelper.mockRequestExchangeAllInfoErrorServiceUnavailable();

        given()

                .when()
                .get("/ticker/exchangeallinfo")

                .then()
                .log().all()
                .assertThat()
                .status(HttpStatus.SERVICE_UNAVAILABLE);
    }


    @Test
    void testSubscribeTicker() {
        var symbol = checkbinanceServiceHelper.createRandomSymbol().getLastSymbol();

        given()
                .params("name", symbol.getName())

        .when()
                .post("/ticker/subscribeticker")

        .then()
                .assertThat().status(HttpStatus.OK);

        assertTrue(checkbinanceServiceHelper.isSymbolSubscribed(symbol));
    }

    @Test
    void testSubscribeTicker_InvalidId() {
        var symbol = checkbinanceServiceHelper.createAndSubscribeRandomSymbol().getLastSymbol();

        given()
                .params("name", symbol.getName())

                .when()
                .post("/ticker/unsubscribeticker")

                .then()
                .assertThat().status(HttpStatus.OK);

        assertTrue(checkbinanceServiceHelper.isSymbolUnsubscribed(symbol));
    }

    @Test
    void testSubscribeTicker_InvalidName() {
        given()
                .params("name", "absolutely not valid name")


                .when()
                .post("/ticker/subscribeticker")


                .then()
                .assertThat().status(HttpStatus.NOT_FOUND);
    }

    @Test
    void testUnsubscribeTicker_InvalidName() {
        given()
                .params("name", "absolutely not valid name")

        .when()
                .post("/ticker/unsubscribeticker")

        .then()
                .assertThat().status(HttpStatus.NOT_FOUND);
    }

    @Test
    void testSubscribeTicker_shouldReturnBadRequestIfWithoutParams() {
        given()

        .when()
                .post("/ticker/subscribeticker")

        .then()
                .assertThat().status(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testUnsubscribeTicker_shouldReturnBadRequestIfWithoutParams() {
        given()

        .when()
                .post("/ticker/subscribeticker")

        .then()
                .assertThat().status(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testSubscriptions() {
        checkbinanceServiceHelper.createAndSubscribeRandomSymbols(10);

        var actual = given()

        .when()
                .get("/ticker/subscriptions")

        .then()
                .assertThat().status(HttpStatus.OK)
                .contentType("application/json")
                .extract().body()
                .jsonPath().getList(".", SymbolShortDTO.class);

        var expected = checkbinanceServiceHelper.getSymbols().stream().map(
                testSymbol -> SymbolShortDTO.builder().id(testSymbol.getId().getId()).name(testSymbol.getName()).build())
                .collect(Collectors.toList());

        assertEquals(expected, actual);
    }

    @Test
    void testSubscriptions_shouldReturnEmptyIfNoSubscriptions() {
        given()

        .when()
                .get("/ticker/subscriptions")

        .then()
                .assertThat().status(HttpStatus.OK)
                .contentType("application/json")
                .body("isEmpty()", Matchers.is(true));
    }

}
