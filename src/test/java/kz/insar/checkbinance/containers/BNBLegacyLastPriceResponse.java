package kz.insar.checkbinance.containers;

import kz.insar.checkbinance.client.RecentTradeDTO;

import java.util.List;

public class BNBLegacyLastPriceResponse {

    private final List<BNBLegacyLastPrice> prices;

    public List<RecentTradeDTO> toRecentTradeDTOs() {
        return null;
    }

}
