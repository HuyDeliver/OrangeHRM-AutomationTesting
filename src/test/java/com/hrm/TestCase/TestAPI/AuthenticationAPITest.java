package com.hrm.TestCase.TestAPI;

import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBaseAPI;

import io.restassured.response.Response;

public class AuthenticationAPITest extends TestBaseAPI {

    private static final int EMPLOYEE_ID = 003;

    private static final String EMPLOYEE_ENDPOINT =
            "/api/v2/pim/employees/{empNumber}";


    // =========================================================
    // SEC_AUTH_01
    // Valid Bearer Token
    // =========================================================
    @Test
    public void validTokenShouldBeAccepted() {

        Response response =
                given()
                    .auth()
                    .oauth2(TOKEN)
                    .pathParam("empNumber", EMPLOYEE_ID)
                .when()
                    .get(EMPLOYEE_ENDPOINT);

        System.out.println("\n===== VALID TOKEN =====");
        System.out.println("Status: " + response.statusCode());
        System.out.println(response.asPrettyString());

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Valid Bearer Token không truy cập được API"
        );
    }


    // =========================================================
    // SEC_AUTH_02
    // Không gửi Bearer Token
    // =========================================================
    @Test
    public void requestWithoutTokenShouldBeRejected() {

        Response response =
                given()
                    .pathParam("empNumber", EMPLOYEE_ID)
                .when()
                    .get(EMPLOYEE_ENDPOINT);

        System.out.println("\n===== NO TOKEN =====");
        System.out.println("Status: " + response.statusCode());
        System.out.println(response.asPrettyString());

        Assert.assertEquals(
                response.statusCode(),
                401,
                "Security issue: API cho phép truy cập mà không cần authentication"
        );
    }


    // =========================================================
    // SEC_AUTH_03
    // Gửi Bearer Token không hợp lệ
    // =========================================================
   @Test
    public void invalidTokenShouldBeRejected() {

        Response response =
                given()
                    .redirects()
                    .follow(false)
                    .auth()
                    .oauth2("fake-token")
                    .pathParam("empNumber", EMPLOYEE_ID)
                .when()
                    .get(EMPLOYEE_ENDPOINT);

        System.out.println("\n===== INVALID TOKEN =====");
        System.out.println("Status: " + response.statusCode());
        System.out.println("Location: " + response.getHeader("Location"));

        Assert.assertEquals(
                response.statusCode(),
                302,
                "Invalid Bearer Token phải bị từ chối"
        );

        Assert.assertTrue(
                response.getHeader("Location").contains("/auth/login"),
                "Invalid token không được redirect về login"
        );
    }
}