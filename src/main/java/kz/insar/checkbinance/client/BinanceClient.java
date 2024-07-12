package kz.insar.checkbinance.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@AllArgsConstructor
public class BinanceClient {
    private final String baseUrl;

    @NonNull
    private final RestOperations restOperations;
    public BinanceClient(String baseUrl) {
        this(baseUrl, new RestTemplate());
    }


    private UriComponentsBuilder getBaseUrl() {
        return UriComponentsBuilder.fromHttpUrl(baseUrl);
    }

    public ExchangeInfoResponseDTO getExchangeInfo() {
        return restOperations.getForObject(getBaseUrl().pathSegment("exchangeInfo").build().toUri(),
                ExchangeInfoResponseDTO.class);
    }

    public ExchangeInfoResponseDTO getExchangeInfoBySymbol(String symbol) {
        return restOperations.getForObject(
                getBaseUrl()
                        .queryParam("symbol", symbol)
                        .pathSegment("exchangeInfo")
                        .build()
                        .toUri(), ExchangeInfoResponseDTO.class);
    }

    @SneakyThrows
    public ExchangeInfoResponseDTO getExchangeInfoBySymbols(List<String> symbols) {
        ObjectMapper objectMapper = new ObjectMapper();
        URI requestUri = getBaseUrl()
                .queryParam("symbols", objectMapper.writeValueAsString(symbols))
                .pathSegment("exchangeInfo")
                .build()
                .toUri();
        return restOperations.getForObject(requestUri, ExchangeInfoResponseDTO.class);
    }


    public List<RecentTradeDTO> getRecentTrades(@NonNull String symbol, int limit) {
        if (limit < 1 || limit > 10) {
            throw new IllegalArgumentException("Limit must be in range 1 to 10");
        }
        URI requestUri = getBaseUrl()
                .pathSegment("trades")
                .queryParam("symbol", symbol)
                .queryParam("limit", limit)
                .build()
                .toUri();

        return restOperations.exchange(
                requestUri, HttpMethod.GET, null, new ParameterizedTypeReference<List<RecentTradeDTO>>() {}
                )
                .getBody();

    }

    public List<RecentTradeDTO> getRecentTrades(String symbol) {
        return getRecentTrades(symbol, 10);
    }

    @SneakyThrows
    public List<SymbolPriceDTO> getPrices(List<String> symbols) {
        ObjectMapper objectMapper = new ObjectMapper();
        URI requestUri = getBaseUrl()
                .pathSegment("ticker", "price")
                .queryParam("symbols", objectMapper.writeValueAsString(symbols))
                .build()
                .toUri();
        return restOperations.exchange(
                        requestUri, HttpMethod.GET, null, new ParameterizedTypeReference<List<SymbolPriceDTO>>() {}
                )
                .getBody();
    }

    public SymbolPriceDTO getPrice(String symbol) {
        ObjectMapper objectMapper = new ObjectMapper();
        URI requestUri = getBaseUrl()
                .pathSegment("ticker", "price")
                .queryParam("symbol", symbol)
                .build()
                .toUri();

        return restOperations.exchange(
                        requestUri, HttpMethod.GET, null, new ParameterizedTypeReference<SymbolPriceDTO>() {}
                )
                .getBody();
    }

    public List<SymbolPriceDTO> getPrices() {
        ObjectMapper objectMapper = new ObjectMapper();
        URI requestUri = getBaseUrl()
                .pathSegment("ticker", "price")
                .build()
                .toUri();

        return restOperations.exchange(
                        requestUri, HttpMethod.GET, null, new ParameterizedTypeReference<List<SymbolPriceDTO>>() {}
                ).getBody();
    }
}
