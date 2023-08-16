package kz.insar.checkbinance.containers;

import org.mockserver.client.MockServerClient;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

public class MockServerContainerBuilder {
    private static final DockerImageName MOCKSERVER_IMAGE = DockerImageName
            .parse("mockserver/mockserver")
            .withTag("mockserver-" + MockServerClient.class.getPackage().getImplementationVersion());

    public static MockServerContainerBuilder getInstance() {
        return new MockServerContainerBuilder();
    }

    public MockServerContainer startMockBinanceContainer() {
        var container = new MockServerContainer(MOCKSERVER_IMAGE);
        container.start();

        System.setProperty("binance-client.base-url", container.getEndpoint());
        return container;
    }
}
