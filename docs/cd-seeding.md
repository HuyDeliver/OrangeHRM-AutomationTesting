# CD minimal seed (Phase 2)

Seed là bước chuẩn bị dữ liệu trước test. Database CD được giữ nguyên; seed tìm fixture
theo Employee ID, chỉ tạo nếu thiếu, rồi đọc lại để xác minh. Không copy dữ liệu DEV,
không drop/restore database, không tạo tài khoản Admin mới.

## Phạm vi

| Prerequisite | Cách chuẩn bị |
|---|---|
| Login | Dùng Admin CD đã initialize, credential từ config CD |
| Admin Add User | Employee `AUTOEMP001`, tên giả `AUTO SeedEmployee`, chọn được trong form Add User |
| PIM Search nhỏ | Employee ID/tên trên và negative ID `AUTONONE01` không tồn tại |

Không tạo User của testcase Add User. Employee/User có tên ngẫu nhiên từ TestDataShare
vẫn do testcase tự tạo. Không ép fixture đáp ứng toàn bộ Excel PIM. Chưa seed
Recruitment, My Info, Performance, API, Security, Time hoặc Claim.

Constraint đã đọc từ source OrangeHRM 5.9 trong image CD: EmployeeService giới hạn
employeeId 10 ký tự, firstName/lastName 30 ký tự; EmployeeAPI validate employeeId unique.
Các giá trị fixture nằm trong `deploy/seed.properties`, không chứa thông tin cá nhân thật.
Internal empNumber do OrangeHRM cấp, seed đọc từ trang detail, không hard-code.

## Chạy bằng Windows PowerShell

Tại thư mục project, bật Docker/CD và bảo đảm không có pipeline khác đang ghi dữ liệu CD:

```powershell
.\deploy\wait-ready.ps1
.\deploy\seed.ps1 -RunName sample-1
.\deploy\seed.ps1 -RunName sample-2
```

Tên lượt phải mới để không ghi đè bằng chứng cũ. Bỏ RunName sẽ dùng timestamp.
Script đọc `deploy/config.cd.properties` đã có. Không cần truyền password trên command line.

Script từ chối URL khác `http://localhost:8081/web/index.php/auth/login`, kiểm tra
Docker project/port và Conf.php phải dùng database `orangehrm_cd` trên host `db`.
CDSeedTest cũng chặn URL khác và yêu cầu bước preflight của script. Không chạy thẳng
class qua IDE rồi bỏ qua script. Chỉ truy cập ứng dụng CD bằng Selenium/POM.

Nếu tìm kiếm lỗi, tìm thấy duplicate, fixture sai tên hoặc negative ID đã tồn tại:
seed FAIL, không tự xóa/sửa những bản ghi đó. Nếu một lần seed dừng sau khi tạo employee,
lần sau tìm lại và reuse employee đó. Baseline được giữ lại, không cleanup cuối seed.

## Kết quả và số lượng testcase

Mỗi lượt lưu riêng tại `target/cd-seed/<RunName>/`:

- `seed.log`: log local, có các dòng `[SEED]`.
- `seed-result.properties`: key, internal ID, số match, CREATED/REUSED và kết quả preparation.
- `surefire/`, `allure-setup/`: report kỹ thuật của bước setup.

CDSeedTest có group `data-preparation`, không mã OHR, không nằm trong testng.xml.
Surefire có thể ghi một setup method đã chạy; **không tính method này vào số functional,
security hoặc regression PASS**. Không gộp thư mục allure-setup vào báo cáo regression.
Không ghi password, token, cookie hoặc client secret vào log seed.

## Kết nối testcase sau Phase 2

TestConfig.employeeName hỗ trợ config `admin.employee.name`, mặc định DEV vẫn như cũ.
Để nhóm Admin User dùng employee CD, thêm vào config CD (và Secret CD khi tích hợp):

```properties
admin.employee.name=AUTO SeedEmployee
```

Nhóm `CDPIMTest` đọc trực tiếp `deploy/seed.properties` để dùng cùng dữ liệu với seed.
PIM Search cũ vẫn đọc Excel cũ; chưa tuyên bố toàn bộ PIM Excel hoặc module Admin/PIM chạy được.
Các danh mục Job/Salary và baseline khác được bổ sung khi mở rộng phạm vi.

Workflow hiện chạy readiness → seed → Login smoke → ba test PIM CD → chuỗi Admin User.
Mỗi nhóm phải qua bước kiểm tra report trước khi chạy nhóm tiếp theo. Report setup vẫn tách riêng.
Các chuỗi Add/Edit/Delete dùng TestDataShare phải chạy cùng một Maven/TestNG invocation.

CDSeedTest chỉ là một class setup dùng POM. Không thêm service/repository/factory.
Khi API CD được cấu hình và xác minh, có thể thay phần tìm/tạo fixture bằng API mà vẫn
giữ fixture key, script preflight và quy tắc report. Hiện API/OAuth 8080 chưa được dùng.

## Kết quả thực tế ngày 29/10/2026

Compile: SUCCESS. Script được chạy bằng Windows PowerShell 5.1, Chrome headless.
Thử truyền config DEV: bị chặn trước Docker/browser, không có thao tác ghi DEV.

| Lượt xác minh cuối | Fixture key | Internal ID | Match | Duplicate | Preparation |
|---|---|---|---|---|---|
| final-1 | AUTOEMP001 | 2 | 1 | NO | SUCCESS (REUSED) |
| final-2 | AUTOEMP001 | 2 | 1 | NO | SUCCESS (REUSED) |

Đối chiếu DB CD read-only: employee từ 1 lên 2, user vẫn 1; chỉ một employee có key
AUTOEMP001, negative key AUTONONE01 không tồn tại. Admin prerequisite: READY (chọn
employee được trong Add User). PIM prerequisite: READY cho fixture ID/name/negative này.
DEV 8080 touched: NO (không truy cập/ghi app hoặc DB DEV; container IDs/status/mounts không đổi).
Recruitment/My Info/Performance/API/Security: NOT IMPLEMENTED. Regression chưa chạy.

Các lượt thử trước đó được giữ nguyên log, không tính là PASS: run-1 bị sidebar ẩn vì
headless maximize thành 800x600; verified-1 đã tạo employee ID 2 nhưng FAIL ở so khớp
tên có middle name trống (UI trả `AUTO  SeedEmployee`). Đã cố định cửa sổ seed
1920x1080 và chuẩn hóa khoảng trắng khi đọc tên được chọn. Hai lượt final ở trên
chứng minh reuse cả fixture còn lại sau một lần setup bị dừng, không tạo bản sao.

Fixture baseline được giữ lại. Riêng bước seed thêm **0** functional/security/regression testcase.

## Nhóm PIM CD nhỏ (OHR67–69)

| Case | Thao tác | Expected |
|---|---|---|
| OHR67 | Tìm Employee ID positive trong seed.properties | Đúng 1 record và bảng chứa đúng ID |
| OHR68 | Tìm Employee ID negative đã được seed xác nhận không tồn tại | 0 record, hiển thị No Records Found |
| OHR69 | Tìm rồi mở detail của employee baseline | Employee ID, First Name, Last Name khớp fixture |

Expected dựa trên dữ liệu fixture đã kiểm chứng và chức năng tìm kiếm/hiển thị employee.
Ba test chỉ đọc; không tạo/sửa/xóa employee. Không dùng empNumber hard-code.
Đây là ba lượt kiểm tra chức năng trên CD, có phần tìm kiếm trùng phạm vi OHR23;
không coi là ba chức năng nghiệp vụ hoàn toàn mới khi thống kê coverage.

Chạy sau seed, tại thư mục project (dùng Windows PowerShell):

```powershell
mvn -B -ntp test -Pcd-pim "-Dconfig.file=deploy/config.cd.properties" "-Dheadless=true"
.\deploy\pim-report.ps1
```

Profile chỉ chọn `com.hrm.TestCase.PIMTab.CDPIMTest`; không chạy cả module PIM cũ.
Trong GitHub Actions, log chi tiết nằm ở `ci-pim.log` trên runner; bước kiểm tra report
hiển thị tổng PASS/FAIL/SKIP. Artifact có thêm `pim-summary.json`, ghi tên và kết quả
từng test. Surefire nằm trong `target/cd-pim/surefire`, Allure trong
`target/cd-pim/allure-results`, không ghi đè Login hoặc setup.
Chỉ PASS khi đúng cả ba method đều PASS và không có lỗi setup/teardown.
Không đổi expected hoặc dùng skip để làm pipeline xanh khi assertion thất bại.

### Kết quả chạy local ngày 31/10/2026

- Compile: PASS. Chrome headless, Windows PowerShell 5.1, CD 8081.
- Seed `pim-integration-20261031`: SUCCESS, REUSED `AUTOEMP001`, empNumber=2,
  match.count=1, duplicate=NO. Đây là setup, không cộng vào functional PASS.
- Login smoke: 1 PASS, 0 FAIL, 0 SKIP.
- OHR67: PASS, actual count=1 và ID trong bảng khớp fixture.
- OHR68: PASS, actual count=0 và có No Records Found.
- OHR69: PASS, ID/First Name/Last Name khớp fixture.
- Report PIM: 3 PASS, 0 FAIL, 0 SKIP; file `target/cd-evidence/pim-summary.json`.
- DEV 8080 không được truy cập bởi các test; container IDs/status/mounts trước/sau không đổi.
- Workflow đã thêm bước PIM nhưng chưa push/chạy bản thay đổi này trên GitHub Actions.

## Chuỗi Admin User trên CD

Tái sử dụng bốn testcase hiện có, giữ mã OHR; không tạo thêm bốn testcase mới để tăng số lượng.
Nhóm `CDAdminUser` được khai báo trong `testng.xml` hiện có. Maven profile `cd-admin-user`
chọn đúng nhóm này và chạy cả chuỗi trong một JVM để giữ nguyên `TestDataShare.USERNAME`.

| Thứ tự | Case | Assertion |
|---|---|---|
| 1 | OHR6 - Add User | Có toast thành công và tìm lại được đúng 1 user |
| 2 | OHR4 - Search User | Bảng có đúng username, role, employee, status; đúng 1 dòng |
| 3 | OHR7 - Edit User | Đổi mật khẩu thành công; xóa cookie rồi đăng nhập user bằng mật khẩu mới và thấy Dashboard |
| 4 | OHR8 - Delete User | Có toast thành công; tìm lại username trả 0 record |

Employee baseline được giữ lại. Username và hai mật khẩu của user tạm được tạo riêng cho mỗi
lượt chạy trong TestDataShare; không đổi mật khẩu Admin dùng đăng nhập môi trường.
Không log giá trị mật khẩu. Các thao tác vẫn dùng Selenium/POM/componentLocator/TestUtil.

`CDAdminUserSetup` kiểm tra URL CD 8081 và tên baseline trước khi chạy. `@AfterTest(alwaysRun=true)`
đăng nhập lại bằng Admin môi trường, tìm username của lượt chạy, xóa nếu còn và xác nhận 0 record.
Nếu cleanup lỗi, toàn nhóm FAIL dù bốn test đã PASS. Setup/cleanup không được tính vào số test.
Cleanup chỉ xử lý username của cùng lượt chạy; nếu runner bị tắt đột ngột thì cần kiểm tra log
và dọn user đó thủ công. Không tự xóa user của các lượt khác.

Workflow tự thêm `admin.employee.name` từ seed.properties vào config CD được tạo trên runner,
nên không cần đổi GitHub Secret. Chạy local cần property này trong deploy/config.cd.properties:

```properties
admin.employee.name=AUTO SeedEmployee
```

Sau seed, chạy tại thư mục project:

```powershell
mvn -B -ntp test -Pcd-admin-user "-Dconfig.file=deploy/config.cd.properties" "-Dheadless=true"
.\deploy\admin-user-report.ps1
```

Report riêng: `target/cd-admin-user/surefire`, `target/cd-admin-user/allure-results`.
Log trên runner: `ci-admin-user.log`. GitHub chỉ nhận `admin-user-summary.json` gồm kết quả,
tên testcase và trạng thái cleanup. Report chỉ PASS khi đủ đúng 4 test PASS, không SKIP,
không lỗi configuration và cleanup đã PASS. Không chạy profile này cùng cd-seed/cd-pim/cd-smoke
trong một lệnh; riêng bốn testcase Admin phải luôn đi cùng nhau.

### Kết quả xác minh Admin User ngày 31/10/2026

- Chuỗi cuối: OHR6 → OHR4 → OHR7 → OHR8, **4 PASS, 0 FAIL, 0 SKIP**.
- User tạm: `HuyDeliver_8d2b253c`. Sau Delete và cleanup: 0 record; baseline vẫn được giữ.
- Đã xác nhận đăng nhập được bằng mật khẩu mới qua phiên không có cookie Admin.
- Cleanup: PASS; không tính là testcase thứ năm. Seed: REUSED, employee empNumber=2.
- Lượt thử đầu FAIL sau khi tạo vì form Save còn loading; cleanup đã xóa user còn lại.
  Đã dùng waitUtils đợi ô tìm kiếm của trang danh sách trước khi nhập, không thêm sleep.
- Lượt thứ hai Search FAIL do cách thao tác dropdown mới; đã giữ lại cách chọn có sẵn
  trong POM. Add/Edit/Delete và cleanup của lượt đó vẫn PASS. Hai lượt lỗi lưu riêng tại
  `target/cd-admin-first-failure` và `target/cd-admin-second-failure`, không đổi assertion để PASS.
- Bộ tổng hợp report đã được thử bằng XML giả lập 4 PASS nhưng cleanup FAIL: từ chối đúng.
- Kiểm tra lại sau thay đổi TestBase headless: Login 1 PASS, PIM 3 PASS. Tổng ba nhóm
  ở lượt xác minh cuối: 8 functional PASS; seed/cleanup không cộng vào số này.
- DEV không được các test truy cập; container IDs/status/mounts trước và sau không đổi.
- Workflow đã cập nhật local; chưa push/chạy bản Admin User mới trên GitHub Actions.
