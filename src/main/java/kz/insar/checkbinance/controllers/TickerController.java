package kz.insar.checkbinance.controllers;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.BinanceClient;
import kz.insar.checkbinance.converters.ApiConvertrer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("ticker")
public class TickerController {

    @Autowired
    private BinanceClient binanceClient;

    @Autowired
    private ApiConvertrer apiConvertrer;

    @GetMapping("/lastprice")
    public List<LastPriceDTO> lastPrices(@Nullable @RequestParam List<String> symbols) {
        List<LastPriceDTO> prices = new ArrayList<>();
        for (Integer i = 0; i < symbols.size(); i++) {
            String symbol = symbols.get(i);
            var recentTrades = binanceClient.getRecentTrades(symbol);
            for (Integer j = 0; j < recentTrades.size(); j++) {
                prices.add(apiConvertrer.toApi(symbol, recentTrades.get(i)));
            }
        }
        return prices;
    }

    @GetMapping("/exchangeinfo")
    public ExchangeInfoBySymbolsDTO exchangeInfo(@Nullable @RequestParam List<String> symbols) {
        return apiConvertrer.toApi(binanceClient.getExchangeInfoBySymbols(symbols));
    }

    @GetMapping("/exchangeallinfo")
    public ExchangeInfoBySymbolsDTO exchangeAllInfo() {
        return apiConvertrer.toApi(binanceClient.getExchangeInfo());
    }
}
