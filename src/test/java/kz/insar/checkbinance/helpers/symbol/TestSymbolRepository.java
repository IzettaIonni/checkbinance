package kz.insar.checkbinance.helpers.symbol;

import kz.insar.checkbinance.domain.SymbolId;
import org.junit.jupiter.api.Test;

import java.util.List;

public interface TestSymbolRepository<T extends TestSymbolRepository<T>> {

    T createSymbol(TestSymbol testSymbol);

    T subscribeSymbol(TestSymbol testSymbol);
    T unsubscribeSymbol(TestSymbol testSymbol);

    int getSymbolCount();
    List<TestSymbol> getSymbols();
    TestSymbol getSymbol(int creationIndex);
    SymbolId getSymbolId(int creationIndex);
    SymbolId getSymbolId(TestSymbol testSymbol);

    default TestSymbol getFirstSymbol() {
        return getSymbol(0);
    }

    default TestSymbol getLastSymbol() {
        return getSymbol(getSymbolCount()-1);
    }

}
