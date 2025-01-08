package kz.insar.checkbinance.services.qus.impl;

import com.binance.connector.client.impl.WebSocketStreamClientImpl;
import kz.insar.checkbinance.domain.SymbolId;
import kz.insar.checkbinance.repositories.SymbolRepository;
import kz.insar.checkbinance.repositories.entities.SymbolName;
import kz.insar.checkbinance.services.qus.TradePriceService;
import kz.insar.checkbinance.services.qus.tradeprice.ITradePriceListener;
import kz.insar.checkbinance.services.qus.tradeprice.ITradePriceSubscriptionManager;
import kz.insar.checkbinance.services.qus.tradeprice.binance.BinanceStreamNodeFactory;
import kz.insar.checkbinance.services.qus.tradeprice.binance.BinanceWebSocketConverter;
import kz.insar.checkbinance.services.qus.tradeprice.binance.PrintListener;
import kz.insar.checkbinance.services.qus.tradeprice.impl.WebSocketManagerImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TradePriceServiceImpl implements TradePriceService {

    ITradePriceListener listener;
    ITradePriceSubscriptionManager manager;
    SymbolRepository repository;

    @Autowired
    public TradePriceServiceImpl(SymbolRepository repository) {
        this.listener = new PrintListener();
        this.manager = new WebSocketManagerImpl(
                new BinanceStreamNodeFactory(
                        new WebSocketStreamClientImpl(), this.listener, new BinanceWebSocketConverter()),
                5);
        this.repository = repository;
    }

//    @PostConstruct
//    public void start() {
//        var symbols = repository.findAllNamesBy().stream().map(SymbolName::getSymbolName).collect(Collectors.toList());
//        for (int i = 0; i < 10; i++) {
////            manager.subscribeItem("btcusdt");
////            manager.subscribeItem("bnbbtc");
////            manager.subscribeItem("ethusdt");
//            manager.subscribeItem(symbols.get(i));
//            System.out.println(symbols.get(i));
//
//        }
//    }
    @PostConstruct
    @Scheduled(fixedDelay = 300000)
    public void start() {
        subscriptionRepisitory.getAllSymbolNames.stream().forEach((e) -> manager.subscribeItem(e));

    }

    @PreDestroy
    public void stop() {
        manager.close();
    }

}
