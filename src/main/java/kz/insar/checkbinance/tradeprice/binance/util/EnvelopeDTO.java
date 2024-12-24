package kz.insar.checkbinance.tradeprice.binance.util;

//todo ingonre all unknown properties

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnvelopeDTO {

    private StreamDataDTO data;

}
