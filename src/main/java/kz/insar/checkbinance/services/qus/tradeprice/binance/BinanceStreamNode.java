package kz.insar.checkbinance.services.qus.tradeprice.binance;

import com.binance.connector.client.WebSocketStreamClient;
import com.binance.connector.client.utils.websocketcallback.WebSocketMessageCallback;
import kz.insar.checkbinance.services.qus.tradeprice.ITradePriceListener;
import kz.insar.checkbinance.services.qus.tradeprice.StreamNode;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;


@AllArgsConstructor
class BinanceStreamNode implements StreamNode, AutoCloseable{
    @NonNull
    private final WebSocketStreamClient wsStreamClient;
    @NonNull
    private final WebSocketMessageCallback callback;
    private final List<String> symbols;
    @Getter
    private Integer streamId;

    public BinanceStreamNode(@NonNull WebSocketStreamClient wsStreamClient, @NonNull WebSocketMessageCallback callback) {
        this(wsStreamClient, callback, new ArrayList<>(), null);
    }

    public BinanceStreamNode(@NonNull WebSocketStreamClient wsStreamClient, @NonNull ITradePriceListener listener,
                             @NonNull BinanceWebSocketConverter converter, @NonNull String... symbol) {
        this(wsStreamClient, new Callback(listener, converter), new ArrayList<>(Arrays.asList(symbol)), null);
    }

    public void addItem(@NonNull String symbol) {
        if (symbols.contains(symbol)) return;
        var intermediateList = new ArrayList<>(symbols);
        intermediateList.add(symbol);
        if (streamId != null) wsStreamClient.closeConnection(streamId);
        streamId = wsStreamClient.combineStreams(addStreamType(intermediateList), callback);
        symbols.add(symbol);
    }

    public void removeItem(@NonNull String symbol) {
        if (!symbols.contains(symbol)) return;
        var intermediateList = new ArrayList<>(symbols);
        intermediateList.remove(symbol);
        if (streamId != null) wsStreamClient.closeConnection(streamId);
        if (!(symbols.size() == 1)) streamId = wsStreamClient.combineStreams(addStreamType(intermediateList), callback);
        symbols.remove(symbol);
    }

    private String addStreamType(String symbol) {
        return symbol + "@trade";
    }

    private ArrayList<String> addStreamType(List<String> symbols) {
        return symbols.stream().map(this::addStreamType).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<String> getItems() {
        return new ArrayList<>(symbols);
    }

    @Override
    public void close() {
        wsStreamClient.closeConnection(streamId);
    }

    void setStreamId(Integer id) {
        streamId = id;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Callback implements WebSocketMessageCallback {
        @NonNull
        private final ITradePriceListener listener;
        @NonNull
        private final BinanceWebSocketConverter converter;

        @Override
        public void onMessage(String data) {
            listener.processTrade(converter.toITradePrice(data));
        }
    }
}
