# PIM regression trên CD 8081

Dùng lại module PIM trong testng.xml, Selenium/POM/componentLocator/TestUtil, TestNG,
Log4j và Allure. Một lệnh Maven chạy cả chuỗi để TestDataShare giữ đúng employee/user.
Profile `cd-pim` cũ vẫn chạy riêng ba kiểm tra baseline; profile mới là `cd-pim-regression`.

## Test và expected

| Case | Kiểm tra | Expected |
|---|---|---|
| OHR19 | Tạo employee và tài khoản Disabled | Lưu đúng ID, first/last name |
| OHR20 | Sửa Job Details | Sau refresh, đúng ngày, job, category, subunit, location, status |
| OHR21 | Thêm Salary Component | Sau refresh, đúng component, amount, currency, frequency |
| OHR22 | Gán supervisor | Sau refresh, đúng tên supervisor và Reporting Method |
| OHR23 (4 dòng) | Tìm theo tên, ID âm tính, status, tổ hợp bộ lọc | Khớp expectedFound từ Excel |
| OHR24 | Xóa employee của chuỗi | Tìm lại ID trả 0 record |
| OHR25 | Terminate employee riêng | Sau refresh giữ trạng thái, ngày và lý do; cleanup riêng |
| OHR41 / #1876 | Chọn Past bằng TAB so với ENTER | UI giữ Past và hai cách trả cùng số record |

Tổng 8 method, 11 lượt chạy; không tạo thêm mã testcase để tăng số lượng.
Add tạo dữ liệu trước các Edit; Search đợi Job và Supervisor thành công; Delete chạy sau Search.
Delete chỉ phụ thuộc Add để vẫn thực hiện được khi Edit/Search lỗi. Cleanup cuối vẫn chạy khi test FAIL.

Excel Employee không bị sửa. Riêng CD, DataProvider thay giá trị tìm kiếm bằng tên/ID employee
của lượt chạy, danh mục seed và tên supervisor baseline; giữ nguyên Include và expectedFound.
Dòng ID âm tính dùng AUTONONE01 đã được seed xác nhận không tồn tại. Bộ lọc Sub Unit dùng
unit CD riêng; không tuyên bố đã kiểm tra quan hệ unit cha/con từ dữ liệu DEV cũ.

## Seed và cấu hình

`deploy/pim.properties` chứa tên danh mục CD, không chứa password/token/internal ID.
Workflow ghép file này vào config CD trên runner; credential vẫn lấy từ GitHub Secret.
TestConfig đọc property tương ứng, giữ mặc định cho DEV khi không cấu hình override.

`deploy/seed.ps1 -IncludePIM` chạy CDPIMSeedTest, kế thừa CDSeedTest:

1. Xác minh Docker project/port/database CD bằng script hiện có.
2. Tìm/tạo và xác minh employee AUTOEMP001; negative ID phải vắng mặt.
3. Tìm từng danh mục, chỉ tạo nếu thiếu; duplicate phải FAIL.
4. Pay Grade phải có Vietnamese Dong và range 0–100000. Không dùng Euro của Issue #1897.

| Fixture key | Dữ liệu |
|---|---|
| job | AUTO CD Job |
| category | AUTO CD Category |
| status | AUTO CD Full Time |
| subunit | AUTO CD PIM |
| location | AUTO CD Location |
| paygrade | AUTO CD Grade |
| supervisor | AUTO SeedEmployee (employee baseline) |

Danh mục và employee baseline được giữ lại để dùng lần sau. Không cần tạo thêm employee Past:
Issue #1876 lấy baseline Current/Past bằng UI và chỉ có ý nghĩa khi hai số đếm khác nhau.
Nếu điều kiện này không đủ, test SKIP và report/pipeline không được tính PASS.

Seed gồm hai method setup, không tính vào functional/security/regression PASS.
Bằng chứng seed nằm trong `target/cd-seed/<run>/seed-result.properties` và
`pim-seed-result.properties`; report Allure setup tách khỏi test chức năng.

## Chạy local

Chỉ dùng deploy/config.cd.properties trỏ localhost:8081. Ghép nội dung deploy/pim.properties
vào file config CD này; cần giữ `admin.employee.name=AUTO SeedEmployee`. Không ghép vào config DEV.
GitHub Actions tự thực hiện phần ghép config, không cần đổi Secret.

```powershell
.\deploy\seed.ps1 -IncludePIM -RunName pim-check-1
.\deploy\seed.ps1 -IncludePIM -RunName pim-check-2
mvn -B -ntp test -Pcd-pim-regression "-Dconfig.file=deploy/config.cd.properties" "-Dheadless=true"
.\deploy\pim-regression-report.ps1
```

Mỗi RunName phải mới. Không chạy các testcase Edit/Delete bằng các lệnh Maven tách rời.
Log trên runner: ci-pim-regression.log. Surefire/Allure: target/cd-pim-regression/.
Artifact GitHub chỉ nhận pim-regression-summary.json; không upload config, token hoặc raw log.

CDPIMSetup cleanup employee CRUD, employee Terminate và user của lượt chạy, xác nhận 0 record.
Không xóa employee baseline, supervisor hoặc các danh mục seed. Nếu runner bị tắt giữa chừng,
cần kiểm tra username/Employee ID trong log để dọn bản ghi đúng lượt đó.
Report chỉ PASS khi đủ 11 lượt PASS, không SKIP/lỗi configuration và cleanup PASS.
Issue #1876 vẫn FAIL nếu bug tồn tại; không đổi expected hoặc bỏ test để làm pipeline xanh.

## Kết quả thực tế ngày 01/11/2026

Compile thành công. Đã chạy hai lượt regression sau khi chuẩn bị dữ liệu; lượt cuối dùng
code đã gom chờ kết quả tìm kiếm vào POM. Cả hai lượt: **10 PASS, 1 FAIL, 0 SKIP**.

- PASS: OHR19, OHR20, OHR21, OHR22, bốn dòng OHR23, OHR24 và OHR25.
- FAIL: OHR41 / Issue #1876. Past xác nhận bằng ENTER = 0; TAB = 3 dù UI hiển thị Past.
  Assertion nghiệp vụ phát hiện lỗi, không phải lỗi locator/setup. Giữ nguyên expected.
- Cleanup employee CRUD, employee Terminate và user đều PASS; các ID của lượt cuối
  `14b2848e` và `T14b2848e` đều trả 0 record sau cleanup.
- Không có dòng nhập Password/Confirm Password bị ghi lộ giá trị trong log đã kiểm tra.
- DEV 8080 không được truy cập/ghi dữ liệu bởi test; IDs/status/mounts các container DEV
  trước và sau không đổi.

Hai lượt seed `pim-full-first` và `pim-full-second` có cùng tên/URL định danh/số match:

| Fixture | ID lượt 1 | ID lượt 2 | Match sau lượt 2 | Duplicate |
|---|---|---|---:|---|
| Employee AUTOEMP001 | 2 | 2 | 1 | NO |
| Job AUTO CD Job | 3 | 3 | 1 | NO |
| Category AUTO CD Category | 10 | 10 | 1 | NO |
| Status AUTO CD Full Time | 1 | 1 | 1 | NO |
| Location AUTO CD Location | 3 | 3 | 1 | NO |
| Pay Grade AUTO CD Grade | 5 | 5 | 1 | NO |
| Sub Unit AUTO CD PIM | UI không hiển thị ID | UI không hiển thị ID | 1 | NO |

ID được đọc từ URL edit thực tế; không gán các số này trong testcase.
Sub Unit được đối chiếu bằng tên nguyên văn trên cây Structure, không bịa internal ID.
Seed chỉ là preparation, không cộng hai setup method vào PASS chức năng.

Bộ tổng hợp report thật trả mã khác 0 với 10 PASS + 1 FAIL và cleanup=PASS.
Lượt đầu được lưu ở `target/pim-regression-first-run`; lượt cuối ở `target/cd-pim-regression`.
Workflow được cập nhật local, chưa push hoặc chạy bản PIM regression mới trên GitHub Actions.
Sau push, nếu bug #1876 và #1897 vẫn còn thì PIM và Admin đều báo FAIL; module kia vẫn
được chạy để thu đủ kết quả, không dùng continue-on-error để làm xanh pipeline.
