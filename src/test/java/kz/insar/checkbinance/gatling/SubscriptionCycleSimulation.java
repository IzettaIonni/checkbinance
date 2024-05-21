package kz.insar.checkbinance.gatling;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.header;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.OpenInjectionStep.RampRate.RampRateOpenInjectionStep;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubscriptionCycleSimulation extends Simulation {

    private static final HttpProtocolBuilder HTTP_PROTOCOL_BUILDER = setupProtocolForSimulation();

    private static final Iterator<Map<String, Object>> FEED_DATA = setupTestFeedData();

    private static final ScenarioBuilder POST_SCENARIO_BUILDER = buildPostScenario();

    public SubscriptionCycleSimulation() {
        setUp(POST_SCENARIO_BUILDER.injectOpen(postEndpointInjectionProfile())
                .protocols(HTTP_PROTOCOL_BUILDER)).assertions(global().responseTime()
                .max()
                .lte(10000), global().successfulRequests()
                .percent()
                .gt(90d));
    }

    private RampRateOpenInjectionStep postEndpointInjectionProfile() {
        int totalDesiredUserCount = 1;
        double userRampUpPerInterval = 1;
        double rampUpIntervalSeconds = 5;

        int totalRampUptimeSeconds = 1;
        int steadyStateDurationSeconds = 1;
        return rampUsersPerSec(userRampUpPerInterval / (rampUpIntervalSeconds / 60)).to(totalDesiredUserCount)
                .during(Duration.ofSeconds(totalRampUptimeSeconds + steadyStateDurationSeconds));
    }

    private static Iterator<Map<String, Object>> setupTestFeedData() {
        Iterator<Map<String, Object>> iterator;
        iterator = Stream.generate(() -> {
                    Map<String, Object> stringObjectMap = new HashMap<>();
                    stringObjectMap.put("id", ThreadLocalRandom.current().nextInt(1, 2617));
                    return stringObjectMap;
                })
                .iterator();
        return iterator;
    }

    private static HttpProtocolBuilder setupProtocolForSimulation() {
        return HttpDsl.http.baseUrl("http://localhost:8080")
                .acceptHeader("application/json")
                .maxConnectionsPerHost(10);
    }

    private static ScenarioBuilder buildPostScenario() {
        return CoreDsl.scenario("Load Test Requesting Subscriptions")
                .feed(FEED_DATA)
                .exec(http("add-subscription").post("/ticker/subscribeticker")
                        .body(StringBody("{ \"id\" : ${id} }"))
                        .check(status().is(200)))
                .exec(http("check-subscription").get("/ticker/subscriptions")
                        .check(
                                status().is((200)),
                                substring("${id}"))
                )
                .exec(http(("delete-subscription")).post("/ticker/unsubscribeticker")
                        .check(status().is(200)));
    }

}

