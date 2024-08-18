package kz.insar.checkbinance.helpers.symbol;

import kz.insar.checkbinance.client.SymbolStatus;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class TestSymbolCreator<T extends TestSymbolRepository<T>> {
    @NonNull
    private final T repository;

    private String name;
    private SymbolStatus status;
    private String baseAsset;
    private int baseAssetPrecision;
    private String quoteAsset;
    private int quotePrecision;
    private int quoteAssetPrecision;

    @SuppressWarnings("unchecked")
    public <R extends TestSymbolCreator<T>> R castTo(Class<R> to) {
        return (R) this;
    }

    private String getRandomString(int limit) {
        return RandomStringUtils.randomAlphabetic(limit);
    }

    private String getRandomString() {
        return getRandomString(ThreadLocalRandom.current().nextInt(256));
    }

    public SymbolStatus getRandomSymbolStatus() {
        int pick = ThreadLocalRandom.current().nextInt(SymbolStatus.values().length);
        return SymbolStatus.values()[pick];
    }

    public TestSymbolCreator<T> withName(String name) {
        this.name = name;
        return this;
    }

    public TestSymbolCreator<T> withRandomName() {
        for (int i = 0; i < 10; i++) {
            var name = getRandomString(32);
            if (!repository.isSymbolPresent(name)) {
                return withName(name);
            }
        }
        throw new IllegalStateException();
    }

    public TestSymbolCreator<T> withNullName() {
        return withName(null);
    }

    public TestSymbolCreator<T> withStatus(SymbolStatus status) {
        this.status = status;
        return this;
    }

    public TestSymbolCreator<T> withRandomStatus() {
        return withStatus(getRandomSymbolStatus());
    }

    public TestSymbolCreator<T> withNullStatus() {
        return withStatus(null);
    }

    public TestSymbolCreator<T> withBaseAsset(String baseAsset) {
        this.baseAsset = baseAsset;
        return this;
    }

    public TestSymbolCreator<T> withRandomBaseAsset() {
        return withBaseAsset(getRandomString(32));
    }

    public TestSymbolCreator<T> withNullBaseAsset() {
        return withBaseAsset(null);
    }

    public TestSymbolCreator<T> withBaseAssetPrecision(int baseAssetPrecision) {
        this.baseAssetPrecision = baseAssetPrecision;
        return this;
    }

    public TestSymbolCreator<T> withRandomBaseAssetPrecision() {
        return withBaseAssetPrecision(ThreadLocalRandom.current().nextInt());
    }

    public TestSymbolCreator<T> withNullBaseAssetPrecision() {
        return withBaseAssetPrecision(0);
    }

    public TestSymbolCreator<T> withQuoteAsset(String quoteAsset) {
        this.quoteAsset = quoteAsset;
        return this;
    }

    public TestSymbolCreator<T> withRandomQuoteAsset() {
        return withQuoteAsset(getRandomString(32));
    }

    public TestSymbolCreator<T> withNullQuoteAsset() {
        return withQuoteAsset(null);
    }

    public TestSymbolCreator<T> withQuotePrecision(int quotePrecision) {
        this.quotePrecision = quotePrecision;
        return this;
    }

    public TestSymbolCreator<T> withRandomQuotePrecision() {
        return withQuotePrecision(ThreadLocalRandom.current().nextInt());
    }

    public TestSymbolCreator<T> withNullQuotePrecision() {
        return withQuotePrecision(0);
    }

    public TestSymbolCreator<T> withQuoteAssetPrecision(int quoteAssetPrecision) {
        this.quoteAssetPrecision = quoteAssetPrecision;
        return this;
    }


    public TestSymbolCreator<T> withRandomQuoteAssetPrecision() {
        return withQuoteAssetPrecision(ThreadLocalRandom.current().nextInt());
    }

    public TestSymbolCreator<T> withNullQuoteAssetPrecision() {
        return withQuoteAssetPrecision(0);
    }

    public TestSymbolCreator<T> withRandomParams() {
        withRandomName();
        withRandomStatus();
        withRandomBaseAsset();
        withRandomBaseAssetPrecision();
        withRandomQuoteAsset();
        withRandomQuotePrecision();
        withRandomQuoteAssetPrecision();
        return this;
    }

    public T create() {
        repository.createSymbol(TestSymbol.builder()
                .repository(repository)
                .name(name)
                .status(status)
                .baseAsset(baseAsset)
                .baseAssetPrecision(baseAssetPrecision)
                .quoteAsset(quoteAsset)
                .quotePrecision(quotePrecision)
                .quoteAssetPrecision(quoteAssetPrecision).build());
        return repository;
    }
}
