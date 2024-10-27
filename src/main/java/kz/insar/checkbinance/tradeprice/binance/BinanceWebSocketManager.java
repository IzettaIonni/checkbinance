package kz.insar.checkbinance.tradeprice.binance;

import com.binance.connector.client.WebSocketStreamClient;
import com.binance.connector.client.impl.WebSocketStreamClientImpl;
import kz.insar.checkbinance.tradeprice.ITradePriceListener;
import kz.insar.checkbinance.tradeprice.ITradePriceSubscriptionManager;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BinanceWebSocketManager implements ITradePriceSubscriptionManager {

    @NonNull
    private final WebSocketStreamClient wsStreamClient;
    @NonNull
    private final ITradePriceListener listener;
    @NonNull
    private final long maxStreamCount; //n на схеме
    private Map<Long, Integer> streams;
    private Map<Long, List<String>> symbolsOfStreams;
    private Set<String> implementedSymbols;
    private long currentStreamIndex;
    private long symbolCountPerStream;


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
            wsStreamClient.aggTradeStream(symbol, ((event) -> listener.processTrade(converter.toITradePrice(event)))));
            symbolsOfStreams.put(currentStreamIndex, List.of(symbol));
            implementedSymbols.add(symbol);
        }
        else if (symbolsOfStreams.get(currentStreamIndex).size() < symbolCountPerStream) {
            wsStreamClient.closeConnection(streams.get(currentStreamIndex));
            var listSymbols = symbolsOfStreams.get(currentStreamIndex);
            listSymbols.add(symbol);
            symbolsOfStreams.replace(currentStreamIndex, listSymbols);
            streams.replace(currentStreamIndex,
                    wsStreamClient.combineStreams(listSymbols, ((event) -> listener.processTrade(converter.toITradePrice(event)))));
            implementedSymbols.add(symbol);
        }
        currentStreamIndex += 1;
    }

    @Override
    public void unsubscribeSymbol(String symbol) {

    }

    public void init() {
        ArrayList<String> streams = new ArrayList<>();
        streams.add("btcusdt@trade");
        streams.add("bnbusdt@trade");

        System.out.println(streams.size());
        int streamID2 = wsStreamClient.combineStreams((ArrayList<String>) streams, ((event) -> {
            System.out.println(event);
                }));
   }

    public void close() {
        wsStreamClient.closeAllConnections();
    }

}
