package kz.insar.checkbinance.tradeprice;

public interface ITradePriceSubscriptionManager{

    void subscribeItem(String item);
    void unsubscribeItem(String item);

}
