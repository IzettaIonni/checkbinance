package kz.insar.checkbinance.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
//@Builder(toBuilder = true)
public class ExchangeInfoResponseDTO {
    private Long serverTime;
    private List<SymbolDTO> symbols;
}
