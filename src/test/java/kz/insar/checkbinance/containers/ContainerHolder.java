package kz.insar.checkbinance.containers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockserver.client.MockServerClient;
import org.springframework.test.web.reactive.server.MockServerConfigurer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.lang.annotation.Annotation;

public class ContainerHolder implements Extension, BeforeAllCallback {

    private static final PostgreSQLContainer<?> postgreSql = PostgreSQLContainerBuilder
            .getInstance().startCommonContainer();

    public static final PostgreSQLContainer<?> getPostgreSQL() {
        return postgreSql;
    }

    private static final MockServerContainer mockServer = MockServerContainerBuilder
            .getInstance().startMockBinanceContainer();

    public static final MockServerContainer getMockServer() {
        return mockServer;
    }

    public static BinanceAPIHelper getBinanceAPIHelper() {
        return new BinanceAPIHelper(new MockServerClient(mockServer.getHost(), mockServer.getServerPort()));
    }


    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {

    }
}