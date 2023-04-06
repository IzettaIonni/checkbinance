package kz.insar.checkbinance.services;

import kz.insar.checkbinance.api.ExchangeInfoBySymbolsDTO;
import kz.insar.checkbinance.api.LastPriceDTO;

import java.util.List;

public interface TickerService {
    List<LastPriceDTO> lastPrices(List<String> symbols);

    ExchangeInfoBySymbolsDTO exchangeInfo(List<String> symbols);

    ExchangeInfoBySymbolsDTO exchangeInfo();
}
