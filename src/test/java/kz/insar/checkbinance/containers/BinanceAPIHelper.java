package kz.insar.checkbinance.containers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.client.RecentTradeDTO;
import kz.insar.checkbinance.client.SymbolPriceDTO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.model.RequestDefinition;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

    private List<RecentTradeDTO> createRecentTradeDTO(int limit) {
        List<RecentTradeDTO> list = new ArrayList<>();
        Long id = ThreadLocalRandom.current().nextLong();
        for (int i = 0; i < limit; i++) {
            list.add(RecentTradeDTO.builder()
                    .id(id)
                    .price(BigDecimal.valueOf(ThreadLocalRandom.current().nextInt()))
                    .qty(BigDecimal.valueOf(ThreadLocalRandom.current().nextInt()))
                    .quoteQty(BigDecimal.valueOf(ThreadLocalRandom.current().nextInt()))
                    .time(1692277878000L)
                    .isBuyerMaker(ThreadLocalRandom.current().nextBoolean())
                    .isBestMatch(ThreadLocalRandom.current().nextBoolean())
                    .build());
        }
        return list;
    }

    private List<RecentTradeDTO> createRecentTradeDTO() {
        return createRecentTradeDTO(1);
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

    @SneakyThrows
    public BinanceAPIHelper mockRequestExchangeInfo(List<String>requestSymbols, ExchangeInfoBySymbolsDTO responseDTO) {
        return mockRequest(
                buildRequestExchangeInfoGet()
                        .withQueryStringParameter("symbols", objectMapper.writeValueAsString(requestSymbols)),
                response().withBody(JsonBody.json(responseDTO)).withStatusCode(200));
    }

    @SneakyThrows
    public BinanceAPIHelper mockRequestExchangeInfo(ExchangeInfoBySymbolsDTO responseDTO) {
        return mockRequest(buildRequestExchangeInfoGet(),
                response().withBody(JsonBody.json(responseDTO)).withStatusCode(200));
    }

    @SneakyThrows
    public BinanceAPIHelper mockRequestGetPrices(List<String> requestSymbols, List<SymbolPriceDTO> responseDTOList) {
        return mockRequest(buildRequestTickerPriceGet(requestSymbols),
                response().withBody(JsonBody.json(responseDTOList)).withStatusCode(200));
    }

    @SneakyThrows
    private BinanceAPIHelper mockRequestLastPrice(List<String> requestSymbols, HttpResponse response) {
        return mockRequest(buildRequestTickerPriceGet(requestSymbols), response);
    }

    public List<SymbolPriceDTO> mockRequestLastPrice(List<String> requestSymbols, List<Integer> prices) {
        if (requestSymbols.size() != prices.size()) throw new IllegalArgumentException("requestSymbols and prices have different size");
        List<SymbolPriceDTO> responseDTOList = new ArrayList<>();
        for (int i = 0; i < requestSymbols.size(); i++) {
            responseDTOList.add(SymbolPriceDTO.builder().price(prices.get(i)).symbol(requestSymbols.get(i)).build());
        }
        mockRequestLastPrice(requestSymbols,
                response().withBody(JsonBody.json(responseDTOList)).withStatusCode(200));
        return responseDTOList;
    }

    public List<SymbolPriceDTO> mockRequestLastPrice(List<String> requestSymbols) {
        var threadLocalRandom = ThreadLocalRandom.current();
        List<Integer> prices = new ArrayList<>();
        for (int i = 0; i < requestSymbols.size(); i++) {
            prices.add(threadLocalRandom.nextInt());
        }
        return mockRequestLastPrice(requestSymbols, prices);
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

    public RecentTradesWithSymbol mockRequestLegacyLastPrice(String requestSymbol, int limit) {
        var list = createRecentTradeDTO(limit);
        mockRequestLegacyLastPrice(requestSymbol, limit, list);
        return RecentTradesWithSymbol.builder().symbol(requestSymbol).recentTrades(list).build();
    }

    public List<RecentTradesWithSymbol> mockRequestLegacyLastPrice(List<String> requestSymbols, int limit) {
        var recentTradesWithSymbolList = new ArrayList<RecentTradesWithSymbol>();
        for (String symbol : requestSymbols) {
            var list = createRecentTradeDTO(limit);
            mockRequestLegacyLastPrice(symbol, limit, list);
            recentTradesWithSymbolList.add(
                    RecentTradesWithSymbol.builder().symbol(symbol).recentTrades(list).build());
        }
        return recentTradesWithSymbolList;
    }

    private BinanceAPIHelper mockRequestLegacyLastPrice(
            String requestSymbol, int requestLimit, List<RecentTradeDTO> responseDTOList) {
        return mockRequestLegacyLastPrice(requestSymbol, requestLimit,
                response().withBody(JsonBody.json(responseDTOList)).withStatusCode(200));
    }

    @SneakyThrows
    private BinanceAPIHelper mockRequestLegacyLastPrice(String requestSymbol, HttpResponse response) {
        return mockRequest(request()
                .withMethod("GET")
                .withPath("/trades")
                .withQueryStringParameter("symbol", requestSymbol), response);
    }

    private BinanceAPIHelper mockRequestLegacyLastPrice(
            String requestSymbol, List<RecentTradeDTO> responseDTOList) {
        return mockRequestLegacyLastPrice(requestSymbol,
                response().withBody(JsonBody.json(responseDTOList)).withStatusCode(200));
    }

    public RecentTradesWithSymbol mockRequestLegacyLastPrice(String requestSymbol) {
        var list = createRecentTradeDTO();
        mockRequestLegacyLastPrice(requestSymbol, list);
        return RecentTradesWithSymbol.builder().symbol(requestSymbol).recentTrades(list).build();
    }

    public List<RecentTradesWithSymbol> mockRequestLegacyLastPrice(List<String> requestSymbols) {
        var recentTradesWithSymbolList = new ArrayList<RecentTradesWithSymbol>();
        for (String symbol : requestSymbols) {
            var list = createRecentTradeDTO();
            mockRequestLegacyLastPrice(symbol, list);
            recentTradesWithSymbolList.add(
                    RecentTradesWithSymbol.builder().symbol(symbol).recentTrades(list).build());
        }
        return recentTradesWithSymbolList;
    }

}
