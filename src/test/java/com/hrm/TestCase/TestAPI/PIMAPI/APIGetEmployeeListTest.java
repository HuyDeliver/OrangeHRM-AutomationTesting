package com.hrm.TestCase.TestAPI.PIMAPI;

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
@Feature("PIM API")
public class APIGetEmployeeListTest extends TestBaseAPI {
    @Test(description = "OHR75: PIM API - Phân trang danh sách employee với limit và offset")
    public void getPaginatedEmployeeListAPI() {
        Response response = given()
                .auth().oauth2(token)
                .queryParam("limit", 5)
                .queryParam("offset", 0)
                .when()
                .get("/api/v2/pim/employees");

        Log.info("OHR75 Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Không lấy được danh sách employee phân trang");
        List<?> data = response.jsonPath().getList("data");
        Assert.assertNotNull(data, "Response thiếu danh sách data");
        Assert.assertTrue(data.size() <= 5, "Số lượng bản ghi trả về vượt quá limit=5");
    }
}
