package io.gatling.demo;

import api.categoriesApi.CategoryApi;
import api.productsApi.ProductApi;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;
import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class ApiDemoStoreSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://demostore.gatling.io")
            .header("Cache-Control", "no-cache")
            .disableCaching()
            .enableHttp2()
            .shareConnections()
            .contentTypeHeader("application/json")
            .acceptHeader("application/json");

    public static Map<CharSequence, String> authorization = Map.ofEntries(
            Map.entry("authorization", "Bearer #{jwt}")
    );

    public ChainBuilder initSession = exec(session -> session.set("isAuth", false)
    );

    public static FeederBuilder.Batchable<String> categoryFeeder =
            csv("data/categoryFeeder.csv").random();

    public static FeederBuilder.Batchable<String> productFeeder =
            csv("data/productFeeder.csv").circular();

    private ScenarioBuilder admin = scenario("ApiDemoStoreSimulation")
            .exec(initSession,
                    CategoryApi.getCategories,
                    CategoryApi.getCategoryById,
                    ProductApi.getProducts,
                    ProductApi.getProductById,
                    ProductApi.updateProduct,
                    repeat(3).on(ProductApi.createProducts),
                    CategoryApi.updateCategory
            );

    private ScenarioBuilder user = scenario("ApiDemoStore")
            .exec(initSession,
                    CategoryApi.getCategories,
                    CategoryApi.getCategoryById,
                    ProductApi.getProducts,
                    ProductApi.getProductById
            );


    {setUp(
            admin.injectClosed(constantConcurrentUsers(5).during(Duration.ofSeconds(15))),
                user.injectClosed(constantConcurrentUsers(5).during(Duration.ofSeconds(15)))
        ).protocols(httpProtocol);
    }

//    {
//        setUp(scn.injectOpen(
//                atOnceUsers(3),
//                rampUsers(10).during(Duration.ofSeconds(10))))
//                .protocols(httpProtocol)
//                .throttle(reachRps(10).in(Duration.ofSeconds(10)),
//                        holdFor(Duration.ofSeconds(10)),
//                        jumpToRps(5),
//                        holdFor(Duration.ofSeconds(10)));
//    }
}
