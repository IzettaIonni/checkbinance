package kz.insar.checkbinance.client;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

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

    public ExchangeInfoResponseDTO getExchangeInfoBySymbols(String[] symbols) {
        String urn = "%5B";
        for (int i = 0; i < symbols.length; i++) {
            if (i > 0) urn += ",";
            urn += "%22" + symbols[i] + "%22";
        }
        urn += "%5D";
        System.out.println("https://api.binance.com/api/v3/exchangeInfo?symbols=" + urn);
        return restOperations.getForObject("https://api.binance.com/api/v3/exchangeInfo?symbols=" + urn, ExchangeInfoResponseDTO.class);
    }
}
