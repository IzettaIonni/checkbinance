package kz.insar.checkbinance.controllers;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.BinanceClient;
import kz.insar.checkbinance.converters.ApiConvertrer;
import kz.insar.checkbinance.services.TickerService;
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
    private TickerService tickerService;

    @GetMapping("/lastprice")
    public List<LastPriceDTO> lastPrices(@Nullable @RequestParam List<String> symbols) {
        return tickerService.lastPrices(symbols);
    }

    @GetMapping("/exchangeinfo")
    public ExchangeInfoBySymbolsDTO exchangeInfo(@Nullable @RequestParam List<String> symbols) {
        return tickerService.exchangeInfo(symbols);
    }

    @GetMapping("/exchangeallinfo")
    public ExchangeInfoBySymbolsDTO exchangeInfo() {
        return tickerService.exchangeInfo();
    }
}
