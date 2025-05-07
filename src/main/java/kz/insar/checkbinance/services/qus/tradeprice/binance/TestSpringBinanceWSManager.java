package kz.insar.checkbinance.services.qus.tradeprice.binance;

import com.binance.connector.client.WebSocketStreamClient;
import com.binance.connector.client.impl.WebSocketStreamClientImpl;
import lombok.NonNull;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;

public class TestSpringBinanceWSManager {
    @NonNull
    private WebSocketStreamClient wsStreamClient;

    @PostConstruct
    public void init() {
        wsStreamClient = new WebSocketStreamClientImpl();
        ArrayList<String> streams = new ArrayList<>();
        streams.add("btcusdt@trade");

        int streamID2 = wsStreamClient.combineStreams((ArrayList<String>) streams, ((event) -> {
            System.out.println(event);
        }));
    }

    @PreDestroy
    public void close() {
        wsStreamClient.closeAllConnections();
    }

}
