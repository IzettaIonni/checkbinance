package kz.insar.checkbinance.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RecentTradesDTO {
    private List<RecentTradeDTO> recentTrades;
}


