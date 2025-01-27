package kz.insar.checkbinance.shceduled;

import kz.insar.checkbinance.services.TickerService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@DisallowConcurrentExecution
public class SymbolUpdateJob implements Job {

    @Autowired
    private TickerService service;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.err.println("Symbol update started");
        service.updateSymbols();
        System.err.println("Symbol update finished");
    }
}
