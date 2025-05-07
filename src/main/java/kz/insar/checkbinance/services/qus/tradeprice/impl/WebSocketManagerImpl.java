package kz.insar.checkbinance.services.qus.tradeprice.impl;

import kz.insar.checkbinance.services.qus.tradeprice.ITradePriceSubscriptionManager;
import kz.insar.checkbinance.services.qus.tradeprice.StreamNode;
import kz.insar.checkbinance.services.qus.tradeprice.StreamNodeFactory;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class WebSocketManagerImpl implements ITradePriceSubscriptionManager {

    @NonNull
    private final StreamNodeFactory streamNodeFactory;
    @NonNull
    private final long maxStreamCount; //n на схеме
    @NonNull
    private final List<StreamNode> streams;
    private Set<String> items;
    private AtomicInteger currentStreamIndex; //c на схеме
    private AtomicInteger itemsPerStream; //m на схеме

    public WebSocketManagerImpl(@NonNull StreamNodeFactory streamNodeFactory, long maxStreamCount) {
        this(streamNodeFactory, maxStreamCount, new ArrayList<>(), new HashSet<>(), new AtomicInteger(0), new AtomicInteger(1));
    }

    public WebSocketManagerImpl(@NonNull StreamNodeFactory streamNodeFactory) {
        this(streamNodeFactory, 1000L);
    }

    @Override
    public void subscribeItem(String item) {
        if (items.contains(item)) return;

        //first iteration (streams is not full)
        if (streams.size() < maxStreamCount) {
            streams.add(streamNodeFactory.createStreamNodeAndAddItem(item));
            items.add(item);
            return;
        }

        //following iterations
        for (int i = 0; i < currentStreamIndex.get(); i++) {
            var streamNode = streams.get(i);
            if (streamNode.getItemsCount() < itemsPerStream.get()) {
                streamNode.addItem(item);
                items.add(item);
                return;
            }
        }
        streams.get(currentStreamIndex.get()).addItem(item);
        items.add(item);
        currentStreamIndex.incrementAndGet();
        if (currentStreamIndex.get() == maxStreamCount) {
            currentStreamIndex.set(0);
            itemsPerStream.incrementAndGet();
        }

    }

    @Override
    public void unsubscribeItem(String item) {
        if (!items.contains(item)) return;

        for (var streamNode : streams) {
            if (streamNode.isContainsItem(item)) {
                streamNode.removeItem(item);
                items.remove(item);
                return;
            }
        }

        throw new IllegalStateException("No stream with such symbol is existed but symbol is marked as subscribed");
    }

    public void close() {
        for (var node : streams) {
            node.close();
        }
    }

}
