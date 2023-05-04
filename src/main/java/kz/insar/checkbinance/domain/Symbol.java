package kz.insar.checkbinance.domain;

import kz.insar.checkbinance.client.SymbolStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder(toBuilder = true)
public class Symbol {

    @NonNull private final SymbolId id;
    @NonNull private final String name;
    @NonNull private final SymbolStatus status;
    @NonNull private final String baseAsset;
    private final int baseAssetPrecision;
    @NonNull private final String quoteAsset;
    private final int quotePrecision;
    private final int quoteAssetPrecision;

}
