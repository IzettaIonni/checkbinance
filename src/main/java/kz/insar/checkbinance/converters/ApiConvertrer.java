package kz.insar.checkbinance.converters;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.api.SymbolParamsDTO;
import kz.insar.checkbinance.api.SymbolShortDTO;
import kz.insar.checkbinance.client.ExchangeInfoResponseDTO;
import kz.insar.checkbinance.client.RecentTradeDTO;
import kz.insar.checkbinance.client.SymbolDTO;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolUpdate;
import kz.insar.checkbinance.domain.SymbolCreate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
public class ApiConvertrer {

    public LastPriceDTO toApi(String symbol, Integer id, RecentTradeDTO recentTrade) {
        LastPriceDTO lastPriceDTO = new LastPriceDTO();
        lastPriceDTO.setSymbol(symbol);
        lastPriceDTO.setId(id);
        lastPriceDTO.setPrice(recentTrade.getPrice());
        lastPriceDTO.setTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(recentTrade.getTime()), TimeZone.getDefault().toZoneId()));
        return lastPriceDTO;
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
}