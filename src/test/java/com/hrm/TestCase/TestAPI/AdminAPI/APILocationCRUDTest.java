package com.hrm.TestCase.TestAPI.AdminAPI;

import static io.restassured.RestAssured.given;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.hrm.Base.TestBaseAPI;
import com.hrm.Util.Log;

import groovy.json.JsonOutput;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Epic("API Testing")
@Feature("Admin API - Location CRUD")
public class APILocationCRUDTest extends TestBaseAPI {

    private static final String ENDPOINT = "/api/v2/admin/locations";
    private Integer locationId;
    private String locationName;

    @Test(priority = 1, description = "OHR83: Admin API - Tạo mới Location (POST)")
    public void createLocationAPI() {
        locationName = "API_Branch_" + UUID.randomUUID().toString().substring(0, 6);
        String body = JsonOutput.toJson(Map.of(
                "name", locationName,
                "countryCode", "VN",
                "province", "Ha Noi",
                "city", "Ha Noi",
                "address", "100 Yen Lang",
                "zipCode", "10000",
                "phone", "0917196589",
                "fax", "554466",
                "note", "Chi nhanh tao tu API Test"));

        Log.info("OHR83 - POST " + ENDPOINT + " | Body: " + body);
        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().post(ENDPOINT);

        Log.info("Status: " + response.statusCode());
        Log.info(response.asPrettyString());

        Assert.assertEquals(response.statusCode(), 200, "Tạo Location phải trả về 200");
        locationId = response.jsonPath().get("data.id");
        Assert.assertNotNull(locationId, "Response phải có id của Location");
        Assert.assertEquals(response.jsonPath().getString("data.name"), locationName, "Trả về sai tên Location");
    }

    @Test(priority = 2, dependsOnMethods = "createLocationAPI", description = "OHR84: Admin API - Xem chi tiết Location (GET)")
    public void getLocationDetailAPI() {
        Log.info("OHR84 - GET " + ENDPOINT + "/" + locationId);
        Response response = given()
                .auth().oauth2(token)
                .pathParam("id", locationId)
                .when().get(ENDPOINT + "/{id}");

        Log.info("Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Lấy chi tiết Location phải trả về 200");
        Assert.assertEquals(response.jsonPath().getInt("data.id"), locationId.intValue(), "Trả về sai ID Location");
        Assert.assertEquals(response.jsonPath().getString("data.name"), locationName, "Trả về sai tên Location");
    }

    @Test(priority = 3, dependsOnMethods = "createLocationAPI", description = "OHR85: Admin API - Cập nhật thông tin Location (PUT)")
    public void updateLocationAPI() {
        String updatedName = locationName + "_HQ";
        String body = JsonOutput.toJson(Map.of(
                "name", updatedName,
                "countryCode", "VN",
                "province", "Da Nang",
                "city", "Da Nang",
                "address", "200 Nguyen Van Linh",
                "zipCode", "55000",
                "phone", "0988776655",
                "fax", "112233",
                "note", "Da cap nhat qua API"));

        Log.info("OHR85 - PUT " + ENDPOINT + "/" + locationId);
        Response response = given()
                .auth().oauth2(token)
                .pathParam("id", locationId)
                .contentType(ContentType.JSON).body(body)
                .when().put(ENDPOINT + "/{id}");

        Log.info("Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Cập nhật Location phải trả về 200");
        Assert.assertEquals(response.jsonPath().getString("data.name"), updatedName, "Tên Location chưa được cập nhật");
        Assert.assertEquals(response.jsonPath().getString("data.city"), "Da Nang", "City chưa được cập nhật");
        locationName = updatedName;
    }

    @Test(priority = 4, dependsOnMethods = "updateLocationAPI", description = "OHR86: Admin API - Tìm kiếm Location theo tên (GET Filter)")
    public void filterLocationByNameAPI() {
        Response response = given()
                .auth().oauth2(token)
                .queryParam("name", locationName)
                .when().get(ENDPOINT);

        Log.info("OHR86 - Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Lọc Location theo tên phải trả về 200");
        List<String> names = response.jsonPath().getList("data.name");
        Assert.assertNotNull(names, "Response thiếu danh sách data");
        Assert.assertTrue(names.contains(locationName), "Không tìm thấy Location vừa tạo trong danh sách lọc");
    }

    @Test(priority = 5, description = "OHR87: Admin API - Từ chối tạo Location thiếu countryCode (422)")
    public void createLocationMissingCountryShouldFail() {
        String body = JsonOutput.toJson(Map.of("name", "Invalid_Location_No_Country"));

        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().post(ENDPOINT);

        Log.info("OHR87 - Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 422, "Thiếu countryCode bắt buộc phải trả về 422");
    }

    @Test(priority = 6, dependsOnMethods = "createLocationAPI", description = "OHR88: Admin API - Xóa Location (DELETE)")
    public void deleteLocationAPI() {
        String body = JsonOutput.toJson(Map.of("ids", List.of(locationId)));

        Log.info("OHR88 - DELETE " + ENDPOINT + " | ids=" + locationId);
        Response response = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON).body(body)
                .when().delete(ENDPOINT);

        Log.info("Status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 200, "Xóa Location phải trả về 200");
        locationId = null;
    }

    @AfterClass(alwaysRun = true)
    public void cleanupLocation() {
        if (locationId != null) {
            given().auth().oauth2(token)
                    .contentType(ContentType.JSON)
                    .body(JsonOutput.toJson(Map.of("ids", List.of(locationId))))
                    .when().delete(ENDPOINT);
        }
    }
}
