package kz.insar.checkbinance.services.impl;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.BinanceClient;
import kz.insar.checkbinance.converters.ApiConvertrer;
import kz.insar.checkbinance.services.TickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TickerServiceImpl implements TickerService {

    @Autowired
    private BinanceClient binanceClient;

    @Autowired
    private ApiConvertrer apiConvertrer;

    @Override
    public List<LastPriceDTO> lastPrices(List<String> symbols) {
        List<LastPriceDTO> prices = new ArrayList<>();
        for (int i = 0; i < symbols.size(); i++) {
            String symbol = symbols.get(i);
            var recentTrades = binanceClient.getRecentTrades(symbol);
            for (int j = 0; j < recentTrades.size(); j++) {
                prices.add(apiConvertrer.toApi(symbol, recentTrades.get(i)));
            }
        }
        return prices;
    }

    @Override
    public ExchangeInfoBySymbolsDTO exchangeInfo(List<String> symbols) {
        return apiConvertrer.toApi(binanceClient.getExchangeInfoBySymbols(symbols));
    }

    @Override
    public ExchangeInfoBySymbolsDTO exchangeInfo() {
        return apiConvertrer.toApi(binanceClient.getExchangeInfo());
    }
}
