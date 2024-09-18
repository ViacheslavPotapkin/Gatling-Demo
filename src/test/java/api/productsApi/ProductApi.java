package api.productsApi;

import io.gatling.javaapi.core.ChainBuilder;

import static io.gatling.demo.ApiDemoStoreSimulation.authorization;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class ProductApi {

    public static ChainBuilder getProductById = exec(
            http("Get product by id")
                    .get("/api/product/34")
                    .check(status().is(200))
    );

    public static ChainBuilder updateProduct = exec(
            http("Update product")
                    .put("/api/product/34")
                    .headers(authorization)
                    .body(RawFileBody("data/update_product.json"))
                    .check(status().is(200))
    );

    public static ChainBuilder createProducts =
            repeat(3, "countProduct").on(
                    exec(http("Create product")
                            .post("/api/product")
                            .headers(authorization)
                            .body(RawFileBody("create-product#{countProduct}.json")))
            );
}
