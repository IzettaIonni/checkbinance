package kz.insar.checkbinance.helpers.trade;

import java.util.List;

public interface BinanceTradeIdRepository<T extends BinanceTradeIdRepository<T>> {
    T createBinanceTradeId();
    List<Long> getBinanceTradeIds();
    Long getBinanceTradeId(int creationIndex);
    T cleanBinanceTradeIds();

    default int getIdsCount() {
        return getBinanceTradeIds().size();
    }

    default boolean isBinanceTradeIdPresent(Long id) {
        return getBinanceTradeIds().contains(id);
    }
    default boolean isBinanceTradeIdDuplicated(Long id) {
        return getBinanceTradeIds().stream().filter(id::equals).count() > 1;
    }
    default Long getFirstTradeId() {
        return getBinanceTradeId(0);
    }
    default Long getLastTradeId() {
        return getBinanceTradeId(-1);
    }
}
