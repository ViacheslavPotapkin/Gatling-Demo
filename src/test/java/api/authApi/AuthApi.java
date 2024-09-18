package api.authApi;

import io.gatling.javaapi.core.ChainBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class AuthApi {

    public static ChainBuilder authenticate =
            exec(http("Authenticate")
                    .post("/api/authenticate")
                    .body(StringBody("""
                            {
                                "username": "admin",
                                "password": "admin"
                            }
                            """))
                    .check(status().is(200))
                    .check(jsonPath("$.token").saveAs("jwt"))
            );
}
