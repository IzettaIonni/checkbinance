package kz.insar.checkbinance.containers;

import kz.insar.checkbinance.client.RecentTradeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RecentTradesWithSymbol {
    private String symbol;
    private List<RecentTradeDTO> recentTrades;
}
