# Kiểm thử phân quyền OrangeHRM

Bộ kiểm thử gồm ba ca GET chỉ đọc trong môi trường OrangeHRM local. Tài liệu mô tả thiết kế, cách đánh giá và kết quả thực thi ngày 10/10/2026.

## Điều kiện và cách chạy

- Admin dùng `username`, `password` hiện có. ESS dùng `ess.username`, `ess.password`; nhân viên của ESS dùng `ess.empNumber`, đối tượng cần kiểm tra dùng `target.empNumber`. Không ghi mật khẩu, authorization code, access/refresh token vào tài liệu hay báo cáo. Không đưa thông tin xác thực thật vào Git.
- Dữ liệu đã đối chiếu: TriQuan là ESS, `empNumber=7`, không có cấp dưới; HuyBach là ESS, `empNumber=6`, là supervisor của nhân viên 7. TriQuan không được đọc ContactDetails của cấp trên 6. Chiều ngược lại, supervisor 6 có thể có quyền đọc cấp dưới 7 qua vai trò manager; không dùng chiều 6 → 7 làm ca âm tính.
- `ess.empNumber` và `target.empNumber` là ID nội bộ `emp_number`, không phải mã nhân viên hiển thị `employee_id` hay `TestDataShare.IDEMPLOYEE`.
- Lấy token bằng luồng Authorization Code + PKCE trong `OAuthTokenManager` hiện có. Khi username thay đổi, `currentUsername` làm ba giá trị `accessToken`, `refreshToken`, `expiresAt` được reset để tránh dùng token của người trước. Không tạo cơ chế cache mới. Đăng nhập ESS bằng trình duyệt riêng để không dùng phiên browser của Admin.
- Base hiện dùng URL local và API v2 của dự án. Lớp `com.hrm.TestCase.TestAPI.AuthorizationAPITest` kế thừa `TestBaseAPI`; không thay đổi base hoặc test xác thực chỉ để chạy ba ca này.
- Chạy tuần tự bằng `testng_authorization.xml`, có `TestListener` và không có `RetryListener`. AUTHZ_02 phụ thuộc đối chứng AUTHZ_01; nếu đối chứng thất bại thì AUTHZ_02 được SKIP, không phải PASS.

```powershell
mvn -DskipTests test-compile
mvn -o "-Dtestng.suite=testng_authorization.xml" test
```

## AUTHZ_01

| Mục | Nội dung |
| --- | --- |
| 1. Test ID | AUTHZ_01 |
| 2. Tên | Admin truy cập được API danh sách người dùng (`adminShouldAccessAdminApi`). |
| 3. Category | Đối chứng phân quyền theo vai trò. |
| 4. Mục đích | Chứng minh endpoint hoạt động và token Admin hợp lệ trước khi đánh giá ca truy cập trái phép. |
| 5. Role | Admin đã xác thực bằng OAuth. |
| 6. Request | `GET http://localhost:8080/web/index.php/api/v2/admin/users`, gửi bearer token Admin và nhận JSON. |
| 7. Tại sao đổi ID/role? | Không đổi; giữ Admin làm đối chứng. AUTHZ_02 giữ endpoint và đổi người gọi sang ESS. |
| 8. Expected | HTTP `200`, JSON có `data` là danh sách người dùng không rỗng. |
| 9. Assertion | Kiểm tra status `200`, Content-Type JSON và `data` là list không rỗng; không kiểm tra schema từng bản ghi. |
| 10. PASS | Admin nhận danh sách hợp lệ, không phải HTML trang đăng nhập hoặc JSON lỗi. |
| 11. FAIL | Không đạt đối chứng: đăng nhập/token/endpoint/môi trường hoặc response không đúng hợp đồng. Chưa được dùng kết quả đó để kết luận các ca âm tính an toàn. |
| 12. Có chắc vulnerability? | Không. Đây là hành vi được phép; FAIL của đối chứng cũng không chứng minh lỗ hổng phân quyền. |

## AUTHZ_02

| Mục | Nội dung |
| --- | --- |
| 1. Test ID | AUTHZ_02 |
| 2. Tên | ESS không truy cập được API quản trị người dùng (`essUserShouldNotAccessAdminApi`). |
| 3. Category | Vượt quyền theo vai trò: từ ESS lên chức năng Admin. |
| 4. Mục đích | Kiểm tra server kiểm soát quyền của endpoint, kể cả khi người dùng gọi thẳng API ngoài giao diện. |
| 5. Role | ESS hợp lệ, TriQuan/nhân viên 7; token và phiên đăng nhập độc lập với Admin. |
| 6. Request | Giữ `GET http://localhost:8080/web/index.php/api/v2/admin/users` như AUTHZ_01; thay bearer token Admin bằng token ESS. |
| 7. Tại sao đổi ID/role? | Chỉ đổi vai trò người gọi. Nếu vẫn đọc được dữ liệu quản trị, quyền của endpoint chưa ngăn đúng tài khoản ESS. |
| 8. Expected | HTTP `403`, JSON lỗi có `error.message=Unauthorized`, không có `data` danh sách người dùng. |
| 9. Assertion | Sau đối chứng AUTHZ_01 và kiểm tra phiên ESS hợp lệ, yêu cầu chính xác status `403`, JSON và không có `data` được bảo vệ. Không chấp nhận một mã `4xx` bất kỳ hoặc token hết hạn làm bằng chứng đạt. |
| 10. PASS | ESS đang xác thực hợp lệ nhận đúng phản hồi từ chối `403` và không đọc được dữ liệu quản trị. |
| 11. FAIL | ESS nhận dữ liệu quản trị hoặc response khác hợp đồng mong đợi. Lỗi setup, `404`, `5xx`, HTML hoặc token không hợp lệ phải được phân loại riêng, không tự gọi là vượt quyền. |
| 12. Có chắc vulnerability? | Chỉ khi xác nhận đúng tài khoản ESS, token hợp lệ, đúng endpoint và response thực tế cấp dữ liệu/quyền quản trị. Một assertion FAIL đơn lẻ chưa đủ. |

## AUTHZ_03

| Mục | Nội dung |
| --- | --- |
| 1. Test ID | AUTHZ_03 |
| 2. Tên | Nhân viên không đọc được thông tin riêng của nhân viên khác (`employeeShouldNotAccessAnotherEmployeesPrivateData`). |
| 3. Category | Phân quyền đối tượng / IDOR: đổi ID tài nguyên trong request. |
| 4. Mục đích | Kiểm tra server đối chiếu `empNumber` được yêu cầu với quyền của người đang gọi API. |
| 5. Role | ESS TriQuan, nhân viên 7, không có cấp dưới. Đối tượng 6 là HuyBach, tài khoản ESS và supervisor của nhân viên 7. |
| 6. Request | Đối chứng ESS: `GET /web/index.php/api/v2/pim/employee/7/contact-details`. Đối chứng target: Admin gọi `GET /web/index.php/api/v2/pim/employee/6/contact-details`. Sau đó ESS giữ token của mình và đổi request từ ID 7 sang ID 6. Đường dẫn dùng `employee` số ít. |
| 7. Tại sao đổi ID/role? | Chỉ đổi ID từ chính mình (7) sang người khác (6), không đổi token hay role. Đối tượng khác đã tồn tại và không thuộc phạm vi ESS được phép đọc. |
| 8. Expected | ESS đọc ID 7 và Admin đọc ID 6 đều trả `200` với JSON ContactDetails. ESS đọc ID 6 bị từ chối: `403`, hoặc `422` InvalidParameter với `error.data.invalidParamKeys` chứa `empNumber`; không có `data` ContactDetails riêng tư. |
| 9. Assertion | Hai đối chứng phải thành công và own ID khác target ID. Response âm tính phải là JSON không có `data` được bảo vệ; chỉ chấp nhận `403`, hoặc `422` khi `invalidParamKeys` chứa đúng `empNumber`. Không chấp nhận `422` do lỗi tham số khác. |
| 10. PASS | ESS đọc được ContactDetails của mình nhưng không đọc được ContactDetails của nhân viên 6. |
| 11. FAIL | Request đổi ID trả dữ liệu ContactDetails của người khác hoặc response không đúng hợp đồng. Nếu đối chứng ESS own-profile hoặc Admin target thất bại, target sai/không tồn tại hay quyền thực tế khác tiền điều kiện thì cần sửa setup trước khi đánh giá IDOR. |
| 12. Có chắc vulnerability? | Chỉ kết luận IDOR khi chứng minh cùng token ESS hợp lệ đọc được tài nguyên riêng của nhân viên 6 dù không có quyền. `200` HTML, lỗi server hoặc đối chứng hỏng không phải chứng cứ đó. |

## Bằng chứng và giới hạn

Source local có hai lớp từ chối: `ApiAuthorizationSubscriber` trả `403` khi thiếu quyền API; `InAccessibleEmpNumbers` kiểm tra phạm vi nhân viên và có thể tạo lỗi `InvalidParamException`, được chuyển thành `422` với `invalidParamKeys=['empNumber']`. Vì vậy AUTHZ_03 kiểm tra cả mã HTTP và nội dung lỗi, thay vì mặc định mọi IDOR phải trả `403` hoặc nới assertion để PASS.

Ghi Test ID, method/path, role, own/target ID, status HTTP, loại nội dung, kết quả assertion và đối chứng liên quan. Chỉ lưu bản tóm tắt đã loại dữ liệu nhạy cảm; không log token, cookie, authorization code hoặc body chứa thông tin liên hệ thật. Mỗi PASS chỉ xác nhận tình huống đã kiểm tra trên bản triển khai đó.

## Kết quả thực thi ngày 10/10/2026

Compilation: **SUCCESS**. Suite thực tế: **3 PASS, 0 FAIL, 0 SKIP**, chạy bằng:

```powershell
mvn -o "-Dtestng.suite=testng_authorization.xml" test
```

Allure đã tạo ba kết quả `passed` trong `target/allure-results` theo cấu hình hiện có.

| Ca / đối chứng | Kết quả thực tế |
| --- | --- |
| AUTHZ_01: Admin → users | `200`, PASS. |
| AUTHZ_02: ESS → users Admin | `403 Unauthorized`, không có `data`, PASS. |
| AUTHZ_03: ESS 7 → ContactDetails 6 | `422 InvalidParameter`, `invalidParamKeys` chứa `empNumber`, không có `data`, PASS. |
| ESS 7 → ContactDetails 7 | `200`, đối chứng thành công. |
| Admin → ContactDetails 6 | `200`, đối chứng target thành công. |

Lần chạy này chưa phát hiện vượt quyền trong ba tình huống trên; không khẳng định toàn bộ ứng dụng không có lỗ hổng.

**AUTHZ_04 chưa triển khai.** Thao tác PUT ContactDetails ghi toàn bộ 11 trường; cần xác minh việc lưu và khôi phục dữ liệu trước khi thêm ca ghi trái phép. Ba ca trên không sửa ContactDetails hay bản ghi người dùng/nhân viên.
