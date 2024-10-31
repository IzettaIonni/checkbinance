package kz.insar.checkbinance.tradeprice.binance;

import com.binance.connector.client.WebSocketStreamClient;
import com.binance.connector.client.impl.WebSocketStreamClientImpl;
import kz.insar.checkbinance.tradeprice.ITradePriceListener;
import kz.insar.checkbinance.tradeprice.ITradePriceSubscriptionManager;
import kz.insar.checkbinance.tradeprice.StreamNode;
import kz.insar.checkbinance.tradeprice.StreamNodeFactory;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@AllArgsConstructor
public class BinanceWebSocketManager implements ITradePriceSubscriptionManager {

    @NonNull
    private final StreamNodeFactory streamNodeFactory;
    @NonNull
    private final long maxStreamCount; //n на схеме
    @NonNull
    private final List<StreamNode> streams;
    private Set<String> items;
    private AtomicInteger currentStreamIndex; //c на схеме
    private AtomicInteger ItemsPerStream; //m на схеме

    public BinanceWebSocketManager(@NonNull StreamNodeFactory streamNodeFactory, long maxStreamCount) {
        this(streamNodeFactory, maxStreamCount, new ArrayList<>(), new HashSet<>(), new AtomicInteger(0), new AtomicInteger(1));
    }

    public BinanceWebSocketManager(@NonNull StreamNodeFactory streamNodeFactory) {
        this(streamNodeFactory, 1000L);
    }

    @Override
    public void subscribeItem(String item) {
        if (items.contains(item)) return;

        //following iterations
        if (streams.size() == maxStreamCount) {
            for (int i = 0; i < currentStreamIndex.get(); i++) {
                var streamNode = streams.get(i);
                synchronized (streams) {
                    if (streamNode.getItemsCount() < ItemsPerStream.get()) {
                        streamNode.addItem(item);
                        return;
                    }
                }
            }
            synchronized (streams) {
                streams.get(currentStreamIndex.get()).addItem(item);
                items.add(item);
                currentStreamIndex.incrementAndGet();
                if (currentStreamIndex.get() == maxStreamCount - 1) {
                    currentStreamIndex.set(-1);
                    ItemsPerStream.incrementAndGet();
                }
                return;
            }
        }

        //first iteration (streams is not full)
        synchronized (streams) {
            if (streams.size() < maxStreamCount) {
                StreamNode node = null;
                try {
                    node = streamNodeFactory.createStreamNodeAndAddItem(item);
                    streams.add(node);
                    items.add(item);

                } catch (Exception e) {
                    streams.remove(node);
                    items.remove(item);
                    throw e;
                }
            }
        }

    }

    @Override
    public void unsubscribeItem(String item) {
        if (!items.contains(item)) return;

        for (var stream : streams) {
            if (stream.getItems().contains(item)) {
                stream.removeItem(item);
            }
        }

        throw new IllegalStateException("No stream with such symbol is existed but symbol is marked as subscribed");
    }

}
