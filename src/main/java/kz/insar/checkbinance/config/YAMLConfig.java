package kz.insar.checkbinance.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
public class YAMLConfig {

    @Value(value = "${spring.datasource.url}")
    private String datasourceUrl = "asd";
    @Value(value = "${spring.datasource.username}")
    private String datasourceUsername;
    @Value(value = "${spring.datasource.password}")
    private String getDatasourcePassword;

    public String getDatasourceUrl() {
        return datasourceUrl;
    }

    public void setDatasourceUrl(String datasourceUrl) {
        this.datasourceUrl = datasourceUrl;
    }

    public String getDatasourceUsername() {
        return datasourceUsername;
    }

    public void setDatasourceUsername(String datasourceUsername) {
        this.datasourceUsername = datasourceUsername;
    }

    public String getGetDatasourcePassword() {
        return getDatasourcePassword;
    }

    public void setGetDatasourcePassword(String getDatasourcePassword) {
        this.getDatasourcePassword = getDatasourcePassword;
    }
}
