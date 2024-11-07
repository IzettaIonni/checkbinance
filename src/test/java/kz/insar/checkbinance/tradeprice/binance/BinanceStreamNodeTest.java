package kz.insar.checkbinance.tradeprice.binance;

import com.binance.connector.client.WebSocketStreamClient;
import com.binance.connector.client.utils.websocketcallback.WebSocketMessageCallback;
import kz.insar.checkbinance.tradeprice.ITradePriceListener;
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
    private static final String SYMBOL1 = "someSymbol1";
    private static final String SYMBOL2 = "va-11 hall-a";
    private static final String SYMBOL3 = "meow";
    private IMocksControl control;
    private WebSocketStreamClient streamClientMock;
    private ITradePriceListener listenerMock;
    private BinanceWebSocketConverter converterMock;
    private Set<String> symbolsStab;
    private BinanceStreamNode service;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        streamClientMock = control.createMock(WebSocketStreamClient.class);
        listenerMock = control.createMock(ITradePriceListener.class);
        converterMock = control.createMock(BinanceWebSocketConverter.class);
        symbolsStab = new HashSet<>();
        service = new BinanceStreamNode(streamClientMock, listenerMock, converterMock, 0, symbolsStab);
    }

    @Test
    void testAddItem_shouldDoNothingIfSymbolIsPresent() {
        symbolsStab.add(SYMBOL1);
        control.replay();

        service.addItem(SYMBOL1);

        control.verify();
        assertEquals(Set.of(SYMBOL1), symbolsStab);
    }

    @Test
    void testAddItem_shouldAddSymbol() {
        symbolsStab.add(SYMBOL1);
        streamClientMock.closeConnection(0);
        var processedItemList = new ArrayList<String>(List.of(SYMBOL1+"@trade", SYMBOL2+"@trade"));
        expect(streamClientMock.combineStreams(eq(processedItemList), anyObject(WebSocketMessageCallback.class))).andReturn(1238);
        control.replay();

        service.addItem(SYMBOL2);

        control.verify();
        assertEquals(Set.of(SYMBOL1, SYMBOL2), symbolsStab);
        assertEquals(1238, service.getStreamId());
    }

    @Test
    void testRemoveItem_shouldDoNothingIfSymbolIsNotPresent() {
        control.replay();

        service.removeItem(SYMBOL1);

        control.verify();
        assertEquals(new HashSet<>(), symbolsStab);
    }

    @Test
    void testRemoveItem_shouldRemoveSymbol() {
        symbolsStab.add(SYMBOL1);
        symbolsStab.add(SYMBOL2);
        symbolsStab.add(SYMBOL3);
        streamClientMock.closeConnection(0);
        var processedItemList = new ArrayList<String>(List.of(SYMBOL3+"@trade", SYMBOL1+"@trade"));
        expect(streamClientMock.combineStreams(eq(processedItemList), anyObject(WebSocketMessageCallback.class))).andReturn(1238);
        control.replay();

        service.removeItem(SYMBOL2);

        control.verify();
        assertEquals(Set.of(SYMBOL1, SYMBOL3), symbolsStab);
        assertEquals(1238, service.getStreamId());
    }

}
