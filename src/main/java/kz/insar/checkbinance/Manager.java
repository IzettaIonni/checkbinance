package kz.insar.checkbinance;

import com.binance.connector.client.WebSocketStreamClient;
import com.binance.connector.client.impl.WebSocketStreamClientImpl;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;


@Component
public class Manager {
    private WebSocketStreamClient wsStreamClient;

    @PostConstruct
    public void init() {
        wsStreamClient = new WebSocketStreamClientImpl();
        ArrayList<String> streams = new ArrayList<>();
        streams.add("btcusdt@trade");
        streams.add("bnbusdt@trade");

        int streamID2 = wsStreamClient.combineStreams(streams, ((event) -> {
            System.out.println(event);
                }));
    }

    @PreDestroy
    public void close() {
        wsStreamClient.closeAllConnections();
    }
}
