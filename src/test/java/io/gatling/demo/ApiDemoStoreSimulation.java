package io.gatling.demo;

import api.authApi.AuthApi;
import api.categoriesApi.CategoryApi;
import api.productsApi.ProductApi;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
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

    private ScenarioBuilder scn = scenario("ApiDemoStoreSimulation")
            .exec(CategoryApi.getCategories,
                    CategoryApi.getCategoryById,
                    ProductApi.getProductById,
                    AuthApi.authenticate,
                    ProductApi.updateProduct,
                    ProductApi.createProducts,
                    CategoryApi.updateCategory
            );

    {
        setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
    }
}
