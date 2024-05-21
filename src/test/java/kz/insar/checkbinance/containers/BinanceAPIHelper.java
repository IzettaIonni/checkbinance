package kz.insar.checkbinance.containers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.client.RecentTradeDTO;
import kz.insar.checkbinance.client.SymbolPriceDTO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.model.RequestDefinition;

import java.util.ArrayList;
import java.util.List;

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

    public BinanceAPIHelper mockRequest(RequestDefinition request, HttpResponse response) {
        requestDefinitions.add(request);
        mockServerClient.when(request).respond(response);
        return this;
    }

    @SneakyThrows
    public BinanceAPIHelper mockRequestExchangeInfo(List<String>requestSymbols, ExchangeInfoBySymbolsDTO responseDTO) {
        return mockRequest(request()
                        .withMethod("GET")
                        .withPath("/exchangeInfo")
                        .withQueryStringParameter("symbols", objectMapper.writeValueAsString(requestSymbols)),
                response().withBody(JsonBody.json(responseDTO)).withStatusCode(200));
    }

    @SneakyThrows
    public BinanceAPIHelper mockRequestExchangeAllInfo(ExchangeInfoBySymbolsDTO responseDTO) {
        return mockRequest(request()
                        .withMethod("GET")
                        .withPath("/exchangeInfo"),
                response().withBody(JsonBody.json(responseDTO)).withStatusCode(200));
    }

    @SneakyThrows
    public BinanceAPIHelper mockRequestLastPrice(List<String> requestSymbols, HttpResponse response) {
        return mockRequest(request()
                .withMethod("GET")
                .withPath("/ticker/price")
                .withQueryStringParameter("symbols", objectMapper.writeValueAsString(requestSymbols)),
                response);
    }

    public BinanceAPIHelper mockRequestLastPrice(List<String> requestSymbols, List<SymbolPriceDTO> responseDTOList) {

        return mockRequestLastPrice(requestSymbols,
                response().withBody(JsonBody.json(responseDTOList)).withStatusCode(200));
    }

    @SneakyThrows
    public BinanceAPIHelper mockRequestLegacyLastPrice(
            String requestSymbol, int requestLimit, HttpResponse response) {
        return mockRequest(request()
                        .withMethod("GET")
                        .withPath("/trades")
                        .withQueryStringParameter("symbol", requestSymbol)
                        .withQueryStringParameter("limit", Integer.toString(requestLimit)),
                response);
    }

    public BinanceAPIHelper mockRequestLegacyLastPrice(
            String requestSymbol, int requestLimit, List<RecentTradeDTO> responseDTOList) {

        return mockRequestLegacyLastPrice(requestSymbol, requestLimit,
                response().withBody(JsonBody.json(responseDTOList)).withStatusCode(200));
    }

    public BinanceAPIHelper mockRequestLegacyLastPrice(
            String requestSymbol, int requestLimit, RecentTradeDTO responseDTO) {

        return mockRequestLegacyLastPrice(requestSymbol, requestLimit,
                response().withBody(JsonBody.json(responseDTO)).withStatusCode(200));
    }


}
