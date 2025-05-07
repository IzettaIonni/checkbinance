package kz.insar.checkbinance.converters;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.api.SymbolParamsDTO;
import kz.insar.checkbinance.api.SymbolShortDTO;
import kz.insar.checkbinance.client.*;
import kz.insar.checkbinance.common.CurrentTimeSupplier;
import kz.insar.checkbinance.common.EpochMilisToTimeConverter;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolUpdate;
import kz.insar.checkbinance.domain.SymbolCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class ApiConverter {
    //todo разобраться со слоями (клиент бинаса торчит в наш апи конвертер, символДТО из клиента, символ статус из клиента)

    private final Supplier<LocalDateTime> currentTime;

    private final LongFunction<LocalDateTime> epochMiliConverter;

    public ApiConverter(Supplier<LocalDateTime> currentTime, LongFunction<LocalDateTime> epochMiliConverter) {
       if (currentTime == null) {
           throw new NullPointerException();
       }
       this.currentTime = currentTime;
       this.epochMiliConverter = epochMiliConverter;
    }

    @Autowired
    public ApiConverter(Supplier<LocalDateTime> currentTime) {
        this(currentTime, new EpochMilisToTimeConverter());
    }

    public ApiConverter(LongFunction<LocalDateTime> epochMiliConverter) {
        this(new CurrentTimeSupplier(),epochMiliConverter);
    }

    public ApiConverter() {
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
        symbolParamsDTO.setStatus(toSymbolStatus(symbol.getStatus()));
        symbolParamsDTO.setBaseAsset(symbol.getBaseAsset());
        symbolParamsDTO.setBaseAssetPrecision(symbol.getBaseAssetPrecision());
        symbolParamsDTO.setQuoteAsset(symbol.getQuoteAsset());
        symbolParamsDTO.setQuotePrecision(symbol.getQuotePrecision());
        symbolParamsDTO.setQuoteAssetPrecision(symbol.getQuoteAssetPrecision());
        return symbolParamsDTO;
    }

    private SymbolStatus toSymbolStatus(String status) {
        if (status == null) return SymbolStatus.UNKNOWN;
        switch (status) {
            case "PRE_TRADING":
                return SymbolStatus.PRE_TRADING;
            case "TRADING":
                return SymbolStatus.TRADING;
            case "POST_TRADING":
                return SymbolStatus.POST_TRADING;
            case "END_OF_DAY":
                return SymbolStatus.END_OF_DAY;
            case "HALT":
                return SymbolStatus.HALT;
            case "AUCTION_MATCH":
                return SymbolStatus.AUCTION_MATCH;
            case "BREAK":
                return SymbolStatus.BREAK;
            default: return SymbolStatus.UNKNOWN;
        }
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
                .status(toSymbolStatus(updateParams.getStatus()))
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
                .status(toSymbolStatus(createParams.getStatus()))
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