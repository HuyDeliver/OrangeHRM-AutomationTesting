# Recruitment regression trên CD 8081

Chạy lại các test UI có sẵn bằng Selenium/POM/TestNG. Không dùng Rest Assured,
không copy database DEV, không thêm functional testcase chỉ để tăng số lượng.

## Phạm vi

| Mã | Kiểm tra | Số lượt |
|---|---|---:|
| OHR26 | Thêm vacancy và attachment; tìm lại đúng tên/job/manager/Active | 1 |
| OHR27 | Search theo Excel, giữ cả positive và negative | 4 |
| OHR28 | Xóa vacancy của lượt chạy; kiểm tra còn 0 record | 1 |
| OHR29 | Thêm candidate; đọc lại tên và trạng thái Application Initiated | 1 |
| OHR30 | Reject; refresh vẫn Rejected và có history | 1 |
| OHR31 | Xóa candidate riêng; kiểm tra còn 0 record | 1 |
| OHR46 / #1926 | Shortlist, refresh kiểm tra status và history trên ba vacancy | 3 |

Tổng: **7 method, 12 lượt chạy**. Ba method seed và các hook cleanup là setup,
không cộng vào functional/security/regression PASS.

## Dữ liệu

- Tái sử dụng employee `AUTOEMP001` và danh mục của PIM seed.
- Hiring Manager là `AUTO SeedEmployee`, không cần tạo tài khoản đăng nhập mới.
- Giữ ba vacancy riêng: `AUTO CD Vacancy A/B/C`, cùng Job Title `AUTO CD Job`.
- Seed tìm trước, chỉ tạo nếu chưa có; dừng nếu trùng hoặc sai job/manager/status.
- Lấy vacancy ID từ trang edit thực tế, không hard-code internal ID.
- Vacancy CRUD dùng `TestDataShare.VACANCY_NAME`; candidate dùng tên riêng mỗi lượt.
- Candidate tạm được xóa; vacancy baseline giữ lại để dùng lần sau.

Khi bật `cd.recruitment`, DataProvider thay các ô Job/Vacancy/Manager có dữ liệu
bằng fixture CD tương ứng, giữ nguyên ô trống, Status và expectedFound trong Excel.
File Excel không bị sửa. Dòng Closed/false vẫn phải không có kết quả; không đổi
expected nếu dữ liệu môi trường khiến test fail. Ba vacancy cùng job là phạm vi
CD tối thiểu, chưa chứng minh khác biệt giữa ba ngành nghề của dữ liệu DEV.

## Chạy

Workflow tự ghép `deploy/recruitment.properties` vào config lấy từ Secret hiện có.
Không cần thêm Secret. Chạy local thì ghép các dòng non-secret này vào
`deploy/config.cd.properties` trước; URL phải trỏ đúng CD 8081.

```powershell
.\deploy\seed.ps1 -IncludeRecruitment -RunName recruitment-run1
.\deploy\seed.ps1 -IncludeRecruitment -RunName recruitment-run2
mvn -B -ntp test -Pcd-recruitment "-Dconfig.file=deploy/config.cd.properties" "-Dheadless=true"
.\deploy\recruitment-report.ps1
```

`-IncludeRecruitment` gồm cả minimal employee và PIM catalog prerequisites.
Các class Recruitment chạy trong **một Maven/TestNG invocation**, dùng nhóm
Recruitment trong `testng.xml` hiện tại; không tạo XML riêng. Search/Delete vacancy
phụ thuộc Add vacancy. Candidate dùng baseline riêng nên không lệ thuộc vacancy CRUD.

## Đọc kết quả

- Log trên runner: `ci-recruitment.log`.
- TestNG: `target/cd-recruitment/surefire/testng-results.xml`.
- Allure: `target/cd-recruitment/allure-results`.
- JSON an toàn để upload: `target/cd-evidence/recruitment-summary.json`.
- Seed evidence: `target/cd-seed/<RunName>/recruitment-seed-result.properties`.

Report chỉ PASS khi đủ 12 lượt PASS, không có configuration failure, cleanup đầy đủ.
Recruitment vẫn chạy nếu Admin/PIM regression fail, miễn seed và login smoke thành công.
Bất kỳ test/configuration/cleanup thất bại nào vẫn làm job đỏ; không dùng expected failure.
Các helper đếm bảng sẽ dừng nếu chưa đọc đủ dữ liệu do phân trang, tránh báo 0 sai.

## Xác minh seed ngày 03/11/2026

Hai lượt hoàn tất: `recruitment-run1`, `recruitment-run2`.

| Fixture key | ID run 1 | ID run 2 | Record sau run 2 | Duplicate |
|---|---:|---:|---:|---|
| AUTOEMP001 (employee) | 2 | 2 | 1 | NO |
| AUTO CD Vacancy A | 1 | 1 | 1 | NO |
| AUTO CD Vacancy B | 2 | 2 | 1 | NO |
| AUTO CD Vacancy C | 3 | 3 | 1 | NO |

PIM catalog cũng được kiểm tra/tái sử dụng ở cả hai lượt. Các ID trên là kết quả
quan sát của môi trường này, không được đưa vào input test.

Hai lần chuẩn bị trước đó (`recruitment-first`, `recruitment-diagnose`) dừng vì
helper kiểm tra toast lúc chuyển sang Edit Vacancy. Screenshot xác nhận record đã
lưu. Đã sửa POM chờ Edit và seed kiểm tra lại danh sách theo tên/job/manager/status;
không đổi expected nghiệp vụ. Giữ log của các lần lỗi để đối chiếu.

## Kết quả regression ngày 03/11/2026

Lượt cuối kết thúc 21:47:53 (giờ Việt Nam), Maven khoảng 4 phút 11 giây:

- **12 PASS, 0 FAIL, 0 SKIP; BUILD SUCCESS.**
- Add/Search/Delete Vacancy: 6 PASS.
- Add/Reject/Delete Candidate: 3 PASS.
- Issue #1926 trên ba vacancy: 3 PASS; status và History giữ đúng sau refresh.
- Cả 7 hook cleanup đều PASS; không có configuration failure.
- DEV 8080 touched: **NO**. Test/seed chỉ dùng CD 8081; Docker inspect trước/sau
  xác nhận ID, running state và mount của ba container DEV không đổi. Đây không
  phải phép checksum toàn bộ database DEV.

Lượt regression đầu có 10 PASS, 2 FAIL, cleanup PASS. Đã sửa hai điều kiện đồng bộ:
Add Candidate phải chờ Application Stage trước refresh; kiểm tra History dùng
`waitUtils.waitForTable` vì status và bảng History tải riêng. Không đổi expected,
không thêm retry. Bằng chứng lượt đầu giữ tại `target/recruitment-first-run/`.

Kết quả trên là chạy **local CD**, chưa phải xác nhận bản workflow mới đã PASS trên
GitHub Actions. Sau commit/push main, Recruitment sẽ tự chạy. Pipeline vẫn có thể
đỏ do issue còn mở của Admin/PIM; cần đọc summary riêng từng module.
