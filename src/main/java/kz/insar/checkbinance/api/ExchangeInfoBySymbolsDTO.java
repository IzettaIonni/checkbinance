package kz.insar.checkbinance.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeInfoBySymbolsDTO {
    private Long serverTime;
    private List<SymbolParamsDTO> symbols;
}
