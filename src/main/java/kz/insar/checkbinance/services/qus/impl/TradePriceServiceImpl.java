package kz.insar.checkbinance.services.qus.impl;

import com.binance.connector.client.impl.WebSocketStreamClientImpl;
import kz.insar.checkbinance.repositories.SymbolSubscriptionPriceRepository;
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

@Component
@AllArgsConstructor
public class TradePriceServiceImpl implements TradePriceService {

    ITradePriceListener listener;
    ITradePriceSubscriptionManager manager;
    SymbolSubscriptionPriceRepository subscriptionRepository;

    @Autowired
    public TradePriceServiceImpl(SymbolSubscriptionPriceRepository subscriptionRepository) {
        this.listener = new PrintListener();
        this.manager = new WebSocketManagerImpl(
                new BinanceStreamNodeFactory(
                        new WebSocketStreamClientImpl(), this.listener, new BinanceWebSocketConverter()),
                5);
        this.subscriptionRepository = subscriptionRepository;
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
    public void start() {
        reconciliation();
    }

    @Scheduled(fixedDelay = 300000)
    public void reconciliation() {
        subscriptionRepository.getAllSymbolNames().forEach((e) -> manager.subscribeItem(e));
    }

    @PreDestroy
    public void stop() {
        manager.close();
    }

}
