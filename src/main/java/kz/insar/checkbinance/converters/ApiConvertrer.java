package kz.insar.checkbinance.converters;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.api.SymbolParamsDTO;
import kz.insar.checkbinance.client.ExchangeInfoResponseDTO;
import kz.insar.checkbinance.client.RecentTradeDTO;
import kz.insar.checkbinance.client.SymbolDTO;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Service
public class ApiConvertrer {

    public LastPriceDTO toApi(String symbol, RecentTradeDTO recentTrade) {
        LastPriceDTO lastPriceDTO = new LastPriceDTO();
        lastPriceDTO.setSymbol(symbol);
        lastPriceDTO.setId(recentTrade.getId());
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
}
