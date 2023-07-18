package kz.insar.checkbinance.services.impl;

import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.BinanceClient;
import kz.insar.checkbinance.client.SymbolPriceDTO;
import kz.insar.checkbinance.converters.ApiConvertrer;
import kz.insar.checkbinance.domain.Symbol;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.domain.sort.comparators.LastPriceDTOComparator;
import kz.insar.checkbinance.domain.sort.params.LastPriceColumns;
import kz.insar.checkbinance.domain.sort.params.SortDirection;
import kz.insar.checkbinance.domain.sort.params.SortParams;
import kz.insar.checkbinance.repositories.SymbolRepository;
import kz.insar.checkbinance.services.SymbolService;
import org.easymock.IMocksControl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TickerServiceImplTest {

    private IMocksControl control;
    private BinanceClient binanceClientMock;
    private ApiConvertrer apiConvertrerMock;
    private SymbolService symbolServiceMock;
    private SymbolRepository symbolRepositoryMock;
    private TickerServiceImpl service;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        binanceClientMock = control.createMock(BinanceClient.class);
        apiConvertrerMock = control.createMock(ApiConvertrer.class);
        symbolServiceMock = control.createMock(SymbolService.class);
        symbolRepositoryMock = control.createMock(SymbolRepository.class);
        service = new TickerServiceImpl(binanceClientMock, apiConvertrerMock,
                symbolServiceMock, symbolRepositoryMock);
    }

    @Test
    void testListSymbols() {
        Symbol symbolMock1 = control.createMock(Symbol.class);
        Symbol symbolMock2 = control.createMock(Symbol.class);
        expect(symbolServiceMock.getSymbols()).andReturn(List.of(symbolMock1, symbolMock2));
        control.replay();

        var actual = service.listSymbols();

        control.verify();
        var expected = List.of(symbolMock1, symbolMock2);
        assertEquals(expected, actual);
    }

    @Test
    void testSubscribeOnPrice() {
        //given
        SymbolId idMock = control.createMock(SymbolId.class);
        symbolServiceMock.addPriceSubscription(eq(idMock));
        control.replay();

        //when
        service.subscribeOnPrice(idMock);

        //then
        control.verify();
    }

    @Test
    void testExchangeInfo() {
        //given


        //when

        //then

    }


    @Test
    void testLastPrices_noSubscriptions() {
        //given
        SortParams<LastPriceColumns> sortParamsMock = control.createMock(SortParams.class);
        expect(symbolServiceMock.getListOfPriceSubscriptions()).andReturn(List.of());
        control.replay();

        //when
        var actual = service.lastPrices(sortParamsMock);

        //then
        control.verify();
        var expected = List.of();
        assertEquals(expected, actual);
    }

    //    public List<LastPriceDTO> lastPrices(SortParams<LastPriceColumns> sortParams) {
//        List<Symbol> subscriptions = listSubscribtionOnPrices();
//        List<LastPriceDTO> prices = new ArrayList<>();
//        if (subscriptions == null || subscriptions.size() == 0) return prices;
//        prices = apiConvertrer.toApi(binanceClient.getPrices(apiConvertrer.toDomainRequest(subscriptions)), subscriptions);
//        LastPriceDTOComparator sort = new LastPriceDTOComparator(sortParams.getDir(), sortParams.getColumn());
//        prices.sort(sort);
//        return prices;
//    }

    @Test
    void testLastPrices_withSubscriptions() {
        //given
        SortParams<LastPriceColumns> sortParams = new SortParams<>(LastPriceColumns.ID, SortDirection.ASC);
        Symbol symbolMock1 = control.createMock(Symbol.class);
        Symbol symbolMock2 = control.createMock(Symbol.class);
        SymbolPriceDTO symbolPriceDTOMock1 = control.createMock(SymbolPriceDTO.class);
        SymbolPriceDTO symbolPriceDTOMock2 = control.createMock(SymbolPriceDTO.class);
        var subscriptions = List.of(symbolMock1, symbolMock2);
        List<LastPriceDTO> lastPriceDTOListMock = control.createMock(List.class);
        expect(symbolServiceMock.getListOfPriceSubscriptions()).andReturn(subscriptions);
        expect(apiConvertrerMock.toDomainRequest(eq(subscriptions))).andReturn(List.of("Foo", "Bar"));
        expect(binanceClientMock.getPrices(eq(List.of("Foo", "Bar"))))
                .andReturn(List.of(symbolPriceDTOMock1, symbolPriceDTOMock2));
        expect(apiConvertrerMock.toApi(eq(List.of(symbolPriceDTOMock1, symbolPriceDTOMock2)), same(subscriptions)))
                .andReturn(lastPriceDTOListMock);
        lastPriceDTOListMock.sort(eq(new LastPriceDTOComparator(SortDirection.ASC, LastPriceColumns.ID)));
        control.replay();

        //when
        var actual = service.lastPrices(sortParams);

        //then
        control.verify();
        assertEquals(lastPriceDTOListMock, actual);
    }
}
