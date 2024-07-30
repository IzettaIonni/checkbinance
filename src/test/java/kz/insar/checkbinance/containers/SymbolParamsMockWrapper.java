package kz.insar.checkbinance.containers;

import kz.insar.checkbinance.api.SymbolParamsDTO;
import kz.insar.checkbinance.client.SymbolStatus;
import lombok.*;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SymbolParamsMockWrapper {
    private String symbol;
    private SymbolStatus status;
    private String baseAsset;
    private Integer baseAssetPrecision;
    private String quoteAsset;
    private Integer quotePrecision;
    private Integer quoteAssetPrecision;

    public SymbolParamsDTO toSymbolParamsDTO() {
         var symbolParamsDTO = new SymbolParamsDTO();
         symbolParamsDTO.setSymbol(symbol);
         symbolParamsDTO.setStatus(status);
         symbolParamsDTO.setBaseAsset(baseAsset);
         symbolParamsDTO.setBaseAssetPrecision(baseAssetPrecision);
         symbolParamsDTO.setQuoteAsset(quoteAsset);
         symbolParamsDTO.setQuotePrecision(quotePrecision);
         symbolParamsDTO.setQuoteAssetPrecision(quoteAssetPrecision);
         return symbolParamsDTO;
    }
}
