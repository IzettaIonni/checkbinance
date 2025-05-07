package kz.insar.checkbinance.services.qus.tradeprice.binance;

import com.binance.connector.client.WebSocketStreamClient;
import kz.insar.checkbinance.services.qus.tradeprice.StreamNode;
import kz.insar.checkbinance.services.qus.tradeprice.StreamNodeFactory;
import kz.insar.checkbinance.services.qus.tradeprice.ITradePriceListener;
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
