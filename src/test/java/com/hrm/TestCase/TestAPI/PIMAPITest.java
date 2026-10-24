package com.hrm.TestCase.TestAPI;

import static io.restassured.RestAssured.given;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.hrm.Base.TestBaseAPI;
import com.hrm.Util.Log;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;

@Epic("API Testing")
@Feature("PIM API")
public class PIMAPITest extends TestBaseAPI {
        private static final String EMPLOYEES_ENDPOINT = "/api/v2/pim/employees";
        private int empNumber;
        private String firstName;
        private String lastName;

        @BeforeClass
        public void selectTestEmployee() {
                Response response = given().auth().oauth2(token)
                                .queryParam("model", "default").queryParam("limit", 1)
                                .when().get(EMPLOYEES_ENDPOINT);
                Assert.assertEquals(response.statusCode(), 200, "Setup: không lấy được employee list");
                List<?> employees = response.jsonPath().getList("data");
                Assert.assertNotNull(employees, "Setup: response thiếu data");
                Assert.assertFalse(employees.isEmpty(), "Setup: cần ít nhất một employee đang tồn tại");
                empNumber = response.jsonPath().getInt("data[0].empNumber");
                firstName = response.jsonPath().getString("data[0].firstName");
                lastName = response.jsonPath().getString("data[0].lastName");
                Assert.assertNotNull(firstName, "Setup: employee thiếu firstName");
                Assert.assertFalse(firstName.isBlank(), "Setup: firstName phải có giá trị để test filter");
                Log.info("Test employee: empNumber=" + empNumber + ", firstName=" + firstName);
        }

        @Test(description = "OHR56: API_01 - Get employee list")
        public void getEmployeeList() {
                // Arrange
                String model = "default";

                // Act
                Response response = given()
                                .auth().oauth2(token)
                                .queryParam("model", model)
                                .when().get(EMPLOYEES_ENDPOINT);

                // Assert
                Log.info("API_01 Status: " + response.statusCode());
                Log.info(response.asPrettyString());
                Assert.assertEquals(response.statusCode(), 200, "Không lấy được employee list");
                List<?> employees = response.jsonPath().getList("data");
                Assert.assertNotNull(employees, "Employee list phải có data");
                Assert.assertFalse(employees.isEmpty(), "Danh sách phải có employee đối chứng đã lấy ở setup");
                Assert.assertNotNull(response.jsonPath().get("data[0].empNumber"), "Employee phải có empNumber");
                Assert.assertNotNull(response.jsonPath().get("data[0].firstName"), "Employee phải có firstName");
                Assert.assertTrue(response.jsonPath().getInt("meta.total") >= employees.size(),
                                "meta.total không hợp lệ");
        }

        @Test(description = "OHR57: API_02 - Get employee detail")
        public void getEmployeeById() {
                // Arrange
                int employeeNumber = empNumber;

                // Act
                Response response = given()
                                .auth().oauth2(token)
                                .pathParam("empNumber", employeeNumber).queryParam("model", "default")
                                .when().get(EMPLOYEES_ENDPOINT + "/{empNumber}");

                // Assert
                Log.info("API_02 Request empNumber: " + employeeNumber + ", Status: " + response.statusCode());
                Log.info(response.asPrettyString());
                Assert.assertEquals(response.statusCode(), 200, "Không lấy được employee detail");
                Assert.assertNotNull(response.jsonPath().get("data"), "Employee detail phải có data");
                Assert.assertEquals(response.jsonPath().getInt("data.empNumber"), employeeNumber, "Trả sai empNumber");
                Assert.assertEquals(response.jsonPath().getString("data.firstName"), firstName, "Trả sai firstName");
                Assert.assertEquals(response.jsonPath().getString("data.lastName"), lastName, "Trả sai lastName");
        }

        @Test(description = "OHR58: API_03 - Filter employee by name")
        public void searchEmployee() {
                // Arrange
                String employeeName = firstName;

                // Act
                Response response = given()
                                .auth().oauth2(token)
                                .queryParam("name", employeeName).queryParam("model", "default")
                                .when().get(EMPLOYEES_ENDPOINT);

                // Assert
                Log.info("API_03 Request name: " + employeeName + ", Status: " + response.statusCode());
                Log.info(response.asPrettyString());
                Assert.assertEquals(response.statusCode(), 200, "Không tìm được employee theo name");
                List<Map<String, Object>> employees = response.jsonPath().getList("data");
                Assert.assertNotNull(employees, "Search response phải có data");
                Assert.assertFalse(employees.isEmpty(), "Search phải tìm thấy employee đối chứng");
                Assert.assertTrue(response.jsonPath().getList("data.empNumber", Integer.class).contains(empNumber),
                                "Search không trả employee đã chọn");
                for (Map<String, Object> employee : employees) {
                        String fullName = employee.get("firstName") + " " + employee.get("middleName") + " "
                                        + employee.get("lastName");
                        Assert.assertTrue(
                                        fullName.toLowerCase(Locale.ROOT)
                                                        .contains(employeeName.toLowerCase(Locale.ROOT)),
                                        "Search trả employee có tên không khớp input");
                }
        }

        @Test(description = "OHR59: API_04 - Get nonexistent employee")
        public void getInvalidEmployee() {
                // Arrange: lấy ID lớn nhất, bao gồm cả employee đã nghỉ để tránh chọn nhầm ID
                // đang tồn tại.
                Response employeeList = given().auth().oauth2(token)
                                .queryParam("model", "default").queryParam("includeEmployees", "currentAndPast")
                                .queryParam("sortField", "employee.empNumber").queryParam("sortOrder", "DESC")
                                .queryParam("limit", 1)
                                .when().get(EMPLOYEES_ENDPOINT);
                Assert.assertEquals(employeeList.statusCode(), 200,
                                "Không xác minh được ID lớn nhất cho negative test");
                Assert.assertNotNull(employeeList.jsonPath().get("data[0].empNumber"),
                                "Negative setup thiếu employee ID");
                int largestEmpNumber = employeeList.jsonPath().getInt("data[0].empNumber");
                Assert.assertTrue(largestEmpNumber < Integer.MAX_VALUE, "Không tạo được ID negative hợp lệ");
                int missingEmpNumber = largestEmpNumber + 1;

                // Act
                Response response = given()
                                .auth().oauth2(token)
                                .pathParam("empNumber", missingEmpNumber).queryParam("model", "default")
                                .when().get(EMPLOYEES_ENDPOINT + "/{empNumber}");

                // Assert
                Log.info("API_04 Largest ID: " + largestEmpNumber + ", Request empNumber: " + missingEmpNumber
                                + ", Status: " + response.statusCode());
                Log.info(response.asPrettyString());
                Assert.assertEquals(response.statusCode(), 404, "ID không tồn tại phải trả 404 theo documentation");
                Assert.assertNull(response.jsonPath().get("data"), "Không được trả employee cho ID không tồn tại");
                Assert.assertEquals(response.jsonPath().getString("error.message"), "Record Not Found",
                                "Sai lỗi không tìm thấy");
        }
}
