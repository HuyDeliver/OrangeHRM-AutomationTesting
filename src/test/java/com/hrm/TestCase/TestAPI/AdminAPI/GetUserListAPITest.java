package com.hrm.TestCase.TestAPI.AdminAPI;

import static io.restassured.RestAssured.given;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBaseAPI;
import com.hrm.Util.Log;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;

@Epic("API Testing")
@Feature("Admin API")
public class GetUserListAPITest extends TestBaseAPI {
    @Test(description = "OHR63: Admin API - Lấy danh sách user")
    public void getUserListAPI() {
        String endpoint = "api/v2/admin/users";
        Response response = given()
                .auth().oauth2(token)
                .when()
                .get(endpoint);

        Log.info("Bước 3: Status nhận được: " + response.statusCode());
        Log.info(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 200, "Không lấy được danh sách user");

        List<?> list = response.jsonPath().get("data");

        Assert.assertNotNull(list, "Response thiếu danh sách data");

        Assert.assertFalse(list.isEmpty(), "Local đã có user nhưng API trả danh sách rỗng");
    }

}
