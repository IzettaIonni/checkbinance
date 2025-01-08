package kz.insar.checkbinance.services.qus.tradeprice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ITradePrice {

    String getItem();
    BigDecimal getPrice();
    BigDecimal getQuantity();
    LocalDateTime getTime();

}
