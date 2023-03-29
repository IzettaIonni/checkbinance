package kz.insar.checkbinance.client;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

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

    public ExchangeInfoResponseDTO getExchangeInfoBySymbol() {
        return restOperations.getForObject("https://api.binance.com/api/v3/exchangeInfo?symbol=BNBBTC", ExchangeInfoResponseDTO.class);
    }
}
