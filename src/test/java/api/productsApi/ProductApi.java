package api.productsApi;

import api.authApi.AuthApi;
import io.gatling.javaapi.core.ChainBuilder;

import java.util.Random;

import static io.gatling.demo.ApiDemoStoreSimulation.authorization;
import static io.gatling.demo.ApiDemoStoreSimulation.productFeeder;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class ProductApi {

    public static ChainBuilder getProducts = exec(
            http("Get products")
                    .get("/api/product")
                    .check(status().is(200))
                    .check(jmesPath("[*].id").ofList().saveAs("productIds"))
    );

    public static ChainBuilder getProductById =
            exec(session -> {
                var allProductIds = session.getList("productIds");
                return session.set("productId", allProductIds.get(new Random().nextInt(allProductIds.size())));
            })
                    .exec(session -> {
                        System.out.println("All product IDs: " + session.getList("productIds"));
                        System.out.println("Product ID: " + session.get("productId"));
                        return session;
                    })
                    .exec(http("Get product by id")
                            .get("/api/product/#{productId}")
                            .check(status().is(200))
                    );

    public static ChainBuilder updateProduct =
            exec(AuthApi.authenticate)
                    .exec(http("Update product")
                            .put("/api/product/34")
                            .headers(authorization)
                            .body(RawFileBody("data/update_product.json"))
                            .check(status().is(200))
                    );

    public static ChainBuilder createProducts =
            exec(AuthApi.authenticate)
                    .feed(productFeeder)
                    .exec(http("Create product")
                            .post("/api/product")
                            .headers(authorization)
                            .body(ElFileBody("data/create-product.json"))
                    );
}
