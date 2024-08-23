package kz.insar.checkbinance.helpers.symbol;

public interface TestSymbolBuilders<T extends TestSymbolRepository<T>> {
    TestSymbolCreator<T> buildSymbol();
    default T createRandomSymbol() {
        return buildSymbol().withRandomParams().create();
    }
    default TestSymbol createAndGetRandomSymbol() {
        return createRandomSymbol().getLastSymbol();
    }
    default T createRandomSymbols(int count) {
        T result = null;
        for (int i = 0; i < count; i++)
            result = createRandomSymbol();
        return result;
    }
    default T createAndSubscribeSymbol(String symbolName) {
        var repository = buildSymbol().withRandomParams().withName(symbolName).create();
        return repository.subscribeSymbol(repository.getLastSymbol());
    }

    default T createAndSubscribeRandomSymbols(int count) {
        T result = null;
        for (int i = 0; i < count; i++) {
            result = createRandomSymbol();
            result.subscribeSymbol(result.getLastSymbol());
        }
        return result;
    }

    default T createAndSubscribeRandomSymbol() {
        return createAndSubscribeRandomSymbols(1);
    }
}
