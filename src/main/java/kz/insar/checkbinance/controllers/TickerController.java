package kz.insar.checkbinance.controllers;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.api.SymbolShortDTO;
import kz.insar.checkbinance.converters.ApiConvertrer;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.services.TickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("ticker")
public class TickerController {

    @Autowired
    private TickerService tickerService;

    private final ApiConvertrer apiConverter = new ApiConvertrer();

    @GetMapping("/lastprice")
    public List<LastPriceDTO> lastPrices(@Nullable @RequestParam List<String> symbols,
                                         @Nullable @RequestParam(defaultValue = "1") Integer limit) {
        return tickerService.lastPrices(symbols, limit);
    }

    @GetMapping("/exchangeinfo")
    public ExchangeInfoBySymbolsDTO exchangeInfo(@Nullable @RequestParam List<String> symbols) {
        return tickerService.exchangeInfo(symbols);
    }

    @GetMapping("/exchangeallinfo")
    public ExchangeInfoBySymbolsDTO exchangeInfo() {
        return tickerService.exchangeInfo();
    }

    @GetMapping("subscribeticker")
    public void subscribeTicker(@Nullable @RequestParam Integer id) {
            tickerService.subscribeOnPrice(id);
    }

    @GetMapping("unsubscribeticker")
    public void unsubscribeTicker(@Nullable @RequestParam Integer id) {
            tickerService.unsubscribeOnPrice(id);
    }

    @GetMapping("/subscriptions")
    public List<SymbolShortDTO> subscriptions() {
        return apiConverter.fromDomainToShortList(tickerService.listSubscribtionOnPrices());
    }
}
