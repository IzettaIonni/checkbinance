package kz.insar.checkbinance.helpers.trade;

import java.util.List;

public interface BinanceTradeIdRepositoryDelegate<T extends BinanceTradeIdRepositoryDelegate<T>> extends BinanceTradeIdRepository<T> {
    BinanceTradeIdRepository<?> getBinanceTradeIdRepository();
    T getSelf();

    @Override
    default T createBinanceTradeId() {
        getBinanceTradeIdRepository().createBinanceTradeId();
        return getSelf();
    }
    @Override
    default List<Long> getBinanceTradeIds() {
        return getBinanceTradeIdRepository().getBinanceTradeIds();
    }
    @Override
    default Long getBinanceTradeId(int creationIndex) {
        return getBinanceTradeIdRepository().getBinanceTradeId(creationIndex);
    }
    @Override
    default T cleanBinanceTradeIds() {
        getBinanceTradeIdRepository().cleanBinanceTradeIds();
        return getSelf();
    }

    @Override
    default int getIdsCount() {
        return getBinanceTradeIdRepository().getIdsCount();
    }

    @Override
    default boolean isBinanceTradeIdPresent(Long id) {
        return getBinanceTradeIdRepository().isBinanceTradeIdPresent(id);
    }
    @Override
    default boolean isBinanceTradeIdDuplicated(Long id) {
        return getBinanceTradeIdRepository().isBinanceTradeIdDuplicated(id);
    }
    @Override
    default Long getFirstTradeId() {
        return getBinanceTradeIdRepository().getFirstTradeId();
    }
    @Override
    default Long getLastTradeId() {
        return getBinanceTradeIdRepository().getLastTradeId();
    }

}
