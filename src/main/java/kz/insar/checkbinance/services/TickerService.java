package kz.insar.checkbinance.services;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.domain.sort.params.LastPriceColumns;
import kz.insar.checkbinance.domain.sort.params.SortParams;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolId;

import java.util.List;

public interface TickerService {
    List<LastPriceDTO> lastPrices(SortParams<LastPriceColumns> sortParams);

    List<LastPriceDTO> lastPrices(int limit, SortParams<LastPriceColumns> sortParams);

    ExchangeInfoBySymbolsDTO exchangeInfo(List<String> symbols);

    ExchangeInfoBySymbolsDTO exchangeInfo();

    List<String> updateSymbols();

    List<Symbol> listSymbols();

    void subscribeOnPrice(SymbolId id);

    void subscribeOnPrice(String symbolName);

    void unsubscribeOnPrice(SymbolId id);

    void unsubscribeOnPrice(String symbolName);

    List<Symbol> listSubscribtionOnPrices();

}
