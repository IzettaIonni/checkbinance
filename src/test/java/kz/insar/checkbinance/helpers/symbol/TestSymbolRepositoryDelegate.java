package kz.insar.checkbinance.helpers.symbol;

import kz.insar.checkbinance.domain.SymbolId;
import org.junit.jupiter.api.Test;

import java.util.List;

public interface TestSymbolRepositoryDelegate<T extends TestSymbolRepositoryDelegate<T>> extends TestSymbolRepository<T> {

    TestSymbolRepository<?> getSymbolRepository();
    T getSelf();

    @Override
    default T createSymbol(TestSymbol testSymbol) {
        getSymbolRepository().createSymbol(testSymbol);
        return getSelf();
    }
    @Override
    default T deleteSymbol(SymbolId id) {
        getSymbolRepository().deleteSymbol(id);
        return getSelf();
    }
    @Override
    default T subscribeSymbol(TestSymbol testSymbol) {
        getSymbolRepository().subscribeSymbol(testSymbol);
        return getSelf();
    }
    @Override
    default T unsubscribeSymbol(TestSymbol testSymbol) {
        getSymbolRepository().unsubscribeSymbol(testSymbol);
        return getSelf();
    }
    @Override
    default T cleanUp() {
        getSymbolRepository().cleanUp();
        return getSelf();
    }
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
    default SymbolId getSymbolId(String symbolName) {
        return getSymbolRepository().getSymbolId(symbolName);
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
