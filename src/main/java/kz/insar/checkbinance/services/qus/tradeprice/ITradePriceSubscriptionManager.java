package kz.insar.checkbinance.services.qus.tradeprice;

public interface ITradePriceSubscriptionManager{

    void subscribeItem(String item);
    void unsubscribeItem(String item);

    void close();

}
