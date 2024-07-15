package kz.insar.checkbinance.helpers;

import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.RecentTradeDTO;
import kz.insar.checkbinance.client.SymbolPriceDTO;
import kz.insar.checkbinance.client.SymbolStatus;
import kz.insar.checkbinance.containers.RecentTradesWithSymbol;
import kz.insar.checkbinance.converters.ApiConverter;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.helpers.symbol.TestSymbol;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class ConverterHelper {

    @NonNull
    private final ApiConverter apiConverter;

    public ConverterHelper() {
        this(new ApiConverter());
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
}
