package kz.insar.checkbinance.tradeprice.binance;

import com.binance.connector.client.WebSocketStreamClient;
import kz.insar.checkbinance.tradeprice.ITradePriceListener;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
class StreamNode implements AutoCloseable{
    @NonNull
    private final WebSocketStreamClient wsStreamClient;
    @NonNull
    private final ITradePriceListener listener;
    @NonNull
    private final BinanceWebSocketConverter converter;
    @Setter
    private int streamId;
    private final ArrayList<String> symbols;

    public StreamNode(@NonNull WebSocketStreamClient wsStreamClient, @NonNull ITradePriceListener listener,
                      @NonNull BinanceWebSocketConverter converter, @NonNull List<String> symbols) {
        this.wsStreamClient = wsStreamClient;
        this.listener = listener;
        this.converter = converter;
        this.streamId = 0;
        this.symbols = new ArrayList<>(symbols);
    }

    public StreamNode(@NonNull WebSocketStreamClient wsStreamClient, @NonNull ITradePriceListener listener,
                      @NonNull BinanceWebSocketConverter converter, @NonNull String... symbol) {
        this(wsStreamClient, listener, converter, List.of(symbol));
    }

    public void addSymbol(@NonNull String symbol) {
        if (symbols.contains(symbol)) return;
        var intermediateList = new ArrayList<>(symbols);
        intermediateList.add(symbol);
        synchronized (wsStreamClient) {
            wsStreamClient.closeConnection(streamId);
            streamId = wsStreamClient.combineStreams(addStreamType(intermediateList), this::processEvent);
            symbols.add(symbol);
        }
    }

    public void removeSymbol(@NonNull String symbol) {
        if (!symbols.contains(symbol)) return;
        var intermediateList = new ArrayList<>(symbols);
        intermediateList.remove(symbol);
        synchronized (wsStreamClient) {
            wsStreamClient.closeConnection(streamId);
            streamId = wsStreamClient.combineStreams(addStreamType(intermediateList), this::processEvent);
            symbols.remove(symbol);
        }
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



    public int getSymbolCount() {
        return symbols.size();
    }

    @Override
    @PreDestroy
    public void close() throws Exception {
        wsStreamClient.closeConnection(streamId);
    }
}