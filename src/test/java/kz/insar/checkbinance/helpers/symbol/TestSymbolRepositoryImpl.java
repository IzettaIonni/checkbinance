package kz.insar.checkbinance.helpers.symbol;

import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.services.SymbolService;
import kz.insar.checkbinance.services.TickerService;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class TestSymbolRepositoryImpl implements TestSymbolRepository<TestSymbolRepositoryImpl>{

    @NonNull
    private final TestSymbolIssuer issuer;
    private final List<TestSymbol> symbols = new ArrayList<>();
    private final List<SymbolId> symbolIds = new ArrayList<>();

    @Override
    public TestSymbolRepositoryImpl createSymbol(TestSymbol testSymbol) {
        if (isSymbolPresent(testSymbol)) {
            throw new IllegalArgumentException("Symbol is already present in repository");
        }
        var symbol = issuer.createSymbol(testSymbol);
        symbols.add(testSymbol);
        symbolIds.add(symbol.getId());
        testSymbol.markIssued();
        return this;
    }

    @Override
    public TestSymbolRepositoryImpl deleteSymbol(SymbolId id) {
        unsubscribeSymbol(getSymbol(id));
        issuer.deleteSymbol(id);
        symbols.remove(getSymbol(id));
        symbolIds.remove(id);
        return this;
    }

    @Override
    public TestSymbolRepositoryImpl subscribeSymbol(TestSymbol testSymbol) {
        issuer.subscribeSymbol(testSymbol);
        return this;
    }

    @Override
    public TestSymbolRepositoryImpl unsubscribeSymbol(TestSymbol testSymbol) {
        issuer.unsubscribeSymbol(testSymbol);
        return this;
    }

    @Override
    public boolean isSymbolSubscribed(SymbolId id) {
        return issuer.isSymbolSubscribed(id);
    }

    @Override
    public TestSymbolRepositoryImpl cleanTestSymbols() {
        var clone = new ArrayList<>(symbolIds);
        clone.forEach(this::deleteSymbol);
        return this;
    }

    @Override
    public List<TestSymbol> getSymbols() {
        return new ArrayList<>(symbols);
    }

    @Override
    public TestSymbol getSymbol(int creationIndex) {
        return symbols.get(normalizeCreationIndex(creationIndex));
    }

    @Override
    public SymbolId getSymbolId(int creationIndex) {
        return symbolIds.get(normalizeCreationIndex(creationIndex));
    }

    @Override
    public SymbolId getSymbolId(TestSymbol testSymbol) {
        var creationIndex = symbols.indexOf(testSymbol);
        if (creationIndex < 0) {
            throw new IllegalArgumentException("TestSymbol is not present in repository");
        }
        return symbolIds.get(creationIndex);
    }

    public interface TestSymbolIssuer {
        Symbol createSymbol(TestSymbol testSymbol);
        void deleteSymbol(SymbolId id);
        default void deleteSymbol(TestSymbol testSymbol) {
            deleteSymbol(testSymbol.getId());
        }
        void subscribeSymbol(TestSymbol testSymbol);
        void unsubscribeSymbol(TestSymbol testSymbol);
        boolean isSymbolSubscribed(SymbolId symbolId);
    }

    public static TestSymbolRepositoryImplBuilder builder() {
        return new TestSymbolRepositoryImplBuilder();
    }

    public static class TestSymbolRepositoryImplBuilder{
        private TestSymbolIssuer issuer;
        public TestSymbolRepositoryImplBuilder withIssuer(TestSymbolIssuer issuer) {
            this.issuer = issuer;
            return this;
        }
        public TestSymbolRepositoryImplBuilder withCoreIssuer(SymbolService symbolService, TickerService tickerService) {
             return withIssuer(TestSymbolIssuerCore.builder().symbolService(symbolService).tickerService(tickerService).build());
        }
        public TestSymbolRepositoryImpl build() {
            return new TestSymbolRepositoryImpl(issuer);
        }
    }

}
