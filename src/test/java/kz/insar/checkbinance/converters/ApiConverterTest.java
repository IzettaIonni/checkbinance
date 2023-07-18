package kz.insar.checkbinance.converters;


import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.RecentTradeDTO;
import kz.insar.checkbinance.client.SymbolPriceDTO;
import kz.insar.checkbinance.client.SymbolStatus;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolId;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Supplier;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiConverterTest {


    private Supplier<LocalDateTime> currentTimeMock;

    private ApiConvertrer service;

    private IMocksControl control;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        currentTimeMock = control.createMock(Supplier.class);
        service = new ApiConvertrer(currentTimeMock);
    }

    static LocalDateTime T(String time) {
        return LocalDateTime.parse(time);
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
                .time(T("1970-01-20T12:43:59.551"))
                .build();

        assertEquals(expected, actual);
    }

    @Test
    void testToApiListOfLastPriceDTO() {
        //given
        List<Symbol> subscriptions = new ArrayList<>();

        subscriptions.add(Symbol.builder()
                .id(SymbolId.of(12))
                .name("BNBBTC")
                .status(SymbolStatus.POST_TRADING)
                .baseAsset("Lorem")
                .baseAssetPrecision(123)
                .quoteAsset("Lorem")
                .quotePrecision(123)
                .quoteAssetPrecision(123)
                .build());
        subscriptions.add(Symbol.builder()
                .id(SymbolId.of(35))
                .name("BNBETH")
                .status(SymbolStatus.POST_TRADING)
                .baseAsset("Lorem")
                .baseAssetPrecision(123)
                .quoteAsset("Lorem")
                .quotePrecision(123)
                .quoteAssetPrecision(123)
                .build());
        subscriptions.add(Symbol.builder()
                .id(SymbolId.of(57))
                .name("ETHBTC")
                .status(SymbolStatus.POST_TRADING)
                .baseAsset("Lorem")
                .baseAssetPrecision(123)
                .quoteAsset("Lorem")
                .quotePrecision(123)
                .quoteAssetPrecision(123)
                .build());

        List<SymbolPriceDTO> symbolPrices = new ArrayList<>();

        symbolPrices.add(SymbolPriceDTO.builder()
                .symbol("BNBBTC")
                .price(BigDecimal.valueOf(100))
                .build());
        symbolPrices.add(SymbolPriceDTO.builder()
                .symbol("BNBETH")
                .price(BigDecimal.valueOf(110))
                .build());
        symbolPrices.add(SymbolPriceDTO.builder()
                .symbol("ETHBTC")
                .price(BigDecimal.valueOf(120))
                .build());

        expect(currentTimeMock.get())
                .andReturn(T("2012-10-30T20:30:20"))
                .andReturn(T("2012-10-30T20:30:30"))
                .andReturn(T("2012-10-30T20:30:40"));
        control.replay();
        //when

        var actual = service.toApi(symbolPrices, subscriptions);

        //then
        control.verify();
        List<LastPriceDTO> expected = new ArrayList<>();

        expected.add(LastPriceDTO.builder()
                .price(100)
                .symbol("BNBBTC")
                .id(12)
                .time(T("2012-10-30T20:30:20"))
                .build());
        expected.add(LastPriceDTO.builder()
                .price(110)
                .symbol("BNBETH")
                .id(35)
                .time(T("2012-10-30T20:30:30"))
                .build());
        expected.add(LastPriceDTO.builder()
                .price(120)
                .symbol("ETHBTC")
                .id(57)
                .time(T("2012-10-30T20:30:40"))
                .build());

        assertEquals(expected, actual);
    }


}
