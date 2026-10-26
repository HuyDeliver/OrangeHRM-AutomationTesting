# Assertion OHR25 và OHR30

> Cách chạy hiện tại (26/10/2026): `mvn test "-Dmodule=PIM,Recruitment"` qua `testng.xml`; lệnh này chạy toàn bộ module đã chọn, gồm cả issue. Các tên XML/lệnh riêng và số liệu bên dưới là lịch sử trước khi gộp. Xem [hướng dẫn module](test-execution.md).

Ngày cập nhật: 26/10/2026. Môi trường: OrangeHRM OS 5.9 local.

## OHR25 — Terminate Employee

1. Setup tạo employee riêng bằng TestDataShare và lưu qua POM; không cần tạo Login Details.
2. Nhập ngày/lý do từ TestConfig, thực hiện Terminate bằng component hiện có.
3. Refresh trang Job để đọc lại dữ liệu đã lưu.
4. Assert có nút **Activate Employment**, dấu hiệu UI của employee đã terminated.
5. Click ngày Terminated on để mở lại popup; assert ngày và lý do bằng đúng input.
6. Cleanup refresh để đóng popup, tìm ID riêng trong Current and Past Employees rồi xóa.

Các hàm đọc nằm trong EditEmployeePage. Test giữ assertion dễ đọc; không có locator trong testcase.
Expected là tính nhất quán của thao tác lưu rồi đọc lại qua UI với input đã biết.
Chưa kiểm tra Note, phân quyền hoặc mọi quy tắc nghiệp vụ về ngày chấm dứt.

## OHR30 — Reject Candidate

1. Setup tạo candidate riêng; xác nhận trạng thái đầu **Application Initiated**.
2. Mở đúng candidate theo tên riêng, thực hiện Reject bằng POM/component hiện có.
3. Assert trạng thái **Status: Rejected** sau Save.
4. Refresh; assert vẫn **Status: Rejected** và history có hành động **rejected**.
5. Cleanup dùng CandidatesPage.deleteTestCandidate với marker riêng.

Status dùng chữ Rejected viết hoa; câu trong history dùng rejected viết thường.
Đã đối chiếu trực tiếp qua UI; không suy ra một nhãn giống nhau cho hai vị trí.
History được kiểm tra bằng componentLocator.checkTable; chưa kiểm tra thời gian/người thao tác hoặc Note.

## Dữ liệu và log

Hai case giữ nguyên mã OHR, Epic/Feature và TestNG setup hiện có.
Sau khi xác nhận fixture đã tạo, cleanup chạy với alwaysRun=true kể cả khi assertion FAIL.
Chỉ xóa ID/marker do chính testcase tạo; OHR25 tìm cả current và past để vẫn cleanup nếu Terminate không thành công.
Mỗi testcase ghi một log kết quả cuối; các thao tác dùng log trong component/POM hiện có.

## Lệnh và kết quả lịch sử

```powershell
mvn -o -DskipTests test-compile
mvn -o "-Dtestng.suite=testng_workflow_assertions.xml" test
```

Suite dùng TestListener hiện có, không retry và chỉ chạy hai class OHR25/OHR30.

Compile: **BUILD SUCCESS**.
Lần xác minh cuối hoàn tất **22:26:39 ngày 26/10/2026, UTC+7**:

| Testcase | Kết quả |
|---|---|
| OHR25 — trạng thái, ngày và lý do sau refresh | PASS |
| OHR30 — trạng thái và history sau refresh | PASS |
| Các bước setup/cleanup | PASS |

Maven **BUILD SUCCESS**; **2 tests, 0 failures, 0 errors, 0 skipped**. Suite chạy **77,35 giây**.
Sau suite, Selenium tìm lại cả hai employee ID của hai lần chạy trong Current and Past Employees: đều No Records Found.
Cleanup candidate đã xác nhận marker riêng không còn trong bảng bằng helper hiện có.
Báo cáo: target/surefire-reports/testng-results.xml và target/surefire-reports/index.html.

Lần đầu có hai lỗi kiểm tra trong script: toast Terminate đã hết hạn sau bước chụp ảnh; history bị so sai hoa/thường.
Đã thay kiểm tra toast bằng kiểm tra dữ liệu lưu sau refresh và sửa literal history theo UI.
Giữ nguyên yêu cầu về trạng thái/ngày/lý do và sự tồn tại của history; hai FAIL ban đầu chưa phải bằng chứng bug ứng dụng.
