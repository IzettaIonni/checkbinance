package kz.insar.checkbinance.api;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class LastPriceDTO {
    private String symbol;
    private Integer id;
    private BigDecimal price;
    private LocalDateTime time;

    public static class LastPriceDTOBuilder {
        public LastPriceDTOBuilder price(BigDecimal value) {
            this.price = value;
            return this;
        }

        public LastPriceDTOBuilder price(long value) {
            return price(BigDecimal.valueOf(value));
        }

        public LastPriceDTOBuilder price(String value) {
            return price(new BigDecimal(value));
        }

        public LastPriceDTOBuilder time(LocalDateTime value) {
            this.time = value;
            return this;
        }

        public LastPriceDTOBuilder time(String value) {
            return time(LocalDateTime.parse(value));
        }

        public LastPriceDTOBuilder time(long value) {
            return time(LocalDateTime.ofEpochSecond(value, 0, ZoneOffset.UTC));
        }
    }

}
