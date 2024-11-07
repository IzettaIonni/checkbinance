package kz.insar.checkbinance.tradeprice.binance;

import com.binance.connector.client.WebSocketStreamClient;
import com.binance.connector.client.utils.websocketcallback.WebSocketMessageCallback;
import groovyjarjarantlr4.v4.runtime.misc.Array2DHashSet;
import kz.insar.checkbinance.tradeprice.ITradePrice;
import kz.insar.checkbinance.tradeprice.ITradePriceListener;
import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.Random.class)
public class BinanceStreamNodeTest {
    private static final String SYMBOL1 = RandomStringUtils.randomAlphabetic(32);
    private static final String SYMBOL2 = RandomStringUtils.randomAlphabetic(32);
    private static final String SYMBOL3 = RandomStringUtils.randomAlphabetic(32);
    private IMocksControl control;
    private WebSocketStreamClient streamClientMock;
    private ITradePriceListener listenerMock;
    private BinanceWebSocketConverter converterMock;
    private List<String> symbolsStab;
    private BinanceStreamNode service;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        streamClientMock = control.createMock(WebSocketStreamClient.class);
        listenerMock = control.createMock(ITradePriceListener.class);
        converterMock = control.createMock(BinanceWebSocketConverter.class);
        symbolsStab = new ArrayList<>();
        service = new BinanceStreamNode(streamClientMock, listenerMock, converterMock, 0, symbolsStab);
    }

    @Test
    void testAddItem_shouldDoNothingIfSymbolIsPresent() {
        symbolsStab.add(SYMBOL1);
        control.replay();

        service.addItem(SYMBOL1);

        control.verify();
        assertEquals(List.of(SYMBOL1), symbolsStab);
    }

    @Test
    void testAddItem_shouldAddSymbol() {
        symbolsStab.add(SYMBOL1);
        streamClientMock.closeConnection(0);
        Capture<WebSocketMessageCallback> callbackCapture = newCapture();
        var processedItemList = new ArrayList<String>(List.of(SYMBOL1+"@trade", SYMBOL2+"@trade"));
        expect(streamClientMock.combineStreams(eq(processedItemList), capture(callbackCapture))).andReturn(1238);
        ITradePrice iTradePriceMock = control.createMock(ITradePrice.class);
        expect(converterMock.toITradePrice("event")).andReturn(iTradePriceMock);
        listenerMock.processTrade(iTradePriceMock);
        control.replay();

        service.addItem(SYMBOL2);
        callbackCapture.getValues().get(0).onMessage("event");

        control.verify();
        assertEquals(List.of(SYMBOL1, SYMBOL2), symbolsStab);
        assertEquals(1238, service.getStreamId());
    }

    @Test
    void testRemoveItem_shouldDoNothingIfSymbolIsNotPresent() {
        control.replay();

        service.removeItem(SYMBOL1);

        control.verify();
        assertEquals(new ArrayList<>(), symbolsStab);
    }

    @Test
    void testRemoveItem_shouldRemoveSymbol() {
        symbolsStab.add(SYMBOL1);
        symbolsStab.add(SYMBOL2);
        symbolsStab.add(SYMBOL3);
        streamClientMock.closeConnection(0);
        Capture<WebSocketMessageCallback> callbackCapture = newCapture();
        var processedItemList = new ArrayList<String>(List.of(SYMBOL1+"@trade", SYMBOL3+"@trade"));
        expect(streamClientMock.combineStreams(eq(processedItemList), capture(callbackCapture))).andReturn(1238);
        ITradePrice iTradePriceMock = control.createMock(ITradePrice.class);
        expect(converterMock.toITradePrice("event")).andReturn(iTradePriceMock);
        listenerMock.processTrade(iTradePriceMock);
        control.replay();

        service.removeItem(SYMBOL2);
        callbackCapture.getValues().get(0).onMessage("event");

        control.verify();
        assertEquals(List.of(SYMBOL1, SYMBOL3), symbolsStab);
        assertEquals(1238, service.getStreamId());
    }

}
