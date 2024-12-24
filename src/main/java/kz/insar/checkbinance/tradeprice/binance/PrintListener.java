package kz.insar.checkbinance.tradeprice.binance;

import kz.insar.checkbinance.tradeprice.ITradePrice;
import kz.insar.checkbinance.tradeprice.ITradePriceListener;

public class PrintListener implements ITradePriceListener {

    @Override
    public void processTrade(ITradePrice tradePrice) {
        System.out.println(tradePrice.getItem() + "\n" + tradePrice.getPrice());
    }
}
