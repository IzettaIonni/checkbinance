package kz.insar.checkbinance;

import kz.insar.checkbinance.config.YAMLConfig;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class CheckbinanceApplication {

	public static void main(String[] args) {
		if (args.length == 3) {
			
		}

		SpringApplication.run(CheckbinanceApplication.class, args);

	}

}