package kz.insar.checkbinance.tradeprice;

import com.binance.connector.client.impl.WebSocketStreamClientImpl;
import kz.insar.checkbinance.repositories.SymbolRepository;
import kz.insar.checkbinance.repositories.entities.SymbolName;
import kz.insar.checkbinance.tradeprice.binance.BinanceStreamNodeFactory;
import kz.insar.checkbinance.tradeprice.binance.BinanceWebSocketConverter;
import kz.insar.checkbinance.tradeprice.binance.PrintListener;
import kz.insar.checkbinance.tradeprice.impl.WebSocketManagerImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class BinanceTradePrice {

    ITradePriceListener listener;
    ITradePriceSubscriptionManager manager;
    SymbolRepository repository;

    @Autowired
    public BinanceTradePrice(SymbolRepository repository) {
        this.listener = new PrintListener();
        this.manager = new WebSocketManagerImpl(
                new BinanceStreamNodeFactory(
                        new WebSocketStreamClientImpl(), this.listener, new BinanceWebSocketConverter()),
                5);
        this.repository = repository;
    }

    @PostConstruct
    public void start() {
        var symbols = repository.findAllNamesBy().stream().map(SymbolName::getSymbolName).collect(Collectors.toList());
        for (int i = 0; i < 1; i++) {
            manager.subscribeItem("btcusdt");
            manager.subscribeItem("bnbbtc");
            manager.subscribeItem("ethusdt");
        }
    }

    @PreDestroy
    public void stop() {
        manager.close();
    }
}
