package kz.insar.checkbinance.helpers;

import kz.insar.checkbinance.client.SymbolStatus;
import kz.insar.checkbinance.containers.BNBLastPriceResponse;
import kz.insar.checkbinance.containers.BNBLegacyLastPriceResponse;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolCreate;
import kz.insar.checkbinance.helpers.symbol.*;
import kz.insar.checkbinance.helpers.trade.BinanceTradeIdRepository;
import kz.insar.checkbinance.helpers.trade.BinanceTradeIdRepositoryDelegate;
import kz.insar.checkbinance.helpers.trade.BinanceTradeIdRepositoryImpl;
import kz.insar.checkbinance.services.SymbolService;
import kz.insar.checkbinance.services.TickerService;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class CheckbinanceServiceHelper implements TestSymbolRepositoryDelegate<CheckbinanceServiceHelper>,
        TestSymbolBuilders<CheckbinanceServiceHelper>, BinanceTradeIdRepositoryDelegate<CheckbinanceServiceHelper> {

    @NonNull
    private final SymbolService symbolService;
    @NonNull
    private final TickerService tickerService;
    @NonNull
    private final TestSymbolRepository<?> testSymbolRepository;
    @NonNull
    private final BinanceTradeIdRepositoryImpl binanceTradeIdRepository;

    @Autowired
    public CheckbinanceServiceHelper(SymbolService symbolService, TickerService tickerService) {
        this(
                symbolService,
                tickerService,
                TestSymbolRepositoryImpl.builder().withCoreIssuer(symbolService, tickerService).build(),
                new BinanceTradeIdRepositoryImpl()
        );
    }

    public BNBLastPriceResponse.BNBLastPriceResponseBuilder createBNBLastPriceResponseBuilder() {
        return BNBLastPriceResponse.builder().symbolIdExtractor(this);
    }

    public BNBLegacyLastPriceResponse.BNBLegacyLastPriceResponseBuilder createBNBLegacyLastPriceResponseBuilder() {
        return BNBLegacyLastPriceResponse.builder().idGenerator(this).symbolIdExtractor(this);
    }

    @Override
    public TestSymbolCreator<CheckbinanceServiceHelper> buildSymbol() {
        return new TestSymbolCreator<>(this);
    }

    @Override
    public TestSymbolRepository<?> getSymbolRepository() {
        return testSymbolRepository;
    }

    @Override
    public BinanceTradeIdRepository<?> getBinanceTradeIdRepository() {
        return binanceTradeIdRepository;
    }

    @Override
    public CheckbinanceServiceHelper getSelf() {
        return this;
    }

    @Deprecated
    public SymbolStatus getRandomSymbolStatus() {
        int pick = ThreadLocalRandom.current().nextInt(SymbolStatus.values().length);
        return SymbolStatus.values()[pick];
    }

    @Deprecated
    public Symbol createSymbol(String name) {
        return symbolService.createSymbol(SymbolCreate.builder()
                .name(name)
                .quotePrecision(ThreadLocalRandom.current().nextInt())
                .quoteAsset(RandomStringUtils.random(32))
                .baseAsset(RandomStringUtils.random(32))
                .baseAssetPrecision(ThreadLocalRandom.current().nextInt())
                .quoteAssetPrecision(ThreadLocalRandom.current().nextInt())
                .status(getRandomSymbolStatus())
                .build());
    }

    @Deprecated
    public List<Symbol> createSymbols(List<String> names) {
        List<Symbol> symbols = new ArrayList<>();
        for (var name : names) {
            symbols.add(createSymbol(name));
        }
        return symbols;
    }

    @Deprecated
    public Symbol subscribeOnSymbol(Symbol symbol) {
        tickerService.subscribeOnPrice(symbol);
        return symbol;
    }

    @Deprecated
    public List<Symbol> subscribeOnSymbols(List<Symbol> symbols) {
        for (var symbol : symbols)
            subscribeOnSymbol(symbol);
        return symbols;
    }

    @Deprecated
    public List<Symbol> createAndSubscribeSymbols(List<String> names) {
        return subscribeOnSymbols(createSymbols(names));
    }
}
