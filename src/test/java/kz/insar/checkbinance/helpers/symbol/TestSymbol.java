package kz.insar.checkbinance.helpers.symbol;

import kz.insar.checkbinance.client.SymbolStatus;
import kz.insar.checkbinance.domain.SymbolId;
import lombok.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
@Getter
public class TestSymbol {

    @NonNull
    private final TestSymbolRepository<?> repository;
    private final AtomicBoolean issued = new AtomicBoolean(false);

    private final String name;
    private final SymbolStatus status;
    private final String baseAsset;
    private final int baseAssetPrecision;
    private final String quoteAsset;
    private final int quotePrecision;
    private final int quoteAssetPrecision;

    public SymbolId getId() {
        return repository.getSymbolId(this);
    }
    public TestSymbol markIssued() {
        issued.set(true);
        return this;
    }

    public TestSymbol subscribe() {
        repository.subscribeSymbol(this);
        return this;
    }

    public TestSymbol unsubscribe() {
        repository.unsubscribeSymbol(this);
        return this;
    }

}
