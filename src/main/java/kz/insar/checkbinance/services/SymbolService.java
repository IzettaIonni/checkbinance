package kz.insar.checkbinance.services;

import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolCreate;
import kz.insar.checkbinance.domain.SymbolUpdate;

import java.util.List;

public interface SymbolService {
    Symbol createSymbol(SymbolCreate request);

    Symbol updateSymbol(SymbolUpdate request);

    List<Symbol> getSymbols();

}
