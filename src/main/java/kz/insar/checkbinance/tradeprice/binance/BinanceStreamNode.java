package kz.insar.checkbinance.tradeprice.binance;

import com.binance.connector.client.WebSocketStreamClient;
import kz.insar.checkbinance.tradeprice.ITradePriceListener;
import kz.insar.checkbinance.tradeprice.StreamNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.stream.Collectors;


@AllArgsConstructor
class BinanceStreamNode implements StreamNode, AutoCloseable{
    @NonNull
    private final WebSocketStreamClient wsStreamClient;
    @NonNull
    private final ITradePriceListener listener;
    @NonNull
    private final BinanceWebSocketConverter converter;
    @Getter
    private Integer streamId;
    private final List<String> symbols;

    public BinanceStreamNode(@NonNull WebSocketStreamClient wsStreamClient, @NonNull ITradePriceListener listener,
                             @NonNull BinanceWebSocketConverter converter, @NonNull Collection<String> symbols) {
        this(wsStreamClient, listener, converter, null, new ArrayList<>(symbols));
    }

    public BinanceStreamNode(@NonNull WebSocketStreamClient wsStreamClient, @NonNull ITradePriceListener listener,
                             @NonNull BinanceWebSocketConverter converter, @NonNull String... symbol) {
        this(wsStreamClient, listener, converter, Set.of(symbol));
    }

    public void addItem(@NonNull String symbol) {
        if (symbols.contains(symbol)) return;
        var intermediateList = new ArrayList<>(symbols);
        intermediateList.add(symbol);
        if (streamId != null) wsStreamClient.closeConnection(streamId);
        streamId = wsStreamClient.combineStreams(addStreamType(intermediateList), this::processEvent);
        symbols.add(symbol);
    }

    public void removeItem(@NonNull String symbol) {
        if (!symbols.contains(symbol)) return;
        var intermediateList = new ArrayList<>(symbols);
        intermediateList.remove(symbol);
        if (streamId != null) wsStreamClient.closeConnection(streamId);
        streamId = wsStreamClient.combineStreams(addStreamType(intermediateList), this::processEvent);
        symbols.remove(symbol);
    }

    private String addStreamType(String symbol) {
        return symbol + "@trade";
    }

    private ArrayList<String> addStreamType(List<String> symbols) {
        return symbols.stream().map(this::addStreamType).collect(Collectors.toCollection(ArrayList::new));
    }

    private void processEvent(String event) {
        listener.processTrade(converter.toITradePrice(event));
    }

    public List<String> getItems() {
        return new ArrayList<>(symbols);
    }

    @Override
    public void close() throws Exception {
        wsStreamClient.closeConnection(streamId);
    }
}
