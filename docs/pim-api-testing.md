# PIM API functional testing

> Cách chạy hiện tại (26/10/2026): `mvn test "-Dmodule=PIMAPI"` qua `testng.xml`; lệnh này chạy toàn bộ module đã chọn, gồm cả issue. Các tên XML/lệnh riêng và số liệu bên dưới là lịch sử trước khi gộp. Xem [hướng dẫn module](test-execution.md).

Phạm vi: đúng bốn testcase PIM `API_01`–`API_04`. Các request đều là GET chỉ đọc, không tạo, sửa hoặc xóa employee.

Class: `com.hrm.TestCase.TestAPI.PIMAPI.PIMAPITest`, kế thừa `TestBaseAPI`. Token được lấy động bằng `OAuthTokenManager` hiện có. Không hard-code token hoặc employee ID; không in token hay mật khẩu ra log.

Tài liệu endpoint đã đối chiếu: [List All Employees](https://api-starter-orangehrm.readme.io/reference/list-all-employees), [Get an Employee](https://api-starter-orangehrm.readme.io/reference/get-an-employee).

## Cách chạy

```powershell
mvn -o -DskipTests test-compile
mvn -o "-Dtestng.suite=testng_pimapi.xml" test
```

`testng_pimapi.xml` chỉ chạy `PIMAPITest`.

## Chuẩn bị dữ liệu và đọc cú pháp

Trước bốn test, `@BeforeClass selectTestEmployee` gửi GET list với `model=default&limit=1`. Nó lấy `empNumber`, `firstName`, `lastName` từ employee thật. Nếu token hoặc dữ liệu không phù hợp, setup phải báo lỗi; không được tính các testcase chưa chạy là PASS.

- `given()` bắt đầu chuẩn bị request.
- `.auth().oauth2(token)` gửi token qua header `Authorization: Bearer ...`.
- `.queryParam("name", employeeName)` thêm bộ lọc vào query URL. Rest Assured tự mã hóa giá trị.
- `.pathParam("empNumber", employeeNumber)` thay `{empNumber}` trong đường dẫn bằng ID nội bộ đang dùng.
- `.when().get(...)` gửi HTTP GET; kết quả được lưu vào biến `Response`.
- `response.statusCode()` đọc HTTP status; `response.asPrettyString()` in body cho dễ đọc.
- `response.jsonPath()` lấy giá trị trong JSON; ví dụ `data.empNumber` đọc ID của employee trong response detail.
- `Assert` so sánh kết quả thật với expected. Assertion sai làm testcase FAIL.

## Kết quả chạy thật

Chạy riêng PIM ngày **14/10/2026**, kết thúc **22:24:42 (UTC+7)**: **4 testcase, 3 PASS, 1 FAIL, 0 ERROR, 0 SKIP**.

- `mvn -o -DskipTests test-compile`: **BUILD SUCCESS**, không có compile error.
- `mvn -o "-Dtestng.suite=testng_pimapi.xml" test`: **BUILD FAILURE** vì `API_04` trả `422` thay vì expected `404`; ba testcase còn lại PASS. Compile thành công và testcase FAIL là hai việc khác nhau.

| Test ID | Request | Status thật | Relevant response | PASS/FAIL |
| --- | --- | --- | --- | --- |
| API_01 | `GET /web/index.php/api/v2/pim/employees?model=default` | `200` | `data`: 3 employees; `meta.total=3`; empNumbers `[6,1,7]` | PASS |
| API_02 | `GET /web/index.php/api/v2/pim/employees/6?model=default` | `200` | `data.empNumber=6`, `data.employeeId="0003"`, `firstName="Nguyễn"`, `middleName="Huy"`, `lastName="Bách"` | PASS |
| API_03 | `GET /web/index.php/api/v2/pim/employees?name=Nguyễn&model=default` | `200` | `data`: 2 employees; `meta.total=2`; empNumbers `[6,7]`; tên khớp input `Nguyễn` | PASS |
| API_04 | `GET /web/index.php/api/v2/pim/employees/8?model=default` | `422` | `error.message="Invalid Parameter"`; `error.data.invalidParamKeys=["empNumber"]`; không có `data` employee | FAIL — expected `404` |

Base URL hiện tại: `http://localhost:8080`; `Nguyễn` là tên thật lấy từ employee có `empNumber=6`. Query được Rest Assured mã hóa, tương đương `name=Nguy%E1%BB%85n`. Các ID `6`, `8` và tên này là kết quả của lần chạy, không hard-code trong test.

**Vì sao API_04 FAIL:** GET list `currentAndPast`, sắp xếp `employee.empNumber DESC`, lấy record đầu cho ID lớn nhất `7`; test dùng `8` làm ID không tồn tại. Source phiên bản local kiểm tra `Rules::IN_ACCESSIBLE_EMP_NUMBERS` trong `EmployeeAPI::getValidationRuleForGetOne`. Validator yêu cầu input là số dương và có trong danh sách employee mà tài khoản được truy cập; ID `8` không có trong danh sách đó. Vì vậy nó bị từ chối ở bước validation và API trả `422 Invalid Parameter`, trước bước có thể trả `404 Record Not Found`.

Đây là khác biệt giữa expected đã lập theo documentation và hành vi phiên bản local. Không phải compile error hoặc token thất bại; chưa đủ căn cứ kết luận application bug. Assertion `404` được giữ nguyên đúng yêu cầu, không đổi sang `422` chỉ để có PASS. Các assertion nằm sau assertion status trong `API_04` không chạy vì TestNG dừng method ở assertion FAIL đầu tiên; việc response không có employee được quan sát trực tiếp từ body.

## API_01 — Get employee list

**Tên method:** `getEmployeeList`.

- **Input:** `model=default`, dùng token đã có từ Base.
- **Endpoint:** `GET /api/v2/pim/employees`.
- **Rest Assured syntax:** `queryParam("model", model)` đặt query; `get(EMPLOYEES_ENDPOINT)` lấy danh sách.
- **Response:** Expected `200`, `data` là mảng employees, `meta.total` là tổng số record phù hợp. Kết quả thật nằm trong bảng phía trên.
- **Assertion:** Status bằng `200`; `data` khác null, không rỗng; record đầu có `empNumber`, `firstName`; `meta.total` không nhỏ hơn số record trong trang trả về.
- **Tại sao PASS/FAIL:** PASS khi API trả danh sách hợp lệ cho token và môi trường có employee. FAIL khi status khác expected hoặc cấu trúc/count không đúng. `401/403` cần kiểm tra token/quyền trước khi kết luận lỗi chức năng.

## API_02 — Get employee detail

**Tên method:** `getEmployeeById`.

- **Input:** `empNumber` lấy từ GET list lúc setup; `model=default`.
- **Endpoint:** `GET /api/v2/pim/employees/{empNumber}`.
- **Rest Assured syntax:** `pathParam("empNumber", employeeNumber)` điền ID vào đường dẫn; `queryParam("model", "default")` chọn model. `empNumber` là ID nội bộ dùng cho đường dẫn; `employeeId` là mã nhân viên khác trong dữ liệu, không dùng thay cho `empNumber`.
- **Response:** Expected `200`, `data` là object của employee được yêu cầu. Kết quả thật nằm trong bảng phía trên.
- **Assertion:** Status bằng `200`; `data` khác null; `data.empNumber`, `data.firstName`, `data.lastName` khớp employee đã lấy từ list.
- **Tại sao PASS/FAIL:** PASS khi detail đúng employee yêu cầu. FAIL nếu trả sai ID/tên, thiếu data hoặc status khác expected. Employee bị sửa hoặc xóa giữa setup và request cũng có thể gây FAIL; phải xem dữ liệu và response.

## API_03 — Filter employee by name

**Tên method:** `searchEmployee`.

- **Input:** `name` bằng `firstName` thật lấy ở setup; `model=default`.
- **Endpoint:** `GET /api/v2/pim/employees`.
- **Rest Assured syntax:** `queryParam("name", employeeName)` gửi tên tìm kiếm; endpoint vẫn là list employees.
- **Response:** Expected `200`, `data` là mảng employees phù hợp bộ lọc tên. Kết quả thật nằm trong bảng phía trên.
- **Assertion:** Status bằng `200`; `data` khác null, không rỗng; kết quả chứa `empNumber` của employee đối chứng; tên ghép `firstName middleName lastName` của từng record chứa input, bỏ qua khác biệt chữ hoa/thường.
- **Tại sao PASS/FAIL:** PASS khi filter tìm thấy employee đối chứng và các record phù hợp tên. FAIL nếu không tìm thấy đối chứng hoặc trả tên không khớp. Phải kiểm tra dữ liệu thay đổi hoặc pagination trước khi kết luận filter sai.

## API_04 — Get nonexistent employee

**Tên method:** `getInvalidEmployee`.

- **Input:** Lấy GET list với `model=default`, `includeEmployees=currentAndPast`, `sortField=employee.empNumber`, `sortOrder=DESC`, `limit=1`. Chọn `missingEmpNumber` bằng ID lớn nhất tìm được cộng một; không dùng `0/-1` rồi đoán status.
- **Endpoint:** Request kiểm tra là `GET /api/v2/pim/employees/{missingEmpNumber}`.
- **Rest Assured syntax:** GET list trong Arrange xác minh ID lớn nhất; `pathParam("empNumber", missingEmpNumber)` đưa ID negative vào request detail.
- **Response:** Expected `404`, `error.message=Record Not Found`, không trả `data` employee. Kết quả thật nằm trong bảng phía trên.
- **Assertion:** List chuẩn bị phải trả `200` và có ID; kiểm tra không vượt giới hạn số nguyên khi cộng một. Detail phải trả `404`, `data` null, đúng thông báo `Record Not Found`.
- **Tại sao PASS/FAIL:** PASS ở negative test nghĩa là server từ chối đúng ID không tồn tại theo expected đã chọn (`404`). Lần chạy thật FAIL vì server từ chối ID bằng `422`, khác expected, dù không trả employee. Việc lấy ID lớn nhất bao gồm employee hiện tại và đã nghỉ trong phạm vi API tài khoản nhìn thấy; nếu có tài khoản bị giới hạn quyền hoặc một employee mới xuất hiện giữa hai request, phải kiểm tra lại tiền điều kiện.

Đây là functional testing: PASS xác nhận hành vi của bốn request đã kiểm tra, không phải kết luận toàn bộ PIM hoặc bảo mật hệ thống đều đúng. Nếu FAIL, giữ assertion và phân tích token, endpoint, dữ liệu cùng response thực tế trước khi sửa code.
