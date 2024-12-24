package kz.insar.checkbinance.tradeprice.binance.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamDataDTO {
    //todo {"stream":"btcusdt@trade",
    // "data":{"e":"trade","E":1732636519372,"s":"BTCUSDT","t":4156456888,"p":"93028.00000000","q":"0.00023000","T":1732636519372,"m":true,"M":true}}
    @JsonProperty("s")
    //symbol
    private String symbol;
    @JsonProperty("T")
    //trade time
    private Long time;
    @JsonProperty("p")
    //price
    private String price;
    @JsonProperty("q")
    //quantity
    private String quantity;

    public String getSymbol() {
        return symbol;
    }

    public Long getTime() {
        return time;
    }

    public BigDecimal getPrice() {
        return new BigDecimal(price);
    }

    public BigDecimal getQuantity() {
        return new BigDecimal(quantity);
    }

}
