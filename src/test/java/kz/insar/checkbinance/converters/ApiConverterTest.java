package kz.insar.checkbinance.converters;


import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.RecentTradeDTO;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiConverterTest {


    ApiConvertrer service;

    @BeforeEach
    void setUp() {
        service = new ApiConvertrer();
    }

    @Test
    void testToApiRecentTradeDTO() {
        //given
        String symbol = "BNBBTC";
        Integer id = 10;
        var recentTradeDTO = RecentTradeDTO.builder()
                .id(10L)
                .price(BigDecimal.valueOf(100))
                .qty(BigDecimal.valueOf(44))
                .quoteQty(BigDecimal.valueOf(77))
                .time(1687439551L)
                .isBuyerMaker(true)
                .isBestMatch(false)
                .build();
        //when
        var actual = service.toApi(symbol, id, recentTradeDTO);

        //then
        var expected = LastPriceDTO.builder()
                .price(100)
                .symbol("BNBBTC")
                .id(10)
                .time(LocalDateTime.ofInstant(Instant.ofEpochMilli(1687439551L), TimeZone.getDefault().toZoneId()))
                .build();

        assertEquals(expected, actual);
    }

    @Test
    void testToApiListOfLastPriceDTO() {
        //given
        List<LastPriceDTO> lastPrices = new ArrayList<>();

        lastPrices.add(LastPriceDTO.builder()
                .price(100)
                .symbol("BNBBTC")
                .id(1)
                .time(LocalDateTime.ofInstant(Instant.ofEpochMilli(1687439550L), TimeZone.getDefault().toZoneId()))
                .build());
        lastPrices.add(LastPriceDTO.builder()
                .price(110)
                .symbol("BNBETH")
                .id(2)
                .time(LocalDateTime.ofInstant(Instant.ofEpochMilli(1687439551L), TimeZone.getDefault().toZoneId()))
                .build());
        lastPrices.add(LastPriceDTO.builder()
                .price(120)
                .symbol("ETHBTC")
                .id(3)
                .time(LocalDateTime.ofInstant(Instant.ofEpochMilli(1687439552L), TimeZone.getDefault().toZoneId()))
                .build());

        List<Symbol> subscriptions = new ArrayList<>();

        subscriptions.add(Symbol.builder()
                .id(SymbolId.of(12))
                .name()
                .status()
                .baseAsset()
                .baseAssetPrecision()
                .quoteAsset()
                .quotePrecision()
                .quoteAssetPrecision()
                .build());
        //when

        //then
    }

}
