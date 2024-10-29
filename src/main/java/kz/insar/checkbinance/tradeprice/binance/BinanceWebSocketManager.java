package kz.insar.checkbinance.tradeprice.binance;

import com.binance.connector.client.WebSocketStreamClient;
import com.binance.connector.client.impl.WebSocketStreamClientImpl;
import kz.insar.checkbinance.tradeprice.ITradePriceListener;
import kz.insar.checkbinance.tradeprice.ITradePriceSubscriptionManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class BinanceWebSocketManager implements ITradePriceSubscriptionManager { //todo сделать tread safe поскольку будет вызываться спрингом

    @NonNull
    private final WebSocketStreamClient wsStreamClient;
    @NonNull
    private final ITradePriceListener listener;
    @NonNull
    private final BinanceWebSocketConverter converter;
    @NonNull
    private final long maxStreamCount; //n на схеме
    private final List<StreamNode> streams;
    private Set<String> symbols;
    private int currentStreamIndex; //c на схеме
    private int symbolCountPerStream; //m на схеме

    static class StreamNode {
        @Getter
        @Setter
        private int streamId;
        @Getter
        private final ArrayList<String> symbols;

        public StreamNode(@NonNull List<String> symbols) {
            this.streamId = 0;
            this.symbols = new ArrayList<>(symbols);
        }

        public StreamNode(@NonNull String symbol) {
            this(List.of(symbol));
        }

        public void addSymbol(@NonNull String symbol) {
            if (symbols.contains(symbol)) return;
            symbols.add(symbol);
            //todo add subs here
        }

        public void removeSymbol() {
            //todo
        }

        public int getSymbolCount() {
            return symbols.size();
        }
    }

    public BinanceWebSocketManager(@NotNull WebSocketStreamClient wsStreamClient,
                                   @NotNull ITradePriceListener listener,
                                   @NonNull BinanceWebSocketConverter converter,
                                   @NotNull Long maxStreamCount) {
        this.wsStreamClient = wsStreamClient;
        this.listener = listener;
        this.converter = converter;
        this.maxStreamCount = maxStreamCount;
        this.streams = new ArrayList<>(); //сделать возможность задать снаружи для тестирования
        this.symbols = new HashSet<>();
        this.symbolCountPerStream = 1;
        this.currentStreamIndex = -1;
    }

    public BinanceWebSocketManager(ITradePriceListener listener) {
        this(new WebSocketStreamClientImpl(), listener, new BinanceWebSocketConverter(), 1000L);
    }


    //

    @Override
    public void subscribeSymbol(String symbol) {
        if (symbols.contains(symbol)) return;

        if (streams.size() < maxStreamCount) {
            var node = new StreamNode(wsStreamClient);
            node.addSymbol(symbol);
            //node.setStreamId(wsStreamClient.combineStreams(node.getSymbols(), this::processEvent));
            streams.add(node);
            symbols.add(symbol);
            currentStreamIndex++;
        }
        else {
            for (int i = 0; i < currentStreamIndex; i++) {
                var streamNode = streams.get(i);
                if (streamNode.getSymbolCount() < symbolCountPerStream) {
                    streamNode.addSymbol(symbol);
                    return;
                }
            }
            if (symbolsOfStreams.get(currentStreamIndex).size() < symbolCountPerStream)
            wsStreamClient.closeConnection(streams.get(currentStreamIndex));
            var listSymbols = symbolsOfStreams.get(currentStreamIndex);
            listSymbols.add(symbol);
            listSymbols = listSymbols.stream().map(this::addStreamType).collect(Collectors.toList());
            symbolsOfStreams.replace(currentStreamIndex, listSymbols);
            streams.replace(currentStreamIndex,
                    wsStreamClient.combineStreams(new ArrayList<>(listSymbols), this::processEvent));
            symbols.add(symbol);
        }

        if (currentStreamIndex == maxStreamCount - 1) {
            currentStreamIndex = -1;
            symbolCountPerStream++;
        }
    }

    @Override
    public void unsubscribeSymbol(String symbol) {
        if (!symbols.contains(symbol)) return;
        for (int i = 0; i < maxStreamCount; i++) {
            var listSymbols = symbolsOfStreams.get(i);
            if (listSymbols.contains(symbol)) {
                listSymbols.remove(symbol); //todo закрыть стрим
                streams.replace(currentStreamIndex,
                        wsStreamClient.combineStreams(new ArrayList<>(listSymbols),this::processEvent));
                symbolsOfStreams.replace(i, listSymbols);
                symbols.remove(symbol);
                return;
            }
        }
        throw new IllegalStateException("No stream with such symbol is existed but symbol is marked as subscribed");
    }

    public void close() {
        wsStreamClient.closeAllConnections();
    }

    private String addStreamType(String symbol) {
        return symbol + "@trade";
    }

    private void processEvent(String event) {
        listener.processTrade(converter.toITradePrice(event));
    }

}
