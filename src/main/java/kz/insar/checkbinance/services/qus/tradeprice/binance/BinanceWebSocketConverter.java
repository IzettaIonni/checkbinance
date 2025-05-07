package kz.insar.checkbinance.services.qus.tradeprice.binance;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.insar.checkbinance.services.qus.tradeprice.ITradePrice;
import kz.insar.checkbinance.services.qus.tradeprice.binance.util.EnvelopeDTO;
import lombok.SneakyThrows;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class BinanceWebSocketConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public ITradePrice toITradePrice(String trade) {
        var envelopeDTO = objectMapper.readValue(trade, EnvelopeDTO.class);
        var streamTradeDTO = envelopeDTO.getData();
        return new BinanceWebSocketTrade(LocalDateTime.ofInstant(Instant.ofEpochMilli(streamTradeDTO.getTime()), ZoneOffset.UTC),
                streamTradeDTO.getSymbol(), streamTradeDTO.getPrice(), streamTradeDTO.getQuantity());
    }




}
