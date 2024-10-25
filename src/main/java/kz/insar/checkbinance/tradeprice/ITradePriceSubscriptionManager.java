package kz.insar.checkbinance.tradeprice;

public interface ITradePriceSubscriptionManager{

    void subscribeSymbol(String symbol);
    void unsubscribeSymbol(String symbol);
    void close();

}
