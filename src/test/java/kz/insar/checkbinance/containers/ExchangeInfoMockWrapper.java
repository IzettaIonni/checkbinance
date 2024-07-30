package kz.insar.checkbinance.containers;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.SymbolParamsDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class ExchangeInfoMockWrapper {
    @NonNull
    private final String symbol;
    @NonNull
    private Long serverTime;
    @NonNull
    private List<SymbolParamsMockWrapper> symbols;

    public ExchangeInfoMockWrapper(@NonNull String symbol,
                                   @NonNull Long serverTime,
                                   @NonNull List<SymbolParamsMockWrapper> symbols) {
        if (symbol.length() > 64) throw new IllegalArgumentException("Too long symbol");
        this.symbol = symbol;
        this.serverTime = serverTime;
        this.symbols = symbols;
    }

    public ExchangeInfoBySymbolsDTO toExchangeInfoBySymbolsDTO() {
        var convertedSymbols = new ArrayList<SymbolParamsDTO>();
        for (var symbol : symbols)
            convertedSymbols.add(symbol.toSymbolParamsDTO());
        return new ExchangeInfoBySymbolsDTO(serverTime, convertedSymbols);
    }
}
