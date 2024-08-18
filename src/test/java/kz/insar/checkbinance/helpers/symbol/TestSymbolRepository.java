package kz.insar.checkbinance.helpers.symbol;

import kz.insar.checkbinance.domain.SymbolId;

import java.util.ArrayList;
import java.util.List;

public interface TestSymbolRepository<T extends TestSymbolRepository<T>> {

    T createSymbol(TestSymbol testSymbol);
    T deleteSymbol(SymbolId id);
    default T deleteSymbol(TestSymbol testSymbol) {
        return deleteSymbol(testSymbol.getId());
    }
    default T deleteSymbol(int index) {
        return deleteSymbol(getSymbolId(index));
    }

    T subscribeSymbol(TestSymbol testSymbol);
    T unsubscribeSymbol(TestSymbol testSymbol);

    T cleanTestSymbols();

    default boolean isSymbolPresent(String symbolName) {
        return getSymbols().stream().map(TestSymbol::getName).anyMatch(s -> s.equals(symbolName));
    }
    default boolean isSymbolPresent(TestSymbol testSymbol) {
        return isSymbolPresent(testSymbol.getName());
    }
    default boolean isSymbolPresent(SymbolId id) {
        return isSymbolPresent(getSymbol(id).getName());
    }
    default boolean isSymbolDuplicated(String symbol) {
        return getSymbols().stream().map(TestSymbol::getName)
                .filter(testSymbolName -> testSymbolName.equals(symbol)).count() > 1;
    }
    default boolean isSymbolDuplicated(TestSymbol testSymbol) {
        return isSymbolDuplicated(testSymbol.getName());
    }

    default int getSymbolsCount() {
        return getSymbols().size();
    }
    List<TestSymbol> getSymbols();
    TestSymbol getSymbol(int creationIndex);
    default TestSymbol getSymbol(SymbolId id) {
        return getSymbols().stream().filter(testSymbol -> testSymbol.getId().equals(id)).findFirst().orElseThrow();
    }
    SymbolId getSymbolId(int creationIndex);
    SymbolId getSymbolId(TestSymbol testSymbol);
    default SymbolId getSymbolId(String symbolName) {
        return getSymbols().stream().filter((symbol) -> symbol.getName().equals(symbolName)).map(TestSymbol::getId).findFirst().orElseThrow();
    }

    default int normalizeCreationIndex(int creationIndex) {
        if (creationIndex < 0) {
            return getSymbolsCount() + creationIndex;
        }
        else return creationIndex;
    }

    default TestSymbol getFirstSymbol() {
        return getSymbol(0);
    }

    default TestSymbol getLastSymbol() {
        return getSymbol(getSymbolsCount()-1);
    }

    /**
     * @param indexFrom include
     * @param indexTo include
     */
    default List<String> getSymbolNames(int indexFrom, int indexTo) {
        var result = new ArrayList<String>();
        indexFrom = normalizeCreationIndex(indexFrom);
        indexTo = normalizeCreationIndex(indexTo);
        for (int i = indexFrom; i <= indexTo; i++) {
            result.add(getSymbol(i).getName());
        }
        return result;
    }

    default String getSymbolName(int index) {
        return getSymbolNames(index, index).get(0);
    }

}
