package kz.insar.checkbinance.helpers.trade;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BinanceTradeIdRepositoryImpl implements BinanceTradeIdRepository<BinanceTradeIdRepositoryImpl>{

    private final List<Long> binanceTradeIds = new ArrayList<>();
    @Override
    public BinanceTradeIdRepositoryImpl createBinanceTradeId() {
        Long id;
        for (int i = 0; i < 100; i++) {
            id = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
            if (!binanceTradeIds.contains(id)) {
                binanceTradeIds.add(id);
                return this;
            }
        }
        throw new RuntimeException("Couldn't create an random id");
    }

    @Override
    public List<Long> getBinanceTradeIds() {
        return binanceTradeIds;
    }

    @Override
    public Long getBinanceTradeId(int creationIndex) {
        return binanceTradeIds.get(normalizeCreationIndex(creationIndex));
    }

    @Override
    public BinanceTradeIdRepositoryImpl cleanBinanceTradeIds() {
        binanceTradeIds.clear();
        return this;
    }

    private int normalizeCreationIndex(int creationIndex) {
        if (creationIndex < 0) {
            return getIdsCount() + creationIndex;
        }
        else return creationIndex;
    }

}
