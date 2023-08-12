package kz.insar.checkbinance.containers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    public BinanceAPIHelper mockRequestTickerPrice(List<String> requestSymbols, HttpResponse response) {
        return mockRequest(request()
                .withMethod("GET")
                .withPath("/ticker/price")
                .withQueryStringParameter("symbols", objectMapper.writeValueAsString(requestSymbols)),
                response);
    }

    public BinanceAPIHelper mockRequestTickerPrice(List<String> requestSymbols, List<SymbolPriceDTO> responseDTO) {
        return mockRequestTickerPrice(requestSymbols, response().withBody(JsonBody.json(responseDTO)).withStatusCode(200));
    }


}
