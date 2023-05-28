package kz.insar.checkbinance.shceduled;

import kz.insar.checkbinance.services.TickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalTime;

@Configuration
@EnableScheduling
public class ScheduledUpdate {

    @Autowired
    TickerService tickerService;

//    @Scheduled(fixedDelay = 300000)
//    private void shceduledSymbolUpdate() {
//        tickerService.updateSymbols();
//    }
}
