package kz.insar.checkbinance.client;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;

import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureMockMvc
@SpringBootTest
class ClientTest {
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc(mvc);
    }

    @Test
    void testX() {
        given()
                .log().all()
                .accept(ContentType.JSON)
        .when()
                .get("https://api.binance.com/api/v3/exchangeInfo")
        .then()
                .log().all()
                .assertThat()
                .statusCode(200);
    }
}