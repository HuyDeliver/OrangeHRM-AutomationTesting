package com.hrm.TestCase.TestSecurity;

import static io.restassured.RestAssured.given;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.hrm.Base.TestBaseAPI;
import com.hrm.Util.Config;
import com.hrm.Util.ExelReader;
import com.hrm.Util.Log;
import com.hrm.Util.OAuthTokenManager;
import com.hrm.Util.WebDriverFactory;

import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;

@Epic("Security")
@Feature("AuthorizationAPI")
public class AuthorizationAPITest extends TestBaseAPI {
        private static final String ADMIN_ENDPOINT = "/api/v2/admin/users";
        private static final String CONTACT_ENDPOINT = "/api/v2/pim/employee/{empNumber}/contact-details";
        private static final String EXCEL_PATH = "src/test/resources/data/Security-TestData.xlsx";
        private WebDriver essDriver;
        private String essToken;

        // Gọi GET bằng token bất kỳ, dùng chung cho setup và mọi test
        private Response get(String bearer, String url) {
                Response r = given().auth().oauth2(bearer).redirects().follow(false).when().get(url);
                Log.info("GET " + url + " | Status: " + r.statusCode());
                return r;
        }

        private String contactUrl(String empNumber) {
                return CONTACT_ENDPOINT.replace("{empNumber}", empNumber);
        }

        private void assertJson(Response r, String msg) {
                String type = r.contentType();
                Assert.assertTrue(type != null && type.contains("json"), msg);
        }

        @BeforeClass
        public void verifyAccessControls() {
                // Browser riêng để ESS không dùng chung phiên đăng nhập của Admin
                essDriver = WebDriverFactory.createDriver();
                essToken = OAuthTokenManager.getAccessToken(
                                essDriver, Config.get("ess.username"), Config.get("ess.password"));
                Assert.assertNotEquals(essToken, token, "ESS phải dùng token riêng");

                String ownId = Config.get("ess.empNumber");
                String otherId = Config.get("target.empNumber");
                Assert.assertNotEquals(ownId, otherId, "Phải chọn hai employee khác nhau");

                // 1. ESS đọc được hồ sơ của chính mình -> token ESS đang hoạt động
                Response own = get(essToken, contactUrl(ownId));
                Assert.assertEquals(own.statusCode(), 200, "Setup lỗi: ESS phải xem được hồ sơ của mình");
                Assert.assertNotNull(own.jsonPath().get("data"), "Setup lỗi: hồ sơ ESS phải có dữ liệu");

                // 2. Admin đọc được hồ sơ người đích -> employee đích tồn tại
                Response target = get(token, contactUrl(otherId));
                Assert.assertEquals(target.statusCode(), 200, "Setup lỗi: Admin phải đọc được hồ sơ employee đích");
                Assert.assertNotNull(target.jsonPath().get("data"), "Setup lỗi: employee đích phải có dữ liệu");

                // 3. Admin gọi được API users -> endpoint Admin sống
                Response users = get(token, ADMIN_ENDPOINT);
                Assert.assertEquals(users.statusCode(), 200, "Setup lỗi: Admin phải truy cập được API users");
                assertJson(users, "Setup lỗi: API phải trả JSON");
                List<?> list = users.jsonPath().getList("data");
                Assert.assertTrue(list != null && !list.isEmpty(), "Setup lỗi: cần có user làm đối chứng");
        }

        @DataProvider(name = "authorizationData")
        public Object[][] getAuthorizationData() {
                Object[][] data = ExelReader.getDataFromExel(EXCEL_PATH, "Authorization");
                Assert.assertNotNull(data, "Không đọc được file Excel Security-TestData.xlsx");
                Assert.assertTrue(data.length > 0, "Sheet Authorization phải có test data");
                return data;
        }

        @Test(dataProvider = "authorizationData", description = "OHR54: Authorization / IDOR từ Excel")
        public void userShouldHaveCorrectAccess(String testId, String role, String endpoint, String expectedStatuses) {
                // Hiện AUTHZ_01, AUTHZ_02... làm tên test trong báo cáo Allure
                Allure.getLifecycle().updateTestCase(tc -> tc.setName(testId));

                // Arrange
                String roleName = role.trim().toUpperCase(Locale.ROOT);
                Assert.assertTrue(roleName.equals("ADMIN") || roleName.equals("ESS"),
                                testId + ": role trong Excel phải là ADMIN hoặc ESS, nhận: " + role);
                String bearer = roleName.equals("ADMIN") ? token : essToken;
                // {empNumber} trong Excel được thay bằng employee đích lấy từ config, không
                // hard-code ID vào Excel
                String url = endpoint.trim().replace("{empNumber}", Config.get("target.empNumber"));
                List<String> allowed = Arrays.stream(expectedStatuses.split(","))
                                .map(String::trim).toList();
                Log.info(testId + " | Authorization | " + roleName + " | " + url + " | expected=" + allowed);

                // Act
                Response response = get(bearer, url);

                // Assert 1: status nằm trong danh sách Excel cho phép, và trả JSON
                int status = response.statusCode();
                Assert.assertTrue(allowed.contains(String.valueOf(status)),
                                testId + ": status " + status + " ngoài expected " + allowed);
                assertJson(response, testId + ": API phải trả JSON, không phải trang login/error HTML");

                Object data = response.jsonPath().get("data");
                String error = response.jsonPath().getString("error.message");
                Log.info(testId + " | hasData=" + (data != null) + ", error=" + error);

                if (roleName.equals("ADMIN")) {
                        // Admin là đối chứng: phải thành công và có dữ liệu
                        Assert.assertEquals(status, 200, testId + ": Admin phải truy cập được API");
                        List<?> users = response.jsonPath().getList("data");
                        Assert.assertTrue(users != null && !users.isEmpty(),
                                        testId + ": Admin phải nhận được danh sách");
                        Log.info(testId + " | users count=" + users.size());
                } else {
                        // ESS bị từ chối: quan trọng nhất là KHÔNG được nhận dữ liệu bảo vệ
                        Assert.assertNull(data, testId + ": lỗ hổng phân quyền, ESS nhận được dữ liệu không có quyền");
                        // 422 chỉ chấp nhận khi là lỗi validation do empNumber (API từ chối ID không
                        // thuộc mình)
                        if (status == 422) {
                                Assert.assertEquals(error, "Invalid Parameter",
                                                testId + ": 422 phải là lỗi validation của API");
                                List<String> invalid = response.jsonPath().getList("error.data.invalidParamKeys");
                                Log.info(testId + " | invalid params=" + invalid);
                                Assert.assertTrue(invalid != null && invalid.contains("empNumber"),
                                                testId + ": lỗi 422 phải liên quan đến empNumber");
                        }
                }
        }

        @AfterClass(alwaysRun = true)
        public void closeBrowsers() {
                try {
                        if (essDriver != null)
                                essDriver.quit();
                } finally {
                        if (driver != null)
                                driver.quit();
                }
        }
}
