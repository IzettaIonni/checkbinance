package kz.insar.checkbinance.services.qus.tradeprice.binance;

import kz.insar.checkbinance.services.qus.tradeprice.ITradePrice;
import kz.insar.checkbinance.services.qus.tradeprice.ITradePriceListener;

public class PrintListener implements ITradePriceListener {

    @Override
    public void processTrade(ITradePrice tradePrice) {
        System.out.println(tradePrice.getItem() + "\n" + tradePrice.getPrice());
    }
}
