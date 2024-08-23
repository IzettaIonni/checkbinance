package kz.insar.checkbinance.helpers.symbol;

import kz.insar.checkbinance.containers.BinanceAPIHelper;
import kz.insar.checkbinance.containers.ContainerHolder;
import kz.insar.checkbinance.helpers.CheckbinanceServiceHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ExtendWith(ContainerHolder.class)
@TestMethodOrder(MethodOrderer.Random.class)
public class TestSymbolRepositoryImplIT {

    @Autowired
    private CheckbinanceServiceHelper checkbinanceServiceHelper;

    @Test
    void testTestSymbolRepositoryCleanTestSymbols() {
        checkbinanceServiceHelper.createRandomSymbols(10).createAndSubscribeRandomSymbols(10);
        assertFalse(checkbinanceServiceHelper.getSymbols().isEmpty());

        checkbinanceServiceHelper.cleanTestSymbols();

        assertTrue(checkbinanceServiceHelper.getSymbols().isEmpty());
    }

}
