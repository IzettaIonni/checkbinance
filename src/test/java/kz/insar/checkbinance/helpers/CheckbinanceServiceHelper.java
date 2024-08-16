package kz.insar.checkbinance.helpers;

import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.RecentTradeDTO;
import kz.insar.checkbinance.client.SymbolPriceDTO;
import kz.insar.checkbinance.client.SymbolStatus;
import kz.insar.checkbinance.containers.BNBExchangeInfoResponse;
import kz.insar.checkbinance.containers.BNBLastPriceResponse;
import kz.insar.checkbinance.containers.BNBLegacyLastPriceResponse;
import kz.insar.checkbinance.containers.RecentTradesWithSymbol;
import kz.insar.checkbinance.converters.ApiConverter;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolCreate;
import kz.insar.checkbinance.helpers.symbol.*;
import kz.insar.checkbinance.services.SymbolService;
import kz.insar.checkbinance.services.TickerService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class CheckbinanceServiceHelper implements TestSymbolRepositoryDelegate<CheckbinanceServiceHelper>,
        TestSymbolBuilders<CheckbinanceServiceHelper> {

    @NonNull
    private final SymbolService symbolService;
    @NonNull
    private final TickerService tickerService;
    @NonNull
    private final TestSymbolRepository<?> testSymbolRepository;
    @Getter
    private final List<Long> binanceTradeIds = new ArrayList<>();

    @Autowired
    public CheckbinanceServiceHelper(SymbolService symbolService, TickerService tickerService) {
        this(symbolService, tickerService,
                TestSymbolRepositoryImpl.builder().withCoreIssuer(symbolService, tickerService).build()
        );
    }

    public BNBLastPriceResponse.BNBLastPriceResponseBuilder createBNBLastPriceResponseBuilder() {
        return BNBLastPriceResponse.builder().symbolIdExtractor(this);
    }

    public BNBLegacyLastPriceResponse.BNBLegacyLastPriceResponseBuilder createBNBLegacyLastPriceResponseBuilder() {
        return BNBLegacyLastPriceResponse.builder().idGenerator(this).symbolIdExtractor(this);
    }

    public Long createBinanceTradeId() { //todo is it good idea to contain ids in Map<String, Long> with symbol name?
        long id = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        while (binanceTradeIds.contains(id)) id = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        return id;
    }

    public boolean isBinanceTradeIdPresent(Long id) {
        return binanceTradeIds.contains(id);
    }

    public boolean isBinanceTradeIdDuplicated(Long id) {
        return binanceTradeIds.stream().filter(id::equals).count() > 1;
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
    public CheckbinanceServiceHelper getSelf() {
        return this;
    }
//    @Override
//    public CheckbinanceServiceHelper createSymbol(TestSymbol testSymbol) {
//        testSymbolRepository.createSymbol(testSymbol);
//        return this;
//    }
//
//    @Override
//    public CheckbinanceServiceHelper subscribeSymbol(TestSymbol testSymbol) {
//        testSymbolRepository.subscribeSymbol(testSymbol);
//        return this;
//    }
//
//    @Override
//    public CheckbinanceServiceHelper unsubscribeSymbol(TestSymbol testSymbol) {
//        testSymbolRepository.unsubscribeSymbol(testSymbol);
//        return this;
//    }

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
