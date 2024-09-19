package api.categoriesApi;

import api.authApi.AuthApi;
import io.gatling.javaapi.core.ChainBuilder;

import static io.gatling.demo.ApiDemoStoreSimulation.authorization;
import static io.gatling.demo.ApiDemoStoreSimulation.categoryFeeder;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class CategoryApi {

    public static ChainBuilder getCategories = exec(
            http("Get category")
                    .get("/api/category")
                    .check(status().is(200))
                    .check(jsonPath("$[0].name").is("For Him"))
    );

    public static ChainBuilder getCategoryById =
            exec(http("Get category by id")
                            .get("/api/product?category=7")
                            .check(status().is(200))
                    );

    public static ChainBuilder updateCategory =
            exec(AuthApi.authenticate)
                    .feed(categoryFeeder)
                    .exec(http("Update category")
                            .put("/api/category/#{categoryId}")
                            .headers(authorization)
                            .body(StringBody("""
                                    {
                                        "name": "#{categoryName}"
                                    }
                                    """))
                            .check(jsonPath("$.name").isEL("#{categoryName}"))
                    );
}
