package kz.insar.checkbinance.containers;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockserver.client.MockServerClient;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public class ContainerHolder implements Extension, BeforeAllCallback {

    private static final PostgreSQLContainer<?> postgreSql = PostgreSQLContainerBuilder
            .getInstance().startCommonContainer();

    private static final MockServerContainer mockServer = MockServerContainerBuilder
            .getInstance().startMockBinanceContainer();

    public static final MockServerContainer getMockServer() {
        return mockServer;
    }

    public static final PostgreSQLContainer<?> getPostgreSQL() {
        return postgreSql;
    }

    public static BinanceAPIHelper createBinanceAPIHelper() {
        return new BinanceAPIHelper(new MockServerClient(mockServer.getHost(), mockServer.getServerPort()));
    }


    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {

    }
}