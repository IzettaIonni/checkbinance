package kz.insar.checkbinance.tradeprice.binance;

import com.binance.connector.client.WebSocketStreamClient;
import kz.insar.checkbinance.tradeprice.ITradePriceListener;
import kz.insar.checkbinance.tradeprice.StreamNode;
import kz.insar.checkbinance.tradeprice.StreamNodeFactory;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class BinanceStreamNodeFactory implements StreamNodeFactory {

    @NonNull
    private final WebSocketStreamClient wsStreamClient;
    @NonNull
    private final ITradePriceListener listener;
    @NonNull
    private final BinanceWebSocketConverter converter;

    @Override
    public StreamNode createStreamNode() {
        return new BinanceStreamNode(wsStreamClient, listener, converter);
    }
}
