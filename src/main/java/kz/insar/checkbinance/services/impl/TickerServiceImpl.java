package kz.insar.checkbinance.services.impl;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.BinanceClient;
import kz.insar.checkbinance.client.RecentTradeDTO;
import kz.insar.checkbinance.client.SymbolDTO;
import kz.insar.checkbinance.converters.ApiConvertrer;
import kz.insar.checkbinance.domain.LastPriceColumns;
import kz.insar.checkbinance.domain.SortParams;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.domain.exeptions.ObjectNotFoundException;
import kz.insar.checkbinance.repositories.SymbolRepository;
import kz.insar.checkbinance.repositories.entities.SymbolEntity;
import kz.insar.checkbinance.services.SymbolService;
import kz.insar.checkbinance.services.TickerService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
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

    @Autowired
    private SymbolRepository symbolRepository;

    @Override
    public List<LastPriceDTO> lastPrices(SortParams<LastPriceColumns> sortParams) {
        return lastPrices(10, sortParams);
    }


    //TODO: повысить эффективность за счет понижения количества запросов
    @Override
    public List<LastPriceDTO> lastPrices(int limit, SortParams<LastPriceColumns> sortParams) {
        List<Symbol> subscriptions = listSubscribtionOnPrices();
        List<LastPriceDTO> prices = new ArrayList<>();
        if (subscriptions == null || subscriptions.size() == 0) return prices;
        for (Symbol symbol : subscriptions) {
            var recentTrades = binanceClient.getRecentTrades(symbol.getName(), limit);
            for (RecentTradeDTO recentTrade : recentTrades) {
                prices.add(apiConvertrer.toApi(symbol.getName(), symbol.getId().getId(), recentTrade));
            }
        }
        //todo comparator sort etc
        Collections.sort();
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
    public void subscribeOnPrice(@NonNull SymbolId id) {
        symbolService.addPriceSubscription(id);
    }

    @Override
    @Transactional
    public void subscribeOnPrice(@NonNull String symbolName) {
        Supplier<ObjectNotFoundException> supplier = new Supplier<>() {
            @Override
            public ObjectNotFoundException get() {
                return new ObjectNotFoundException("Symbol " + symbolName + " not found ");
            }
        };
        SymbolEntity symbolEntity = symbolRepository.findBySymbolName(symbolName).orElseThrow(supplier);
        subscribeOnPrice(SymbolId.of(symbolEntity.getSymbolId()));
    }

    @Override
    public void unsubscribeOnPrice(@NonNull SymbolId id) {
        symbolService.removePriceSubscription(id);
    }

    @Override
    public void unsubscribeOnPrice(@NonNull String symbolName) {
        Supplier<ObjectNotFoundException> supplier = new Supplier<>() {
            @Override
            public ObjectNotFoundException get() {
                return new ObjectNotFoundException("Symbol '" + symbolName + "' not found ");
            }
        };
        SymbolEntity symbolEntity = symbolRepository.findBySymbolName(symbolName).orElseThrow(supplier);
        unsubscribeOnPrice(SymbolId.of(symbolEntity.getSymbolId()));
    }

    @Override
    public List<Symbol> listSubscribtionOnPrices() {
        return symbolService.getListOfPriceSubscriptions();
    }

}
