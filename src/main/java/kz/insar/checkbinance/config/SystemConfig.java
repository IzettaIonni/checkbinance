package kz.insar.checkbinance.config;

import kz.insar.checkbinance.common.CurrentTimeSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.function.Supplier;

@Configuration
public class SystemConfig {

    @Bean
    public Supplier<LocalDateTime> currentTime() {
        return new CurrentTimeSupplier(Clock.system(ZoneId.of("Asia/Tashkent")));
    }

}
