package kz.insar.checkbinance.tradeprice.binance;

import com.binance.connector.client.WebSocketStreamClient;
import com.binance.connector.client.impl.WebSocketStreamClientImpl;
import kz.insar.checkbinance.tradeprice.ITradePriceListener;
import kz.insar.checkbinance.tradeprice.ITradePriceSubscriptionManager;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class BinanceWebSocketManager implements ITradePriceSubscriptionManager {

    @NonNull
    private final WebSocketStreamClient wsStreamClient;
    @NonNull
    private final ITradePriceListener listener;
    @NonNull
    private final long maxStreamCount; //n на схеме
    private Map<Long, Integer> streams; // streamIndex, streamID
    private Map<Long, List<String>> symbolsOfStreams; // streamIndex, streamSymbols
    private Set<String> implementedSymbols;
    private long currentStreamIndex; //c на схеме
    private long symbolCountPerStream; //m на схеме


    public BinanceWebSocketManager(@NotNull WebSocketStreamClient wsStreamClient,
                                   @NotNull ITradePriceListener listener,
                                   @NotNull Long maxStreamCount) {
        this.wsStreamClient = wsStreamClient;
        this.listener = listener;
        this.maxStreamCount = maxStreamCount;
        this.streams = new HashMap<>();
        this.symbolsOfStreams = new HashMap<>();
        this.implementedSymbols = new HashSet<>();
        this.symbolCountPerStream = 1;
        this.currentStreamIndex = 0;
    }

    public BinanceWebSocketManager(ITradePriceListener listener) {
        this(new WebSocketStreamClientImpl(), listener, 1000L);
    }

    @Override
    public void subscribeSymbol(String symbol) {
        if (implementedSymbols.contains(symbol)) return;
        if (currentStreamIndex == maxStreamCount - 1) {
            currentStreamIndex = 0;
            symbolCountPerStream += 1;
        }

        if (streams.size() < maxStreamCount) {
            streams.put(currentStreamIndex,
            wsStreamClient.combineStreams(addStreamType(symbol), ((event) -> listener.processTrade(converter.toITradePrice(event)))));
            symbolsOfStreams.put(currentStreamIndex, List.of(symbol));
            implementedSymbols.add(symbol);
        }
        else if (symbolsOfStreams.get(currentStreamIndex).size() < symbolCountPerStream) {
            wsStreamClient.closeConnection(streams.get(currentStreamIndex));
            var listSymbols = symbolsOfStreams.get(currentStreamIndex);
            listSymbols.add(symbol);
            listSymbols = listSymbols.stream().map(this::addStreamType).collect(Collectors.toList());
            symbolsOfStreams.replace(currentStreamIndex, listSymbols);
            streams.replace(currentStreamIndex,
                    wsStreamClient.combineStreams(listSymbols, ((event) -> listener.processTrade(converter.toITradePrice(event)))));
            implementedSymbols.add(symbol);
        }
        currentStreamIndex += 1;
    }

    @Override
    public void unsubscribeSymbol(String symbol) {
        if (!implementedSymbols.contains(symbol)) return;
        for (long i = 0; i < maxStreamCount; i++) {
            var listSymbols = symbolsOfStreams.get(i);
            if (listSymbols.contains(symbol)) {
                listSymbols.remove(symbol);
                streams.replace(currentStreamIndex,
                        wsStreamClient.combineStreams(listSymbols, ((event) -> listener.processTrade(converter.toITradePrice(event)))));
                symbolsOfStreams.replace(i, listSymbols);
                implementedSymbols.remove(symbol);
                return;
            }
        }
        throw new IllegalStateException("No stream with such symbol is existed but symbol is marked as implemented");
    }

    public void close() {
        wsStreamClient.closeAllConnections();
    }

    private String addStreamType(String symbol) {
        return symbol + "@trade";
    }

}
