package kz.insar.checkbinance.config;

import kz.insar.checkbinance.client.BinanceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BinanceConfig {

    @Bean
    BinanceClient binanceClient( @Value(value = "${binance-client.base-url}") String baseUrl) {
        return new BinanceClient(baseUrl);
    }

}
