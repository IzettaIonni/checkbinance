package kz.insar.checkbinance.api;

import kz.insar.checkbinance.client.SymbolStatus;
import lombok.Builder;
import lombok.Data;

@Data
public class SymbolParamsDTO {
    private String symbol;
    private SymbolStatus status;
    private String baseAsset;
    private Integer baseAssetPrecision;
    private String quoteAsset;
    private Integer quotePrecision;
    private Integer quoteAssetPrecision;
}
