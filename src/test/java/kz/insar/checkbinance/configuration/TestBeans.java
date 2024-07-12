package kz.insar.checkbinance.configuration;

import kz.insar.checkbinance.containers.ContainerHolder;
import kz.insar.checkbinance.helpers.symbol.TestSymbolRepository;
import kz.insar.checkbinance.helpers.symbol.TestSymbolRepositoryImpl;
import kz.insar.checkbinance.services.SymbolService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration
public class TestBeans {

    //private final PostgreSQLContainer<?> postgreSQL = ContainerHolder.getPostgreSQL();

}
