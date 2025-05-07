package kz.insar.checkbinance.shceduled;

import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzSubmitJobs {

    @Bean(name = "symbolUpdate")
    public JobDetailFactoryBean jobSymbolUpdate() {
        return QuartzConfig.createJobDetail(SymbolUpdateJob.class, "Symbol Update Job");
    }

    @Bean(name = "symbolUpdateTrigger")
    public SimpleTriggerFactoryBean triggerMemberStats(@Qualifier("symbolUpdate") JobDetail jobDetail) {
        return QuartzConfig.createTrigger(jobDetail, 30000, "Symbol Update Trigger");
    }
}
