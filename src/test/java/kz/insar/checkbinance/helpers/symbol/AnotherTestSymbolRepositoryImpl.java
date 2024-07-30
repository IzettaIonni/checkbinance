package kz.insar.checkbinance.helpers.symbol;

import kz.insar.checkbinance.domain.SymbolId;

import java.util.List;

public class AnotherTestSymbolRepositoryImpl implements TestSymbolRepository<AnotherTestSymbolRepositoryImpl>{
    @Override
    public AnotherTestSymbolRepositoryImpl createSymbol(TestSymbol testSymbol) {
        return null;
    }

    @Override
    public AnotherTestSymbolRepositoryImpl subscribeSymbol(TestSymbol testSymbol) {
        return null;
    }

    @Override
    public AnotherTestSymbolRepositoryImpl unsubscribeSymbol(TestSymbol testSymbol) {
        return null;
    }

    @Override
    public int getSymbolCount() {
        return 0;
    }

    @Override
    public List<TestSymbol> getSymbols() {
        return null;
    }

    @Override
    public TestSymbol getSymbol(int creationIndex) {
        return null;
    }

    @Override
    public SymbolId getSymbolId(int creationIndex) {
        return null;
    }

    @Override
    public SymbolId getSymbolId(TestSymbol testSymbol) {
        return null;
    }
}
