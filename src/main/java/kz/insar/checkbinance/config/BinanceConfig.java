package kz.insar.checkbinance.config;

import kz.insar.checkbinance.client.BinanceClient;
import kz.insar.checkbinance.client.BinanceRestTemplateErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BinanceConfig {

    @Bean
    BinanceClient binanceClient(@Value(value = "${binance-client.base-url:https://api.binance.com/api/v3}") String baseUrl,
                                RestTemplateBuilder restTemplateBuilder) {
        return new BinanceClient(
                baseUrl, restTemplateBuilder.errorHandler(new BinanceRestTemplateErrorHandler()).build());
    }

}
