package kz.insar.checkbinance.containers;

import org.testcontainers.containers.PostgreSQLContainer;

public class ContainerHolder {

    private static final PostgreSQLContainer<?> postgreSql = PostgreSQLContainerBuilder.getInstance()
            .startCommonContainer();

    public static final PostgreSQLContainer<?> getPostgreSQL() {
        return postgreSql;
    }

}