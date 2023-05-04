package kz.insar.checkbinance.services;

import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolCreate;

public interface SymbolService {
    Symbol createSymbol(SymbolCreate request);
}
