package kz.insar.checkbinance.containers;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.helpers.symbol.TestSymbol;
import kz.insar.checkbinance.helpers.symbol.TestSymbolRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder(toBuilder = true)
public class BNBExchangeInfoResponse {

    private final Long serverTime;
    private final List<BNBExchangeInfoSymbol> symbols;

    public ExchangeInfoBySymbolsDTO toExchangeInfoBySymbolsDTO() {
        return ExchangeInfoBySymbolsDTO.builder()
                .serverTime(serverTime)
                .symbols(symbols.stream().map(BNBExchangeInfoSymbol::toSymbolParamsDTO).collect(Collectors.toList()))
                .build();
    }

    public List<String> getRequestSymbols() {
        return symbols.stream().map(BNBExchangeInfoSymbol::getSymbol).collect(Collectors.toList());
    }

    public static class BNBExchangeInfoResponseBuilder {
        private Long serverTime = Instant.now().toEpochMilli();
        private List<BNBExchangeInfoSymbol> symbols = new ArrayList<>();

        public BNBExchangeInfoResponseBuilder addSymbol(@NonNull BNBExchangeInfoSymbol symbol) {
            symbols.add(symbol);
            return this;
        }

        public BNBExchangeInfoResponseBuilder addSymbol(@NonNull TestSymbol testSymbol) {
            return addSymbol(BNBExchangeInfoSymbol.of(testSymbol));
        }

        public BNBExchangeInfoResponseBuilder serverTimeCurrent() {
            return serverTime(Instant.now().toEpochMilli());
        }

    }

}
