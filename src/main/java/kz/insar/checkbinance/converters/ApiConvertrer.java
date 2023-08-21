package kz.insar.checkbinance.converters;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.api.SymbolParamsDTO;
import kz.insar.checkbinance.api.SymbolShortDTO;
import kz.insar.checkbinance.client.ExchangeInfoResponseDTO;
import kz.insar.checkbinance.client.RecentTradeDTO;
import kz.insar.checkbinance.client.SymbolDTO;
import kz.insar.checkbinance.client.SymbolPriceDTO;
import kz.insar.checkbinance.common.CurrentTimeSupplier;
import kz.insar.checkbinance.common.EpochMilisToTimeConverter;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolUpdate;
import kz.insar.checkbinance.domain.SymbolCreate;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ApiConvertrer {

    private final Supplier<LocalDateTime> currentTime;

    private final LongFunction<LocalDateTime> epochMiliConverter;

    public ApiConvertrer(Supplier<LocalDateTime> currentTime, LongFunction<LocalDateTime> epochMiliConverter) {
       if (currentTime == null) {
           throw new NullPointerException();
       }
       this.currentTime = currentTime;
       this.epochMiliConverter = epochMiliConverter;
    }

    public ApiConvertrer(Supplier<LocalDateTime> currentTime) {
        this(currentTime, new EpochMilisToTimeConverter());
    }

    public ApiConvertrer(LongFunction<LocalDateTime> epochMiliConverter) {
        this(new CurrentTimeSupplier(),epochMiliConverter);
    }

   @Autowired
    public ApiConvertrer() {
        this(new CurrentTimeSupplier(), new EpochMilisToTimeConverter());
    }

    public LastPriceDTO toApi(String symbol, Integer id, RecentTradeDTO recentTrade) {
        LastPriceDTO lastPriceDTO = new LastPriceDTO();
        lastPriceDTO.setSymbol(symbol);
        lastPriceDTO.setId(id);
        lastPriceDTO.setPrice(recentTrade.getPrice());
        lastPriceDTO.setTime(epochMiliConverter.apply(recentTrade.getTime()));
        return lastPriceDTO;
    }


    public List<LastPriceDTO> toApi(List<SymbolPriceDTO> symbolPrices, List<Symbol> subscriptions) {
        List<LastPriceDTO> lastPrices = new ArrayList<>();
        for (SymbolPriceDTO symbolPrice : symbolPrices) {
            LastPriceDTO lastPriceDTO = new LastPriceDTO();
            lastPriceDTO.setSymbol(symbolPrice.getSymbol());
            lastPriceDTO.setId(
                    subscriptions.stream()
                            .filter(subscription -> symbolPrice.getSymbol().equals(subscription.getName()))
                            .findAny().orElseThrow().getId().getId()
            );
            lastPriceDTO.setPrice(symbolPrice.getPrice());
            lastPriceDTO.setTime(currentTime.get());
            lastPrices.add(lastPriceDTO);
        }
        return lastPrices;
    }

    public SymbolParamsDTO toApi(SymbolDTO symbol) {
        SymbolParamsDTO symbolParamsDTO = new SymbolParamsDTO();
        symbolParamsDTO.setSymbol(symbol.getSymbol());
        symbolParamsDTO.setStatus(symbol.getStatus());
        symbolParamsDTO.setBaseAsset(symbol.getBaseAsset());
        symbolParamsDTO.setBaseAssetPrecision(symbol.getBaseAssetPrecision());
        symbolParamsDTO.setQuoteAsset(symbol.getQuoteAsset());
        symbolParamsDTO.setQuotePrecision(symbol.getQuotePrecision());
        symbolParamsDTO.setQuoteAssetPrecision(symbol.getQuoteAssetPrecision());
        return symbolParamsDTO;
    }

    public ExchangeInfoBySymbolsDTO toApi(ExchangeInfoResponseDTO exchangeInfoResponse) {
        ExchangeInfoBySymbolsDTO exchangeInfoBySymbolsDTO = new ExchangeInfoBySymbolsDTO();
        exchangeInfoBySymbolsDTO.setServerTime(exchangeInfoResponse.getServerTime());
        List<SymbolDTO> symbolDTOList = exchangeInfoResponse.getSymbols();
        List<SymbolParamsDTO> symbolParamsDTOList = new ArrayList<>();
        for (Integer i = 0; i < symbolDTOList.size(); i++) {
            symbolParamsDTOList.add(toApi(symbolDTOList.get(i)));
        }
        exchangeInfoBySymbolsDTO.setSymbols(symbolParamsDTOList);
        return exchangeInfoBySymbolsDTO;
    }

    public SymbolUpdate toDomainUpdate(Symbol symbol, SymbolDTO updateParams) {
        return SymbolUpdate.builder()
                .id(symbol.getId())
                .status(updateParams.getStatus())
                .baseAsset(updateParams.getBaseAsset())
                .baseAssetPrecision(updateParams.getBaseAssetPrecision())
                .quoteAsset(updateParams.getQuoteAsset())
                .quotePrecision(updateParams.getQuotePrecision())
                .quoteAssetPrecision(updateParams.getQuoteAssetPrecision())
                .build();
    }

    public SymbolCreate toDomainCreate(SymbolDTO createParams) {
        return SymbolCreate.builder()
                .name(createParams.getSymbol())
                .status(createParams.getStatus())
                .baseAsset(createParams.getBaseAsset())
                .baseAssetPrecision(createParams.getBaseAssetPrecision())
                .quoteAsset(createParams.getQuoteAsset())
                .quotePrecision(createParams.getQuotePrecision())
                .quoteAssetPrecision(createParams.getQuoteAssetPrecision())
                .build();

    }

    public SymbolShortDTO fromDomainToShort(Symbol symbol) {
        return SymbolShortDTO.builder()
                .id(symbol.getId().getId())
                .name(symbol.getName())
                .build();
    }

    public List<SymbolShortDTO> fromDomainToShortList(List<Symbol> symbols) {
        return symbols.stream().map(this::fromDomainToShort).collect(Collectors.toList());
    }

    public List<String> toDomainRequest(List<Symbol> symbols) {
        List<String> stringSymbols = new ArrayList<>();
        for (Symbol symbol : symbols) {
            stringSymbols.add(symbol.getName());
        }
        return stringSymbols;
    }
}