# CI/CD OrangeHRM — bản tối thiểu

Pipeline tự triển khai môi trường kiểm thử, không deploy production và không build source OrangeHRM.

## Luồng chạy

Push/merge `main` → CI compile → deploy CD 8081 → readiness → OHR1 Login smoke headless → report.

- Pull Request vào main: chỉ CI compile, không deploy.
- Run workflow trên main: chạy lại cùng pipeline, không có checkbox Deploy.
- Compile FAIL: job CD không chạy. Deploy/readiness/smoke FAIL: pipeline FAIL.
- Concurrency giữ một job deploy/test tại một thời điểm; không hủy job đang chạy để thay lượt mới.
- Regression và API CD chưa bật. Automatic rollback: TODO / future improvement.

## Môi trường

| | DEV | CD |
|---|---|---|
| URL | http://localhost:8080 | http://localhost:8081 |
| Compose project | orangehrmtesting | orangehrm-cd |
| Web | orangehrmtesting-orangehrm-1 | orangehrm-cd-web-1 |
| DB | orangehrmtesting-db-1 | orangehrm-cd-db-1 |
| Database | orangehrm | orangehrm_cd |
| DB volume | orangehrmtesting_db_data | orangehrm-cd_cd-db |
| Config volume | volume ứng dụng DEV hiện có | orangehrm-cd_cd-config |

CD không publish database; web chỉ bind 127.0.0.1:8081. Không copy DB DEV, không prune, không chạy compose DEV. Workflow đối chiếu container ID, trạng thái chạy và mounts DEV trước/sau.

OrangeHRM 5.9 và MariaDB 10.4 được pin bằng digest đã xác minh từ image local trong `deploy/compose.cd.yml`. Không tự nâng version/migrate DB mỗi push.

## Tại sao cần renew volume?

Image OrangeHRM lưu thư mục ứng dụng `/var/www/html` trong anonymous volume. Khi recreate container, Docker có thể dùng lại volume cũ nên mã cũ vẫn chạy dù image đã đổi. Deploy dùng `--force-recreate --renew-anon-volumes` cho riêng web CD để lấy mã từ image pin. Database và `lib/confs` nằm trong named volumes riêng nên vẫn được giữ. Không xóa volume DEV hoặc tự prune anonymous volumes.

## Khởi tạo một lần, tách khỏi deploy bình thường

Môi trường cần được khởi tạo hoàn chỉnh trước khi bật push tự động. Không chạy installer mỗi commit.

1. Chuẩn bị file local bị gitignore `deploy/config.cd.properties` với 4 key bên dưới; dùng credential Admin test do bạn chọn, không dùng dữ liệu cá nhân.
2. Chuẩn bị file local bị gitignore `deploy/.env.local` chứa `CD_DB_PASSWORD=<password DB CD>`; giữ password này nhất quán với DB persistent.
3. Chạy **một lần**:

```powershell
.\deploy\deploy.ps1 -Initialize
.\deploy\wait-ready.ps1
```

Bootstrap dùng CLI có sẵn trong image 5.9; CLI cũ này đã deprecated upstream, chỉ dùng one-time trên image pin, không xây migration framework quanh nó. Đã tắt registration consent. Nếu config thiếu nhưng database có bảng, script từ chối cài đè. Không tự drop/reset schema; trường hợp cài dở cần kiểm tra và được cho phép phục hồi một lần.

Deploy bình thường:

```powershell
.\deploy\deploy.ps1
.\deploy\wait-ready.ps1
```

Bước check yêu cầu config trỏ đúng DB CD và có user trong DB. Readiness yêu cầu HTTP 200 và marker `<auth-login`, không chỉ container Up. Sau đó Login smoke xác minh credential và Dashboard thực tế.

## GitHub setup

Runner Windows cần labels `self-hosted`, `Windows`, `X64`, `orangehrm-local`; Maven, Chrome, Docker Compose phải hoạt động. Máy cần bật, Engine running và runner online. Chạy runner ở thư mục riêng, không dùng checkout DEV làm runner workspace.

Tạo 2 repository Secrets:

- `ORANGEHRM_CD_CONFIG`: nội dung config riêng CD, tối thiểu:

```properties
app.url=http://localhost:8081/web/index.php/auth/login
browser=chrome
username=<Admin CD đã khởi tạo>
password=<password Admin CD>
```

- `ORANGEHRM_CD_DB_PASSWORD`: đúng password DB đã dùng lúc initialization (chỉ giá trị password, không kèm `CD_DB_PASSWORD=`).

Không commit file credentials hoặc dán password vào workflow. Workflow ghi config CD vào file riêng rồi xóa sau job; không sửa config DEV. Thay credential trong Secret không tự đổi password đã lưu trong DB; smoke sẽ FAIL nếu không khớp.

Workflow file phải được push lên main; sau đó các push/merge main tự kích hoạt deploy, không cần nút phê duyệt deploy. Chỉ chạy source đáng tin cậy trên runner cá nhân; ưu tiên repo private.

## Smoke hiện có, không viết lại Login

```powershell
$env:HEADLESS = 'true'
mvn -B -ntp test -Pcd-smoke "-Dconfig.file=deploy/config.cd.properties"
.\deploy\smoke-report.ps1
Remove-Item Env:HEADLESS
```

Profile `cd-smoke` chọn đúng `LoginTest.loginSuccess` (OHR1) và đăng ký lại TestListener hiện có vì lệnh chọn method không lấy listener trong XML. Config chỉ bổ sung tùy chọn system property `config.file`; không truyền thì vẫn đọc `config.properties` như trước.

Validator bắt buộc đúng 1 test OHR1, 1 PASS, 0 FAIL, 0 SKIP. Không có report hoặc không chạy test đều FAIL. OHR3 quên mật khẩu vẫn giữ trong source, không thuộc smoke này. HEADLESS chỉ bật trên CI hoặc khi bạn tự đặt biến local.

GitHub dùng `clean test` trên checkout runner riêng. Chạy ở checkout DEV có Allure Docker mount `target/allure-results` có thể lỗi clean; không dừng DEV để khắc phục, dùng lệnh không clean như trên.

## Report

- Report đầy đủ giữ local: `target/surefire-reports`, `target/allure-results` (gồm screenshot nếu test lỗi).
- Raw log giữ local: `ci-deploy.log`, `ci-smoke.log`; không upload vì có thể chứa dữ liệu nhạy cảm.
- Artifact GitHub `cd-smoke-<run number>` chỉ gồm `smoke-summary.json`: tên test cố định, URL CD, số PASS/FAIL/SKIP và kết quả suy ra từ report thật.
- Không upload raw XML/HTML, Allure JSON, screenshot hoặc config trong phase này vì chưa kiểm chứng toàn bộ nội dung. Đây là allowlist nhỏ, không phải sanitizer phức tạp.
- Chuẩn bị summary và upload dùng `if: always()` nên vẫn có bằng chứng khi fail. Nếu CI fail trước job CD thì job CD bị skip, không có report smoke.
- Lưu report muốn giữ ra ngoài runner workspace trước lần chạy tiếp theo; checkout/clean sẽ dọn dữ liệu cũ.

## Phase sau

Chỉ sau khi minimal pipeline ổn định mới thêm regression sau smoke bằng cơ chế `mvn test -Dmodule=...` hiện có. Chuẩn bị fixture CD và giữ report smoke/regression ở thư mục riêng để không ghi đè.

Chưa bật API: TestBaseAPI/OAuthTokenManager còn URL và client DEV; phải chuyển sang config CD, đăng ký OAuth client CD trước. Không để UI chạy 8081 nhưng API chạm 8080. Trang reset password còn URL demo công khai cũng cần xử lý trước khi mở rộng toàn bộ Login.

## Kết quả xác minh

Ngày 28/10/2026, đã chạy thực tế local:

| Bước | Kết quả |
|---|---|
| CI command `clean test-compile -DskipTests` | PASS trên bản copy source trong target/ci-validation, không vướng mount Allure DEV |
| One-time initialization | PASS; migration tới 5.9, tạo Admin và Conf.php |
| Normal deploy sau initialization | PASS; web recreate từ image pin, không installer lại, DB còn 1 Admin/1 employee fixture |
| Readiness | PASS, Login CD HTTP 200 và đúng marker |
| OHR1 Chrome headless | 1 PASS, 0 FAIL, 0 SKIP |
| Validator | Từ chối đúng trường hợp không có test hoặc test SKIP |
| DEV 8080 | HTTP 200 Login; IDs, trạng thái và mounts của 3 container DEV không đổi |
| Regression/API | NOT ENABLED YET |

Report local: `target/surefire-reports`, `target/allure-results`, `target/cd-evidence/smoke-summary.json`. TestNG ghi total=3 vì có 2 method bị loại khỏi lựa chọn smoke; validator đếm đúng 1 method thực sự chạy, không đánh đồng excluded với skipped.

Schema CD cài dở trước đó được kiểm tra 0 user/0 employee, dump local trước và chỉ khởi tạo lại sau khi người dùng cho phép. Không đưa thao tác DROP vào deploy script/pipeline; các lần push bình thường không reset DB.

Chưa push/dispatch workflow mới lên GitHub trong phiên này, nên chưa có URL run hoặc xác nhận upload artifact thực tế. Các kết quả trên là kiểm tra local các bước của pipeline. Cần đặt đủ 2 Secrets trên GitHub trước khi push main.
