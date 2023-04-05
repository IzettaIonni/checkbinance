package kz.insar.checkbinance.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ExchangeInfoBySymbolsDTO {
    private Long serverTime;
    private List<SymbolParamsDTO> symbols;
}
