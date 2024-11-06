package kz.insar.checkbinance.tradeprice.impl;

import kz.insar.checkbinance.tradeprice.StreamNode;
import kz.insar.checkbinance.tradeprice.StreamNodeFactory;
import org.easymock.IMocksControl;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.Random.class)
public class WebSocketManagerImplTest {


    private static final String ITEM_NAME1 = "foo";
    private static final String ITEM_NAME2 = "bar";
    private static final String ITEM_NAME3 = "someString";
    private IMocksControl control;
    private StreamNodeFactory factoryMock;
    private List<StreamNode> streamsStub;
    private Set<String> itemsStub;
    private AtomicInteger currentStreamIndexStub;
    private AtomicInteger itemsPerStreamStub;
    private StreamNode nodeMock1, nodeMock2, nodeMock3;
    private WebSocketManagerImpl service;

    @BeforeEach
    void setUp() {
        control = createStrictControl();
        factoryMock = control.createMock(StreamNodeFactory.class);
        streamsStub = new ArrayList<>();
        itemsStub = new HashSet<>();
        currentStreamIndexStub = new AtomicInteger(0);
        itemsPerStreamStub = new AtomicInteger(1);
        service = new WebSocketManagerImpl(factoryMock, 3, streamsStub, itemsStub, currentStreamIndexStub, itemsPerStreamStub);
        nodeMock1 = control.createMock(StreamNode.class);
        nodeMock2 = control.createMock(StreamNode.class);
        nodeMock3 = control.createMock(StreamNode.class);
    }

    @AfterEach
    void tearDown() {
        streamsStub.clear();
        itemsStub.clear();
        currentStreamIndexStub.set(0);
        itemsPerStreamStub.set(1);
    }

    @Test
    void testSubscribeItem_shouldNotAddSameItemTwice() {
        //given
        itemsStub.add(ITEM_NAME1);

        //when
        service.subscribeItem(ITEM_NAME1);

        //then
        assertEquals(new ArrayList<StreamNode>(), streamsStub);
        assertEquals(Set.of(ITEM_NAME1), itemsStub);
    }

    @Test
    void testSubscribeItem_shouldAddItemIfItIsFirstIteration() {
        expect(factoryMock.createStreamNodeAndAddItem(ITEM_NAME1)).andReturn(nodeMock1);
        control.replay();

        service.subscribeItem(ITEM_NAME1);

        control.verify();
        assertEquals(List.of(nodeMock1), streamsStub);
        assertEquals(Set.of(ITEM_NAME1), itemsStub);
    }

    @Test
    void testSubscribeItem_shouldUseHoleInExistingStreamIfExist() {
        streamsStub.add(nodeMock1);
        streamsStub.add(nodeMock2);
        streamsStub.add(nodeMock3);

        currentStreamIndexStub.set(2);
        itemsPerStreamStub.set(5);
        expect(nodeMock1.getItemsCount()).andReturn(5);
        expect(nodeMock2.getItemsCount()).andReturn(3);
        nodeMock2.addItem(ITEM_NAME1);
        control.replay();

        service.subscribeItem(ITEM_NAME1);

        control.verify();
        assertEquals(List.of(nodeMock1, nodeMock2, nodeMock3), streamsStub);
        assertEquals(2, currentStreamIndexStub.get());
        assertEquals(5, itemsPerStreamStub.get());
        assertEquals(Set.of(ITEM_NAME1), itemsStub);
    }

    @Test
    void testSubscribeItem_shouldUseNextStreamIfNoHolesExistAndGoToNextStream() {
        streamsStub.add(nodeMock1);
        streamsStub.add(nodeMock2);
        streamsStub.add(nodeMock3);
        currentStreamIndexStub.set(1);
        itemsPerStreamStub.set(5);
        expect(nodeMock1.getItemsCount()).andReturn(5);
        nodeMock2.addItem(ITEM_NAME1);

        control.replay();

        service.subscribeItem(ITEM_NAME1);

        control.verify();
        assertEquals(List.of(nodeMock1, nodeMock2, nodeMock3), streamsStub);
        assertEquals(2, currentStreamIndexStub.get());
        assertEquals(5, itemsPerStreamStub.get());
        assertEquals(Set.of(ITEM_NAME1), itemsStub);
    }

    @Test
    void testSubscribeItem_shouldUseNextStreamIfNoHolesExistAndGoToNextPass() {
        streamsStub.add(nodeMock1);
        streamsStub.add(nodeMock2);
        streamsStub.add(nodeMock3);
        currentStreamIndexStub.set(2);
        itemsPerStreamStub.set(5);
        expect(nodeMock1.getItemsCount()).andReturn(5);
        expect(nodeMock2.getItemsCount()).andReturn(5);
        nodeMock3.addItem(ITEM_NAME1);

        control.replay();

        service.subscribeItem(ITEM_NAME1);

        control.verify();
        assertEquals(List.of(nodeMock1, nodeMock2, nodeMock3), streamsStub);
        assertEquals(0, currentStreamIndexStub.get());
        assertEquals(6, itemsPerStreamStub.get());
        assertEquals(Set.of(ITEM_NAME1), itemsStub);
    }

    @Test
    void testUnsubscribeItem_shouldDoNothingIfNoSuchItemExists() {
        control.replay();

        service.unsubscribeItem(ITEM_NAME1);

        control.verify();
        assertEquals(new ArrayList<>(), streamsStub);
        assertEquals(new HashSet<>(), itemsStub);
    }

    @Test
    void testUnsubscribeItem_shouldRemoveItemIfItemExists() {
        streamsStub.add(nodeMock1);
        streamsStub.add(nodeMock2);
        streamsStub.add(nodeMock3);
        itemsStub.add(ITEM_NAME3);
        expect(nodeMock1.getItems()).andReturn(List.of(ITEM_NAME1));
        expect(nodeMock2.getItems()).andReturn(List.of(ITEM_NAME2));
        expect(nodeMock3.getItems()).andReturn(List.of(ITEM_NAME3));
        nodeMock3.removeItem(ITEM_NAME3);
        control.replay();

        service.unsubscribeItem(ITEM_NAME3);

        control.verify();
        assertEquals(List.of(nodeMock1, nodeMock2, nodeMock3), streamsStub);
        assertEquals(new HashSet<>(), itemsStub);
    }

    @Test
    void testUnsubscribeItem_shouldThrowExceptionIfItemMarkedAsExistingButNotUsedInStreams() {
        streamsStub.add(nodeMock1);
        itemsStub.add(ITEM_NAME3);
        expect(nodeMock1.getItems()).andReturn(List.of(ITEM_NAME1));
        control.replay();

        assertThrows(IllegalStateException.class, () -> service.unsubscribeItem(ITEM_NAME3));

        control.verify();
        assertEquals(List.of(nodeMock1), streamsStub);
        assertEquals(Set.of(ITEM_NAME3), itemsStub);
    }

}
