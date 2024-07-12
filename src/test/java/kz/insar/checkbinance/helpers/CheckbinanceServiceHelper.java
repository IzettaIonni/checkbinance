package kz.insar.checkbinance.helpers;

import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.RecentTradeDTO;
import kz.insar.checkbinance.client.SymbolPriceDTO;
import kz.insar.checkbinance.client.SymbolStatus;
import kz.insar.checkbinance.containers.RecentTradesWithSymbol;
import kz.insar.checkbinance.converters.ApiConverter;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolCreate;
import kz.insar.checkbinance.helpers.symbol.*;
import kz.insar.checkbinance.services.SymbolService;
import kz.insar.checkbinance.services.TickerService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component //todo is there more suitable bean type?
@AllArgsConstructor
public class CheckbinanceServiceHelper implements TestSymbolRepositoryDelegate<CheckbinanceServiceHelper>,
        TestSymbolBuilders<CheckbinanceServiceHelper> {

    @NonNull
    private final SymbolService symbolService;
    @NonNull
    private final TickerService tickerService;
    @NonNull
    private final ApiConverter apiConverter;
    @NonNull
    private final TestSymbolRepository<?> testSymbolRepository;

    @Autowired
    public CheckbinanceServiceHelper(SymbolService symbolService, TickerService tickerService) {
        this(symbolService, tickerService, new ApiConverter(),
                TestSymbolRepositoryImpl.builder().withCoreIssuer(symbolService, tickerService).build());
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
    public CheckbinanceServiceHelper createSymbol(TestSymbol testSymbol) {
        testSymbolRepository.createSymbol(testSymbol);
        return this;
    }

    @Override
    public CheckbinanceServiceHelper subscribeSymbol(TestSymbol testSymbol) {
        testSymbolRepository.subscribeSymbol(testSymbol);
        return this;
    }

    @Override
    public CheckbinanceServiceHelper unsubscribeSymbol(TestSymbol testSymbol) {
        testSymbolRepository.unsubscribeSymbol(testSymbol);
        return this;
    }

    public List<LastPriceDTO> convertRecentTradesDTO(List<Symbol> symbols, List<List<RecentTradeDTO>> recentTradeDTOsList) {
        List<LastPriceDTO> list = new ArrayList<>();
        for (int i = symbols.size() - 1; i >= 0; i--) {
            var symbol = symbols.get(i);
            var recentTradeDTOs = recentTradeDTOsList.get(i);
            for (RecentTradeDTO recentTrade : recentTradeDTOs)
                list.add(apiConverter.toApi(symbol.getName(), symbol.getId().getId(), recentTrade));
        }
        return list;
    }

//    public List<LastPriceDTO> convertRecentTradesWithSymbol(List<Symbol> symbols,
//                                                    List<RecentTradesWithSymbol> recentTradesWithSymbols) {
//        Collections.reverse(recentTradesWithSymbols);
//        List<LastPriceDTO> list = new ArrayList<>();
//
//        for (var recentTrades : recentTradesWithSymbols) {
//            Integer symbolId = null;
//
//            for (Symbol symbol : symbols)
//                if (Objects.equals(recentTrades.getSymbol(), symbol.getName()))
//                    symbolId = symbol.getId().getId();
//
//            if (symbolId == null) throw new IllegalArgumentException("Symbols don't match recentTrades");
//
//            for (RecentTradeDTO recentTrade : recentTrades.getRecentTrades())
//                list.add(apiConvertrer.toApi(recentTrades.getSymbol(), symbolId, recentTrade));
//        }
//
//        return list;
//    }

    public List<LastPriceDTO> convertRecentTradesWithSymbol(List<TestSymbol> symbols,
                                                            List<RecentTradesWithSymbol> recentTradesWithSymbols) {
        Collections.reverse(recentTradesWithSymbols);
        List<LastPriceDTO> list = new ArrayList<>();

        for (var recentTrades : recentTradesWithSymbols) {
            Integer symbolId = null;

            for (var symbol : symbols)
                if (Objects.equals(recentTrades.getSymbol(), symbol.getName()))
                    symbolId = symbol.getId().getId();

            if (symbolId == null) throw new IllegalArgumentException("Symbols don't match recentTrades");

            for (RecentTradeDTO recentTrade : recentTrades.getRecentTrades())
                list.add(apiConverter.toApi(recentTrades.getSymbol(), symbolId, recentTrade));
        }

        return list;
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
    public SymbolStatus getRandomSymbolStatus() {
        int pick = ThreadLocalRandom.current().nextInt(SymbolStatus.values().length);
        return SymbolStatus.values()[pick];
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
    public Symbol subscribeOnSymbol(Symbol symbol) { //todo is it normal to return symbol?
        tickerService.subscribeOnPrice(symbol);
        return symbol;
    }

    @Deprecated
    public List<Symbol> subscribeOnSymbols(List<Symbol> symbols) {
        for (var symbol : symbols)
            subscribeOnSymbol(symbol);
        return symbols;
    }

//    @Deprecated
//    public Symbol createAndSubscribeSymbol(String name) {
//        return subscribeOnSymbol(createSymbol(name));
//    }

    @Deprecated
    public List<Symbol> createAndSubscribeSymbols(List<String> names) {
        return subscribeOnSymbols(createSymbols(names));
    }

    public List<LastPriceDTO> toLastPriceDTO(List<SymbolPriceDTO> symbolPriceDTOList, List<TestSymbol> testSymbols) {
        List<LastPriceDTO> lastPrices = new ArrayList<>();
        for (SymbolPriceDTO symbolPrice : symbolPriceDTOList) {
            LastPriceDTO lastPriceDTO = new LastPriceDTO();
            lastPriceDTO.setSymbol(symbolPrice.getSymbol());
            lastPriceDTO.setId(
                    testSymbols.stream()
                            .filter(testSymbol -> symbolPrice.getSymbol().equals(testSymbol.getName()))
                            .findAny().orElseThrow().getId().getId()
            );
            lastPriceDTO.setPrice(symbolPrice.getPrice());
            lastPriceDTO.setTime(LocalDateTime.now());
            lastPrices.add(lastPriceDTO);
        }
        return lastPrices;
    }
}
