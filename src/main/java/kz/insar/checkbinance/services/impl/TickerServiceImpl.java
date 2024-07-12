package kz.insar.checkbinance.services.impl;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.BinanceClient;
import kz.insar.checkbinance.client.RecentTradeDTO;
import kz.insar.checkbinance.client.SymbolDTO;
import kz.insar.checkbinance.converters.ApiConverter;
import kz.insar.checkbinance.domain.sort.comparators.LastPriceDTOComparator;
import kz.insar.checkbinance.domain.sort.params.LastPriceColumns;
import kz.insar.checkbinance.domain.sort.params.SortParams;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.domain.exeptions.ObjectNotFoundException;
import kz.insar.checkbinance.repositories.SymbolRepository;
import kz.insar.checkbinance.repositories.entities.SymbolEntity;
import kz.insar.checkbinance.services.SymbolService;
import kz.insar.checkbinance.services.TickerService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class TickerServiceImpl implements TickerService {

    @NonNull
    private final BinanceClient binanceClient;

    @NonNull
    private final ApiConverter apiConverter;

    @NonNull
    private final SymbolService symbolService;

    @NonNull
    private final SymbolRepository symbolRepository;

    @Override
    public List<LastPriceDTO> lastPrices(SortParams<LastPriceColumns> sortParams) {
        List<Symbol> subscriptions = listSubscriptionOnPrices();
        List<LastPriceDTO> prices = new ArrayList<>();
        if (subscriptions == null || subscriptions.isEmpty()) return prices;
        prices = apiConverter.toApi(binanceClient.getPrices(apiConverter.toDomainRequest(subscriptions)), subscriptions);
        LastPriceDTOComparator sort = new LastPriceDTOComparator(sortParams.getDir(), sortParams.getColumn());
        prices.sort(sort);
        return prices;
    }

    @Override
    public List<LastPriceDTO> legacyLastPrices(int limit, SortParams<LastPriceColumns> sortParams) {
        List<Symbol> subscriptions = listSubscriptionOnPrices();
        List<LastPriceDTO> prices = new ArrayList<>();
        if (subscriptions == null || subscriptions.isEmpty()) return prices;
        for (Symbol symbol : subscriptions) {
            var recentTrades = binanceClient.getRecentTrades(symbol.getName(), limit);
            for (RecentTradeDTO recentTrade : recentTrades) {
                prices.add(apiConverter.toApi(symbol.getName(), symbol.getId().getId(), recentTrade));
            }
        }
        LastPriceDTOComparator sort = new LastPriceDTOComparator(sortParams.getDir(), sortParams.getColumn());
        prices.sort(sort);
        return prices;
    }

    @Override
    public List<LastPriceDTO> legacyLastPrices(SortParams<LastPriceColumns> sortParams) {
        return legacyLastPrices(1, sortParams);
    }


    @Override
    public ExchangeInfoBySymbolsDTO exchangeInfo(List<String> symbols) {
        return apiConverter.toApi(binanceClient.getExchangeInfoBySymbols(symbols));
    }

    @Override
    public ExchangeInfoBySymbolsDTO exchangeInfo() {
        return apiConverter.toApi(binanceClient.getExchangeInfo());
    }

    @Override
    public  List<String> updateSymbols() {
        var exchangeInfo = binanceClient.getExchangeInfo();
        Map<String, Symbol> existSymbols = symbolService.getSymbols()
                .stream().collect(Collectors.toMap(Symbol::getName, Function.identity()));
        List<String> result = new ArrayList<>();
        for (SymbolDTO params : exchangeInfo.getSymbols()) {
            if (existSymbols.containsKey(params.getSymbol())) {
                //Update
                var request = apiConverter.toDomainUpdate(existSymbols.get(params.getSymbol()), params);
                result.add(symbolService.updateSymbol(request).getName());
            }
            else {
                //Create
                var request = apiConverter.toDomainCreate(params);
                result.add(symbolService.createSymbol(request).getName());
            }
        }
        return result;
    }

    @Override
    public List<Symbol> listSymbols() {
        return symbolService.getSymbols();
    }

    @Override
    public void subscribeOnPrice(@NonNull SymbolId id) {

        SymbolEntity symbolEntity = symbolRepository.
                findById(id).orElseThrow(() -> new ObjectNotFoundException("Symbol " + id + " not found "));

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
    public List<Symbol> listSubscriptionOnPrices() {
        return symbolService.getListOfPriceSubscriptions();
    }

}
