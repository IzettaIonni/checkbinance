package kz.insar.checkbinance.converters;

import kz.insar.checkbinance.api.LastPriceDTO;
import kz.insar.checkbinance.client.RecentTradeDTO;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Service
public class ApiConvertrer {

    public LastPriceDTO toApi(RecentTradeDTO recentTrade) {
        LastPriceDTO lastPriceDTO = new LastPriceDTO();
        lastPriceDTO.setId(recentTrade.getId());
        lastPriceDTO.setPrice(recentTrade.getPrice());
        lastPriceDTO.setTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(recentTrade.getTime()), TimeZone.getDefault().toZoneId()));
        return lastPriceDTO;
    }
}
