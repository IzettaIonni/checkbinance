package kz.insar.checkbinance.client;

import kz.insar.checkbinance.containers.ContainerHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@AutoConfigureMockMvc
@Disabled
@SpringBootTest
//todo refactor
class BinanceClientTest {
    BinanceClient service;

    private final PostgreSQLContainer<?> postgreSQL = ContainerHolder.getPostgreSQL();

}