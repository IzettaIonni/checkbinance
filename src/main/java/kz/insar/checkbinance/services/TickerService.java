package kz.insar.checkbinance.services;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolId;

import java.util.List;

public interface TickerService {
    List<LastPriceDTO> lastPrices(List<String> symbols);

    List<LastPriceDTO> lastPrices(List<String> symbols, int limit);

    ExchangeInfoBySymbolsDTO exchangeInfo(List<String> symbols);

    ExchangeInfoBySymbolsDTO exchangeInfo();

    List<String> updateSymbols();

    List<Symbol> listSymbols();

    void subscribeOnPrice(SymbolId id);

    void unsubscribeOnPrice(SymbolId id);

    List<Symbol> listSubscribtionOnPrices();
}
