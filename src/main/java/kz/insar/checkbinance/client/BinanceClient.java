package kz.insar.checkbinance.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@AllArgsConstructor
public class BinanceClient {
    @NonNull
    private final RestOperations restOperations;
    @Autowired
    public BinanceClient() {
        this(new RestTemplate());
    }

    public ExchangeInfoResponseDTO getExchangeInfo() {
        return restOperations.getForObject("https://api.binance.com/api/v3/exchangeInfo", ExchangeInfoResponseDTO.class);
    }

    public ExchangeInfoResponseDTO getExchangeInfoBySymbol(String symbol) {
        return restOperations.getForObject(
                "https://api.binance.com/api/v3/exchangeInfo?symbol=" + symbol, ExchangeInfoResponseDTO.class);
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

        try {
            return restOperations.exchange(
                    requestUri, HttpMethod.GET, null, new ParameterizedTypeReference<List<RecentTradeDTO>>() {}
                    )
                    .getBody();
        }
        catch(Exception e) {
            throw new BinanceClientExeption("Unable to get recent trades", e);
        }

    }
    public List<RecentTradeDTO> getRecentTrades(String symbol) {
        return getRecentTrades(symbol, 10);
    }

    //Get prices of symbols form the list
    @SneakyThrows
    public List<SymbolPriceDTO> getPrices(List<String> symbols) {
        ObjectMapper objectMapper = new ObjectMapper();
        URI requestUri = UriComponentsBuilder.fromHttpUrl("https://api.binance.com")
                .path("/api/v3/ticker/price")
                .queryParam("symbols", objectMapper.writeValueAsString(symbols))
                .build()
                .toUri();
        try {
            return restOperations.exchange(
                            requestUri, HttpMethod.GET, null, new ParameterizedTypeReference<List<SymbolPriceDTO>>() {}
                    )
                    .getBody();
        }
        catch(Exception e) {
            throw new BinanceClientExeption("Unable to get symbols prices", e);
        }
    }

    @SneakyThrows
    public SymbolPriceDTO getPrice(String symbol) {
        ObjectMapper objectMapper = new ObjectMapper();
        URI requestUri = UriComponentsBuilder.fromHttpUrl("https://api.binance.com")
                .path("/api/v3/ticker/price")
                .queryParam("symbol", symbol)
                .build()
                .toUri();

        try {
            return restOperations.exchange(
                            requestUri, HttpMethod.GET, null, new ParameterizedTypeReference<SymbolPriceDTO>() {}
                    )
                    .getBody();
        }
        catch(Exception e) {
            throw new BinanceClientExeption("Unable to get symbol price", e);
        }
    }


    @SneakyThrows
    public List<SymbolPriceDTO> getPrices() {
        ObjectMapper objectMapper = new ObjectMapper();
        URI requestUri = UriComponentsBuilder.fromHttpUrl("https://api.binance.com")
                .path("/api/v3/ticker/price")
                .build()
                .toUri();

        try {
            return restOperations.exchange(
                            requestUri, HttpMethod.GET, null, new ParameterizedTypeReference<List<SymbolPriceDTO>>() {}
                    )
                    .getBody();
        }
        catch(Exception e) {
            throw new BinanceClientExeption("Unable to get all symbols prices", e);
        }
    }
}
