package kz.insar.checkbinance.shceduled;

import kz.insar.checkbinance.services.TickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduledUpdate {

    @Autowired
    TickerService tickerService;
//todo uncomment

    @Scheduled(fixedDelay = 300000)
    public void scheduledSymbolUpdate() {
        tickerService.updateSymbols();
    }
}
