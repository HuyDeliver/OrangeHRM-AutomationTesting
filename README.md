# OrangeHRM Automation Testing

Đồ án kiểm thử OrangeHRM OS 5.9 local bằng Java 21, Maven, Selenium, TestNG, Rest Assured, Allure và Log4j2.
Hiện có **66 method @Test**. Các method dùng DataProvider tạo thêm lượt chạy theo từng dòng dữ liệu.

## Tổ chức test theo module

Mỗi class thuộc đúng một module trong **testng.xml**. Test chức năng, Search, assertion và regression issue được đặt chung folder của module.

| Module chạy | Folder trong com.hrm.TestCase | Issue đi kèm |
|---|---|---|
| Login | LoginTab | |
| Admin | AdminTab | #1897 |
| PIM | PIMTab | #1876 |
| Recruitment | Recruitment | #1926 |
| MyInfo | MyInfoTab | #1928 |
| Performance | PerformanceTab | |
| Profile | ProfileTab | #1923 |
| Time | TimeTab | #1787, #1890 |
| Claim | ClaimTab | #1820 |
| PIMAPI | TestAPI/PIMAPI | GET và Create/Update/Delete |
| AdminAPI | TestAPI/AdminAPI | |
| RecruitmentAPI | TestAPI/RecruitmentAPI | Validation và Boundary |
| Security | TestSecurity | Authentication, Authorization, Injection |

POM nằm trong src/main/java/com/hrm/Pages. TestBase, TestBaseAPI, TestUtil, componentLocator và các utility hiện có tiếp tục được dùng.

## Cách chạy

Chuẩn bị JDK 21, Maven, Chrome/Firefox, OrangeHRM local và dữ liệu đối chứng trong TestConfig/Excel.
Chạy lệnh từ thư mục gốc repository để đọc đúng config và test data.

```powershell
mvn -DskipTests test-compile

mvn test -Dmodule=Admin
mvn test -Dmodule=PIM
mvn test -Dmodule=Recruitment
mvn test -Dmodule=MyInfo
mvn test -Dmodule=Performance

mvn test -Dmodule=Profile
mvn test -Dmodule=Time
mvn test -Dmodule=Claim

mvn test -Dmodule=PIMAPI
mvn test -Dmodule=AdminAPI
mvn test -Dmodule=RecruitmentAPI
mvn test -Dmodule=Security
```

Mặc định `mvn test` chạy module Login. Có thể chọn nhiều module:

```powershell
mvn test "-Dmodule=Admin,PIM"
```

Tên module phải đúng chữ hoa/thường. Tên không tồn tại làm build FAIL.
`-o` là tùy chọn khi dependency Maven đã có đủ trên máy; máy CI mới cần tải dependency.

Surefire truyền giá trị module vào tùy chọn testnames của TestNG để chọn thẻ test tương ứng.
Danh sách class khai báo rõ trong XML nhằm giữ thứ tự Add, Search/Edit rồi Delete cho các flow đang dùng chung TestDataShare.
Khi thêm class mới, đặt đúng folder/package và thêm class vào module tương ứng trong testng.xml.

## Cấu hình và dữ liệu

Config đọc config.properties ở thư mục gốc; cấu hình app.url, browser, username và password theo môi trường được phép kiểm thử.
Security cần thêm tài khoản ESS và own/target employee theo tài liệu phân quyền.
Base API dùng OAuthTokenManager để lấy token động; môi trường cần có cấu hình OAuth phù hợp.

Dữ liệu UI nằm trong Datadriven-OrangeHrm.xlsx; ba sheet Search Location, Employee, Vacancy có cột expectedFound.
TRUE yêu cầu tìm thấy dữ liệu phù hợp; FALSE yêu cầu No Records Found.
Fixture Time/Claim cần đúng trạng thái được mô tả trong tài liệu issue; suite không tự sửa fixture sai trạng thái để chạy tiếp.

Các class Candidate Add/Reject/Delete dùng marker riêng và cleanup theo bản ghi do mình tạo.
Employee của OHR25 có mã riêng, tránh trùng employee của flow Add/Edit/Delete trong cùng lượt PIM.

## Kết quả và chuẩn bị CI/CD

- TestNG/Surefire: target/surefire-reports/.
- Allure: target/allure-results/.
- Log4j2: logfile.log.
- Screenshot lỗi được TestListener đính kèm vào Allure.

Các module có issue chưa sửa vẫn có thể FAIL. Không đổi assertion hoặc tự retry để làm kết quả xanh.
testng.xml dùng TestListener hiện có và chạy tuần tự vì TestBase đang giữ driver static.

Lệnh dùng cho một job CI có thể là `mvn -B test -Dmodule=RecruitmentAPI`.
Runner phải truy cập được OrangeHRM, có browser, config và fixture tương ứng.
Workflow `.github/workflows/orangehrm-ci.yml` tự compile khi push/PR; chạy test theo module bằng Run workflow trên Windows self-hosted runner có OrangeHRM local.
Xem [thiết lập GitHub Actions](docs/github-actions.md) để cấu hình runner, secret và xem report local. Chưa có kết quả chạy GitHub Actions được xác nhận.

Xem [cách chạy và cấu trúc module](docs/test-execution.md), [Search/assertion](docs/ui-search-assertions.md) và [Terminate/Reject](docs/ui-workflow-assertions.md).

## Thay đổi đường dẫn

Sau lần gom module ngày 26/10/2026:

- LoginTest chuyển vào LoginTab.
- Issue1923RegressionTest chuyển vào ProfileTab.
- PIMAPITest chuyển vào TestAPI/PIMAPI cùng các test Create/Update/Delete.
- Hai class SearchingUserWithValidData/SearchingUserWithInvalidData đổi thành SearchUserWithValidData/SearchUserWithInvalidData để tên file trong Git khớp public class trên Linux.

Mã OHR1–OHR66 được giữ nguyên.
Các lệnh XML riêng và fullName trong bằng chứng lịch sử vẫn mô tả lần chạy cũ; dùng lệnh module ở trên cho source hiện tại.
