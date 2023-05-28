package kz.insar.checkbinance.services.impl;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.BinanceClient;
import kz.insar.checkbinance.client.SymbolDTO;
import kz.insar.checkbinance.converters.ApiConvertrer;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.services.SymbolService;
import kz.insar.checkbinance.services.TickerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TickerServiceImpl implements TickerService {

    @Autowired
    private BinanceClient binanceClient;

    @Autowired
    private ApiConvertrer apiConvertrer;

    @Autowired
    private SymbolService symbolService;

    @Override
    public List<LastPriceDTO> lastPrices(List<String> symbols) {
        return lastPrices(symbols, 10);
    }

    @Override
    public List<LastPriceDTO> lastPrices(List<String> symbols, int limit) {
        List<LastPriceDTO> prices = new ArrayList<>();
        if (symbols == null || symbols.size() == 0) return prices;
        for (int i = 0; i < symbols.size(); i++) {
            String symbol = symbols.get(i);
            var recentTrades = binanceClient.getRecentTrades(symbol, limit);
            for (int j = 0; j < recentTrades.size(); j++) {
                prices.add(apiConvertrer.toApi(symbol, recentTrades.get(j)));
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

    @Override
    public  List<String> updateSymbols() {
        var exchangeInfo = binanceClient.getExchangeInfo();
        Map<String, Symbol> exsistsSymbols = symbolService.getSymbols()
                .stream().collect(Collectors.toMap(Symbol::getName, Function.identity()));
        List<String> result = new ArrayList<>();
        for (SymbolDTO params : exchangeInfo.getSymbols()) {
            if (exsistsSymbols.containsKey(params.getSymbol())) {
                //Update
                var request = apiConvertrer.toDomainUpdate(exsistsSymbols.get(params.getSymbol()), params);
                result.add(symbolService.updateSymbol(request).getName());
            }
            else {
                //Create
                var request =apiConvertrer.toDomainCreate(params);
                result.add(symbolService.createSymbol(request).getName());
            }
        }
        log.info("Symbol list is updated!");
        return result;
    }

    @Override
    public List<Symbol> listSymbols() {
        return symbolService.getSymbols();
    }

    @Override
    public void subscribeOnPrice(SymbolId id) {
        symbolService.addPriceSubscription(id);
    }

    @Override
    public void unsubscribeOnPrice(SymbolId id) {
        symbolService.removePriceSubscription(id);
    }

    @Override
    public List<Symbol> listSubscribtionOnPrices() {
        return symbolService.getListOfPriceSubscriptions();
    }

}
