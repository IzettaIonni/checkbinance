package kz.insar.checkbinance.services.qus.tradeprice.binance;

import com.binance.connector.client.WebSocketStreamClient;
import com.binance.connector.client.utils.websocketcallback.WebSocketMessageCallback;
import kz.insar.checkbinance.services.qus.tradeprice.binance.BinanceStreamNode;
import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(MethodOrderer.Random.class)
public class BinanceStreamNodeTest {
    private static final String SYMBOL1 = RandomStringUtils.randomAlphabetic(32);
    private static final String SYMBOL2 = RandomStringUtils.randomAlphabetic(32);
    private static final String SYMBOL3 = RandomStringUtils.randomAlphabetic(32);
    private IMocksControl control;
    private WebSocketStreamClient streamClientMock;
    private WebSocketMessageCallback callbackMock;
    private List<String> symbolsStub;
    private BinanceStreamNode service;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        streamClientMock = control.createMock(WebSocketStreamClient.class);
        callbackMock = control.createMock(WebSocketMessageCallback.class);
        symbolsStub = new ArrayList<>();
        service = new BinanceStreamNode(streamClientMock, callbackMock, symbolsStub, null);
    }

    @Test
    void testAddItem_shouldDoNothingIfSymbolIsPresent() {
        symbolsStub.add(SYMBOL1);
        control.replay();

        service.addItem(SYMBOL1);

        control.verify();
        assertEquals(List.of(SYMBOL1), symbolsStub);
    }

    @Test
    void testAddItem_shouldAddSymbol() {
        service.setStreamId(10);
        symbolsStub.add(SYMBOL1);
        var processedItemList = new ArrayList<String>(List.of(SYMBOL1+"@trade", SYMBOL2+"@trade"));
        streamClientMock.closeConnection(10);
        expect(streamClientMock.combineStreams(processedItemList, callbackMock)).andReturn(1238);
        control.replay();

        service.addItem(SYMBOL2);

        control.verify();
        assertEquals(List.of(SYMBOL1, SYMBOL2), symbolsStub);
        assertEquals(1238, service.getStreamId());
    }

    @Test
    void testAddItem_shouldNotCloseStreamIfFirstItemAdded() {
        var processedItemList = new ArrayList<String>(List.of(SYMBOL1+"@trade"));
        expect(streamClientMock.combineStreams(processedItemList, callbackMock)).andReturn(1238);
        control.replay();

        service.addItem(SYMBOL1);
        control.verify();
        assertEquals(List.of(SYMBOL1), symbolsStub);
        assertEquals(1238, service.getStreamId());
    }

    @Test
    void testRemoveItem_shouldDoNothingIfSymbolIsNotPresent() {
        control.replay();

        service.removeItem(SYMBOL1);

        control.verify();
        assertEquals(new ArrayList<>(), symbolsStub);
    }

    @Test
    void testRemoveItem_shouldRemoveSymbol() {
        service.setStreamId(10);
        symbolsStub.add(SYMBOL1);
        symbolsStub.add(SYMBOL2);
        symbolsStub.add(SYMBOL3);
        var processedItemList = new ArrayList<String>(List.of(SYMBOL1+"@trade", SYMBOL3+"@trade"));
        streamClientMock.closeConnection(10);
        expect(streamClientMock.combineStreams(processedItemList, callbackMock)).andReturn(1238);
        control.replay();

        service.removeItem(SYMBOL2);

        control.verify();
        assertEquals(List.of(SYMBOL1, SYMBOL3), symbolsStub);
        assertEquals(1238, service.getStreamId());
    }

    @Test
    void testRemoveItem_shouldNotCallCombineStreamsWhenDeleteLastItem() {
        service.setStreamId(10);
        symbolsStub.add(SYMBOL2);
        streamClientMock.closeConnection(10);
        control.replay();

        service.removeItem(SYMBOL2);

        control.verify();
    }

}
