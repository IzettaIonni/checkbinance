package kz.insar.checkbinance.helpers.symbol;

import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolCreate;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.services.SymbolService;
import kz.insar.checkbinance.services.TickerService;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Builder(toBuilder = true)
public class TestSymbolIssuerCore implements TestSymbolRepositoryImpl.TestSymbolIssuer {

    @NonNull
    private final SymbolService symbolService;
    @NonNull
    private final TickerService tickerService;

    @Override
    public Symbol createSymbol(TestSymbol testSymbol) {
        return symbolService.createSymbol(SymbolCreate.builder()
                .name(testSymbol.getName())
                .quotePrecision(testSymbol.getQuotePrecision())
                .quoteAsset(testSymbol.getQuoteAsset())
                .baseAsset(testSymbol.getBaseAsset())
                .baseAssetPrecision(testSymbol.getBaseAssetPrecision())
                .quoteAssetPrecision(testSymbol.getQuoteAssetPrecision())
                .status(testSymbol.getStatus())
                .build());
    }

    @Override
    public void deleteSymbol(SymbolId id) {
        symbolService.deleteSymbol(id);
    }

    @Override
    public void subscribeSymbol(TestSymbol testSymbol) {
        tickerService.subscribeOnPrice(testSymbol.getId());
    }

    @Override
    public void unsubscribeSymbol(TestSymbol testSymbol) {
        tickerService.unsubscribeOnPrice(testSymbol.getId());
    }

    @Override
    public boolean isSymbolSubscribed(SymbolId symbolId) {
        return tickerService.listSubscriptionOnPrices().stream().map(Symbol::getId).anyMatch(symbolId::equals);
    }

}
