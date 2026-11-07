package com.hrm.TestCase.TestAPI.AdminAPI;

import static io.restassured.RestAssured.given;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBaseAPI;
import com.hrm.Util.Config;
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

    @Test(description = "OHR72: Admin API - Lọc user theo username")
    public void filterUserByUsernameAPI() {
        String adminUser = Config.get("username");
        Response response = given()
                .auth().oauth2(token)
                .queryParam("username", adminUser)
                .when()
                .get("api/v2/admin/users");

        Log.info("OHR72 Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Không lọc được user theo username");
        List<String> usernames = response.jsonPath().getList("data.userName");
        Assert.assertNotNull(usernames, "Response thiếu danh sách userName");
        Assert.assertTrue(usernames.contains(adminUser), "Kết quả lọc không chứa username: " + adminUser);
    }

    @Test(description = "OHR73: Admin API - Lấy danh sách Job Titles")
    public void getJobTitlesListAPI() {
        Response response = given()
                .auth().oauth2(token)
                .when()
                .get("api/v2/admin/job-titles");

        Log.info("OHR73 Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Không lấy được danh sách Job Titles");
        Assert.assertNotNull(response.jsonPath().getList("data"), "Response thiếu danh sách Job Titles");
    }

    @Test(description = "OHR74: Admin API - Lấy danh sách Locations")
    public void getLocationsListAPI() {
        Response response = given()
                .auth().oauth2(token)
                .when()
                .get("api/v2/admin/locations");

        Log.info("OHR74 Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Không lấy được danh sách Locations");
        Assert.assertNotNull(response.jsonPath().getList("data"), "Response thiếu danh sách Locations");
    }
}
