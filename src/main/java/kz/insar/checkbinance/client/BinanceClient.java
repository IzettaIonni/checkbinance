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
    @NonNull
    private final RestOperations restOperations;

    public BinanceClient() {
        this(new RestTemplate());
    }

    public ExchangeInfoResponseDTO getExchangeInfo() {
        return restOperations.getForObject("https://api.binance.com/api/v3/exchangeInfo", ExchangeInfoResponseDTO.class);
    }

    public ExchangeInfoResponseDTO getExchangeInfoBySymbol(String symbol) {
        return restOperations.getForObject("https://api.binance.com/api/v3/exchangeInfo?symbol=" + symbol, ExchangeInfoResponseDTO.class);
    }

    @SneakyThrows
    public ExchangeInfoResponseDTO getExchangeInfoBySymbols(List<String> symbols) {
        ObjectMapper objectMapper = new ObjectMapper();
        URI requestUri = UriComponentsBuilder.fromHttpUrl("https://api.binance.com")
                .queryParam("symbols", objectMapper.writeValueAsString(symbols))
                .path("/api/v3/exchangeInfo")
                .build()
                .toUri();
        return restOperations.getForObject(requestUri, ExchangeInfoResponseDTO.class);
    }

    public List<RecentTradeDTO> getRecentTrades(String symbol, Integer limit) {
        URI requestUri = UriComponentsBuilder.fromHttpUrl("https://api.binance.com")
                .path("/api/v3/trades")
                .queryParam("symbol", symbol).queryParam("limit", limit)
                .build()
                .toUri();
        return restOperations.exchange(
                requestUri, HttpMethod.GET, null, new ParameterizedTypeReference<List<RecentTradeDTO>>() {}
                )
                .getBody();
    }
}

