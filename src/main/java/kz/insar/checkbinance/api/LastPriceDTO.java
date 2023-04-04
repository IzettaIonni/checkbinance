package kz.insar.checkbinance.api;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LastPriceDTO {
    private String symbol;
    private Long id;
    private BigDecimal price;
    private LocalDateTime time;
}
