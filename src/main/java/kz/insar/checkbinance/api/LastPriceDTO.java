package kz.insar.checkbinance.api;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
public class LastPriceDTO {
    private String symbol;
    private Integer id;
    private BigDecimal price;
    private LocalDateTime time;
}
