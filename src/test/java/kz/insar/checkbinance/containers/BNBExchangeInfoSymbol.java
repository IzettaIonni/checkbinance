package kz.insar.checkbinance.containers;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.SymbolParamsDTO;
import kz.insar.checkbinance.client.SymbolStatus;
import kz.insar.checkbinance.helpers.symbol.TestSymbol;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class BNBExchangeInfoSymbol {
    final private String symbol;
    final private SymbolStatus status;
    final private String baseAsset;
    final private Integer baseAssetPrecision;
    final private String quoteAsset;
    final private Integer quotePrecision;
    final private Integer quoteAssetPrecision;

    public static BNBExchangeInfoSymbol of(TestSymbol testSymbol) {
        return BNBExchangeInfoSymbol.builder().from(testSymbol).build();
    }

    public SymbolParamsDTO toSymbolParamsDTO() {
        SymbolParamsDTO result = new SymbolParamsDTO();
        result.setSymbol(symbol);
        result.setStatus(status);
        result.setBaseAsset(baseAsset);
        result.setBaseAssetPrecision(baseAssetPrecision);
        result.setQuoteAsset(quoteAsset);
        result.setQuotePrecision(quotePrecision);
        result.setQuoteAssetPrecision(quoteAssetPrecision);
        return result;
    }

    public static class BNBExchangeInfoSymbolBuilder {
        public BNBExchangeInfoSymbolBuilder from(TestSymbol src) {
            return symbol(src.getName())
                    .status(src.getStatus())
                    .baseAsset(src.getBaseAsset())
                    .baseAssetPrecision(src.getBaseAssetPrecision())
                    .quoteAsset(src.getQuoteAsset())
                    .quotePrecision(src.getQuoteAssetPrecision())
                    .quoteAssetPrecision(src.getQuoteAssetPrecision());
        }
    }
}
