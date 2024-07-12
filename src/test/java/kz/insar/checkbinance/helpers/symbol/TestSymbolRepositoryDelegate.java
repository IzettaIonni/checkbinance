package kz.insar.checkbinance.helpers.symbol;

import kz.insar.checkbinance.domain.SymbolId;
import org.junit.jupiter.api.Test;

import java.util.List;

public interface TestSymbolRepositoryDelegate<T extends TestSymbolRepository<T>> extends TestSymbolRepository<T> {

    TestSymbolRepository<?> getSymbolRepository();
    @Override
    default int getSymbolCount() {
        return getSymbolRepository().getSymbolCount();
    }
    @Override
    default List<TestSymbol> getSymbols() {
        return getSymbolRepository().getSymbols();
    }
    @Override
    default TestSymbol getSymbol(int creationIndex) {
        return getSymbolRepository().getSymbol(creationIndex);
    }
    @Override
    default SymbolId getSymbolId(int creationIndex) {
        return getSymbolRepository().getSymbolId(creationIndex);
    }
    @Override
    default SymbolId getSymbolId(TestSymbol testSymbol) {
        return getSymbolRepository().getSymbolId(testSymbol);
    }
    @Override
    default TestSymbol getFirstSymbol() {
        return getSymbolRepository().getSymbol(0);
    }
    @Override
    default TestSymbol getLastSymbol() {
        return getSymbolRepository().getSymbol(getSymbolCount()-1);
    }

}
