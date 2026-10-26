# Chạy OrangeHRM bằng GitHub Actions

Workflow: `.github/workflows/orangehrm-ci.yml`.

## Workflow làm gì?

- Push hoặc Pull Request: compile source và test bằng Java 21 trên Ubuntu của GitHub. Không chạy test và không cần OrangeHRM.
- Run workflow: compile trước, sau đó chạy một module trên Windows self-hosted runner của bạn.
- Test FAIL làm job FAIL. Log test và report được giữ trên máy runner, không tự upload lên GitHub.

OrangeHRM hiện chạy local và có fixture riêng. Máy runner GitHub không truy cập được localhost trên máy bạn, nên job test dùng chính máy có OrangeHRM. Không tự dựng database mới vì dữ liệu hiện có chưa được đóng gói thành fixture CI.

Đây là pipeline CI kiểm tra automation; chưa có bước deploy ứng dụng/CD.

## Thiết lập một lần

1. Đưa source và workflow lên nhánh mặc định của repository GitHub.
2. Mở **Settings → Actions → Runners → New self-hosted runner**. Chọn Windows x64, làm theo lệnh GitHub cung cấp trong thư mục riêng, ví dụ `C:\actions-runner`. Không dùng thư mục project hiện tại làm thư mục runner.
3. Thêm label **orangehrm-local**. Workflow yêu cầu `self-hosted`, `Windows`, `X64`, `orangehrm-local`. Dùng runner mới theo hướng dẫn GitHub để tương thích các action hiện tại.
4. Máy runner cần Maven trong PATH, Chrome/Firefox tương ứng config, Internet để tải dependency/driver và OrangeHRM local đang chạy. Workflow tự thiết lập Java 21.
5. Selenium hiện mở browser có giao diện. Chạy runner bằng `.\run.cmd` trong phiên Windows đang đăng nhập; chưa cài dưới dạng service cho luồng UI này. Giữ máy thức khi test chạy.
6. Mở **Settings → Secrets and variables → Actions → New repository secret**. Tạo secret **ORANGEHRM_CONFIG**, giá trị là toàn bộ nội dung `config.properties` đang chạy được trên máy bạn. Giữ đủ cấu hình UI, OAuth, ESS và fixture cần cho module. Không dán nội dung vào YAML hay log.

Workflow checkout vào workspace riêng của runner, tạo `config.properties` từ secret rồi xóa file đó sau job. Không sửa config trong thư mục project bạn đang làm việc.

## Chạy test

1. Khởi động OrangeHRM và runner; trên GitHub runner phải hiện **Idle**.
2. Mở **Actions → OrangeHRM CI → Run workflow**.
3. Chọn **nhánh mặc định** và module, nên thử `Login` trước, rồi bấm **Run workflow**.
4. Xem job **Compile Java 21** và **Test ... on local OrangeHRM**.

Job test chỉ chạy khi dispatch từ nhánh mặc định. Dispatch nhánh khác vẫn compile nhưng bỏ qua test. Push/PR không tự gửi test vào máy cá nhân.
Self-hosted runner chỉ dùng cho source đáng tin cậy; ưu tiên repository private theo [hướng dẫn GitHub](https://docs.github.com/en/actions/how-tos/manage-runners/self-hosted-runners/add-runners).

Lệnh bên trong job giữ đúng cách chạy hiện tại:

```powershell
mvn -B -ntp clean test "-Dmodule=Login"
```

Không chạy IDE hoặc Maven khác đồng thời trên cùng database. Các job test của workflow được xếp tuần tự để tránh tranh chấp fixture. Dữ liệu và quyền tài khoản vẫn cần đúng [tiền điều kiện của module](test-execution.md).

## Đọc kết quả

- Compile xanh chỉ có nghĩa Java compile được; chưa chứng minh test PASS.
- Test xanh/đỏ theo exit code Maven, không bỏ qua lỗi hoặc thay expected.
- Job chờ runner: kiểm tra máy bật, runner online và đủ label.
- Test bị skipped: xem tiền điều kiện; skipped không chứng minh chức năng đã được kiểm tra.
- Module có issue đã biết có thể FAIL đúng với mục tiêu regression.

Trong job **Show local report locations**, xem đường dẫn workspace trên máy runner:

- `ci-test.log`: toàn bộ output Maven/test, giữ local để không đưa token/response lên GitHub.
- `target/surefire-reports`: kết quả TestNG, XML/HTML và chi tiết lỗi.
- `target/allure-results`: dữ liệu gốc Allure và attachment do framework tạo, chưa phải website HTML. Có thể xem bằng Allure CLI nếu đã cài: `allure serve <đường-dẫn-allure-results>`.

Report không được upload tự động. Copy report cần giữ sang thư mục riêng trước lần chạy kế tiếp vì checkout/clean sẽ dọn workspace cũ.

## Trạng thái xác minh

Ngày 26/10/2026: YAML parse thành công; danh sách 13 module khớp `testng.xml`; các script PowerShell không có lỗi cú pháp; `mvn -o -B -ntp -DskipTests test-compile` trả BUILD SUCCESS.

Chưa có lần chạy GitHub Actions được xác nhận trong phiên này. Cần cấu hình runner và secret trên repository trước khi chạy thực tế. Các kiểm tra local được ghi trong hướng dẫn chạy module.
