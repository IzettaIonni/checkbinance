package kz.insar.checkbinance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import static org.zalando.logbook.BodyFilter.merge;
//import static org.zalando.logbook.BodyFilters.defaultValue;
//import static org.zalando.logbook.Conditions.exclude;
//import static org.zalando.logbook.Conditions.requestTo;
//import static org.zalando.logbook.json.JsonBodyFilters.replacePrimitiveJsonProperty;
//import org.zalando.logbook.BodyFilter;
//import org.zalando.logbook.DefaultHttpLogWriter;
//import org.zalando.logbook.DefaultSink;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.DefaultHttpLogWriter;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.json.JsonHttpLogFormatter;

@Configuration
public class LogBookConfig {

    @Bean
    public Logbook logbook() {
        return Logbook.builder()
//                .condition(exclude(
//                                requestTo(servletContext.getContextPath() + "/manage/health")
//                                        .or(requestTo(servletContext.getContextPath() + "/manage/prometheus"))
//                        )
//                )
                .sink(new DefaultSink(new JsonHttpLogFormatter(), new DefaultHttpLogWriter()))
//                .bodyFilter(bodyFilter())
                .build();
    }

//    @Bean
//    public BodyFilter bodyFilter() {
//        return merge(
//                defaultValue(),
//                replacePrimitiveJsonProperty(jsonProp ->
//                                jsonProp.equalsIgnoreCase("password") ||
//                                        jsonProp.equalsIgnoreCase("currentPassword"),
//                        (jsonPropName, jsonPropValue) -> "***")
//        );
//    }

}
