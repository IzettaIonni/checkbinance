package kz.insar.checkbinance.containers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.client.SymbolPriceDTO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.model.RequestDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@AllArgsConstructor
public class BinanceAPIHelper {

    private final MockServerClient mockServerClient;

    private final List<RequestDefinition> requestDefinitions;

    private final ObjectMapper objectMapper;

    public BinanceAPIHelper(MockServerClient mockServerClient) {
        this(mockServerClient, new ArrayList<>(), new ObjectMapper());
    }

    public void cleanUp() {
        for (var request : requestDefinitions) {
            mockServerClient.clear(request);
        }
    }

    private BinanceAPIHelper mockRequest(RequestDefinition request, HttpResponse response) {
        requestDefinitions.add(request);
        mockServerClient.when(request).respond(response);
        return this;
    }

    private HttpRequest buildRequestGet(String path) {
        return request()
                .withMethod("GET")
                .withPath(path);
    }

    private HttpRequest buildRequestExchangeInfoGet() {
        return buildRequestGet("/exchangeInfo");
    }

    private HttpRequest buildRequestTickerPriceGet() {
        return buildRequestGet("/ticker/price");
    }

    @SneakyThrows
    private HttpRequest buildRequestTickerPriceGet(List<String> requestSymbols) {
        return buildRequestTickerPriceGet()
                .withQueryStringParameter("symbols", objectMapper.writeValueAsString(requestSymbols));
    }

    @Deprecated
    @SneakyThrows
    public BinanceAPIHelper mockRequestExchangeInfo(List<String>requestSymbols, ExchangeInfoBySymbolsDTO responseDTO) {
        return mockRequest(
                buildRequestExchangeInfoGet()
                        .withQueryStringParameter("symbols", objectMapper.writeValueAsString(requestSymbols)),
                response().withBody(JsonBody.json(responseDTO)).withStatusCode(200));
    }

    public BinanceAPIHelper mockRequestExchangeInfo(BNBExchangeInfoResponse response) {
        return mockRequestExchangeInfo(
                response.getRequestSymbols(),
                response.toExchangeInfoBySymbolsDTO()
        );
    }

    @Deprecated
    @SneakyThrows
    public BinanceAPIHelper mockRequestExchangeAllInfo(ExchangeInfoBySymbolsDTO responseDTO) {
        return mockRequest(buildRequestExchangeInfoGet(),
                response().withBody(JsonBody.json(responseDTO)).withStatusCode(200));
    }

    public BinanceAPIHelper mockRequestExchangeAllInfo(BNBExchangeInfoResponse response) {
        return mockRequestExchangeAllInfo(response.toExchangeInfoBySymbolsDTO());
    }

    @SneakyThrows
    private BinanceAPIHelper mockRequestGetPrices(List<String> requestSymbols, List<SymbolPriceDTO> responseDTOList) {
        return mockRequest(buildRequestTickerPriceGet(requestSymbols),
                response().withBody(JsonBody.json(responseDTOList)).withStatusCode(200));
    }

    @SneakyThrows
    private BinanceAPIHelper mockRequestLastPrice(List<String> requestSymbols, HttpResponse response) {
        return mockRequest(buildRequestTickerPriceGet(requestSymbols), response);
    }

    public BinanceAPIHelper mockRequestLastPrice(List<String> requestSymbols, BNBLastPriceResponse response) {
        List<SymbolPriceDTO> responseDTOList = response.getPrices().stream()
                .map(BNBLastPrice::toSymbolPriceDTO).collect(Collectors.toList());
        return mockRequestLastPrice(requestSymbols,
                response().withBody(JsonBody.json(responseDTOList)).withStatusCode(200));
    }

    public BinanceAPIHelper mockRequestLastPrice(BNBLastPriceResponse response) {
        return mockRequestLastPrice(response.getUniqueSymbol(), response);
    }

    private BinanceAPIHelper mockRequestLastPriceError(List<String> requestSymbols, int responseErrorCode) {
        return mockRequestLastPrice(requestSymbols, response().withStatusCode(responseErrorCode));
    }

    public BinanceAPIHelper mockRequestLastPriceErrorNotFound(List<String> requestSymbols) {
        return mockRequestLastPriceError(requestSymbols, 404);
    }

    public BinanceAPIHelper mockRequestLastPriceErrorWAFLimit(List<String> requestSymbols) {
        return mockRequestLastPriceError(requestSymbols, 403);
    }

    public BinanceAPIHelper mockRequestLastPriceErrorRateLimit(List<String> requestSymbols) {
        return mockRequestLastPriceError(requestSymbols, 429);
    }

    public BinanceAPIHelper mockRequestLastPriceErrorPartialSuccess(List<String> requestSymbols) {
        return mockRequestLastPriceError(requestSymbols, 409);
    }

    public BinanceAPIHelper mockRequestLastPriceErrorServiceUnavailable(List<String> requestSymbols) {
        return mockRequestLastPriceError(requestSymbols, 503);
    }

    @SneakyThrows
    private BinanceAPIHelper mockRequestLegacyLastPrice(
            String requestSymbol, int requestLimit, HttpResponse response) {
        return mockRequest(request()
                        .withMethod("GET")
                        .withPath("/trades")
                        .withQueryStringParameter("symbol", requestSymbol)
                        .withQueryStringParameter("limit", Integer.toString(requestLimit)),
                response);
    }

    public BinanceAPIHelper mockRequestLegacyLastPrice(
            String requestSymbol, int requestLimit, BNBLegacyLastPriceResponse response) {
        return mockRequestLegacyLastPrice(requestSymbol, requestLimit,
                response().withBody(JsonBody.json(response.toRecentTradeDTOs())).withStatusCode(200));
    }

    public BinanceAPIHelper mockRequestLegacyLastPrice(String requestSymbol, BNBLegacyLastPriceResponse response) {
        return mockRequestLegacyLastPrice(requestSymbol, response.getPricesQuantity(), response);
    }

    public BinanceAPIHelper mockRequestLegacyLastPrice(
            List<String> requestSymbols, int requestLimit, List<BNBLegacyLastPriceResponse> responses) {
        if (requestSymbols.size() != responses.size()) throw new IllegalArgumentException("request symbols and responses doesn't match");
        for (int i = 0; i < responses.size(); i++) {
            mockRequestLegacyLastPrice(requestSymbols.get(i), requestLimit, responses.get(i));
        }
        return this;
    }

    public BinanceAPIHelper mockRequestLegacyLastPrice(List<String> requestSymbols, List<BNBLegacyLastPriceResponse> responses) {
        var limit = responses.get(0).getPricesQuantity();
        if (!responses.stream().allMatch(response -> Objects.equals(response.getPricesQuantity(), limit)))
            throw new IllegalArgumentException("Different responses' prices size"); //todo some uncertainty exception
        return mockRequestLegacyLastPrice(requestSymbols, limit, responses);
    }

    public BinanceAPIHelper mockRequestLegacyLastPrice(List<BNBLegacyLastPriceResponse> responses) {
        return mockRequestLegacyLastPrice(responses.stream().map(BNBLegacyLastPriceResponse::getCommonSymbolName).collect(Collectors.toList()), responses);
    }

    @Deprecated
    public BinanceAPIHelper mockRequestLegacyLastPriceWrapper(LegacyLastPriceMockWrapper requestSymbol) {
        var idIterator = requestSymbol.getRecentTrades().stream()
                .map(RecentTradesMockWrapper::getId).collect(Collectors.toList()).iterator();
        return mockRequestLegacyLastPrice(
                requestSymbol.getSymbol(),
                requestSymbol.getRequestLimit(),
                BNBLegacyLastPriceResponse.builder()
                        .addPrices(
                            requestSymbol.getRecentTrades().stream().map((trade) -> {
                            return BNBLegacyLastPrice.builder()
                                    .symbol(requestSymbol.getSymbol())
                                    .time(trade.getTime())
                                    .price(trade.getPrice())
                                    .qty(trade.getQty())
                                    .quoteQty(trade.getQuoteQty())
                                    .isBuyerMaker(trade.getIsBuyerMaker())
                                    .isBestMatch(trade.getIsBestMatch())
                                    .build();
                            }).collect(Collectors.toList())
                        )
                        .idGenerator(idIterator::next)
                        .build()
        );
    }

    @Deprecated
    public BinanceAPIHelper mockRequestLegacyLastPriceWrapper(List<LegacyLastPriceMockWrapper> requestSymbols) {
        BinanceAPIHelper result = mockRequestLegacyLastPriceWrapper(requestSymbols.get(0));
        for (int i = 1; i < requestSymbols.size(); i++)
            mockRequestLegacyLastPriceWrapper(requestSymbols.get(i));
        return result;
    }

    private BinanceAPIHelper mockRequestLegacyLastPriceError(String requestSymbol, int limit, int responseErrorCode) {
        return mockRequestLegacyLastPrice(requestSymbol, limit, response().withStatusCode(responseErrorCode));
    }

    private BinanceAPIHelper mockRequestLegacyLastPriceError(String requestSymbol, int responseErrorCode) {
        return mockRequestLegacyLastPriceError(requestSymbol, 1, responseErrorCode);
    }

    public BinanceAPIHelper mockRequestLegacyLastPriceErrorNotFound(String requestSymbol) {
        return mockRequestLegacyLastPriceError(requestSymbol, 404);
    }

    public BinanceAPIHelper mockRequestLegacyLastPriceErrorWAFLimit(String requestSymbol) {
        return mockRequestLegacyLastPriceError(requestSymbol, 403);
    }

    public BinanceAPIHelper mockRequestLegacyLastPriceErrorRateLimit(String requestSymbol) {
        return mockRequestLegacyLastPriceError(requestSymbol, 429);
    }

    public BinanceAPIHelper mockRequestLegacyLastPriceErrorPartialSuccess(String requestSymbol) {
        return mockRequestLegacyLastPriceError(requestSymbol, 409);
    }

    public BinanceAPIHelper mockRequestLegacyLastPriceErrorServiceUnavailable(String requestSymbol) {
        return mockRequestLegacyLastPriceError(requestSymbol, 503);
    }

}
