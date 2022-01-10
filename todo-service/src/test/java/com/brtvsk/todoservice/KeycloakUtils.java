package com.brtvsk.todoservice;

import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;

import java.util.Map;

final class KeycloakUtils {
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "admin_password";
    public static final String TEST_USER_USERNAME = "user";
    public static final String TEST_USER_PASSWORD = "password";
    public static final String REALM = "micro2do-realm";
    public static final String CLIENT = "auth-client";

    public static String getAdminAccessToken(String authServerUrl, String username, String password) {
        String tokenUrl = authServerUrl + "/realms/master/protocol/openid-connect/token";
        return RestAssured.given().contentType("application/x-www-form-urlencoded")
                .formParams(Map.of(
                        "username", username,
                        "password", password,
                        "grant_type", "password",
                        "client_id", "admin-cli"
                ))
                .post(tokenUrl)
                .then().assertThat().statusCode(HttpStatus.OK.value())
                .extract().path("access_token");
    }

    public static String getAccessToken(String authServerUrl, String username, String password) {
        String tokenUrl = authServerUrl +
                "/realms/" +
                REALM +
                "/protocol/openid-connect/token";
        return RestAssured.given().contentType("application/x-www-form-urlencoded")
                .formParams(Map.of(
                        "username", username,
                        "password", password,
                        "grant_type", "password",

                        "client_id", CLIENT
                ))
                .post(tokenUrl)
                .then().assertThat().statusCode(HttpStatus.OK.value())
                .extract().path("access_token");
    }

}
