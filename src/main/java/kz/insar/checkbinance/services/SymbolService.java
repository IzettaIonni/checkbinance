package kz.insar.checkbinance.services;

import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolCreate;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.domain.SymbolUpdate;

import java.util.List;

public interface SymbolService {
    Symbol createSymbol(SymbolCreate request);

    Symbol updateSymbol(SymbolUpdate request);

    void deleteSymbol(SymbolId request);

    List<Symbol> getSymbols();

    void addPriceSubscription(SymbolId request);

    void removePriceSubscription(SymbolId request);

    List<Symbol> getListOfPriceSubscriptions();

}
