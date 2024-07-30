package kz.insar.checkbinance.containers;

import kz.insar.checkbinance.client.RecentTradeDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class LegacyLastPriceMockWrapper {
    @NonNull
    private final String symbol;
    private final int requestLimit;
    @NonNull
    private final List<RecentTradesMockWrapper> recentTrades;

    public LegacyLastPriceMockWrapper(@NonNull String symbol, int requestLimit,
                                      @NonNull List<RecentTradesMockWrapper> recentTrades) {
        if (symbol.length() > 64) throw new IllegalArgumentException("Too long symbol");
        this.symbol = symbol;
        this.requestLimit = requestLimit;
        this.recentTrades = recentTrades;
    }

    public RecentTradesWithSymbol toRecentTradesWithSymbol() {
        var convertedRecentTrades = new ArrayList<RecentTradeDTO>();
        for (var recentTrade : recentTrades) {
            convertedRecentTrades.add(recentTrade.toRecentTradeDTO());
        }
        return RecentTradesWithSymbol.builder().symbol(symbol).recentTrades(convertedRecentTrades).build();
    }
}
