package kz.insar.checkbinance.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class SymbolShortDTO {
    private Integer id;
    private String name;
}
