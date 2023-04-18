package kz.insar.checkbinance.services;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;

import java.util.List;

public interface TickerService {
    List<LastPriceDTO> lastPrices(List<String> symbols);

    List<LastPriceDTO> lastPrices(List<String> symbols, int limit);

    ExchangeInfoBySymbolsDTO exchangeInfo(List<String> symbols);

    ExchangeInfoBySymbolsDTO exchangeInfo();
}
