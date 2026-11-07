# Đề Cương Báo Cáo Đồ Án Tốt Nghiệp & Cẩm Nang Bảo Vệ Đồ Án
**Đề tài:** Xây dựng Framework Kiểm thử Tự động đa tầng (UI, API, Security) tích hợp CI/CD cho hệ thống Quản trị Nhân sự OrangeHRM 5.9.

---

## Phần 1: Thống kê Bộ Testcase Hoàn chỉnh (`OHR1` – `OHR76`)

| Phân tầng | Module | Số hàm `@Test` | Số lượt chạy (kể cả DataProvider) | Mã Testcase | Ghi chú chính |
|---|---|---:|---:|---|---|
| **1. UI Functional** | `Login` | 3 | 8 | `OHR1–OHR3` | `OHR2` đọc 6 dòng từ `LoginTestData.xlsx` |
| **1. UI Functional** | `Admin` | 15 | 18 | `OHR4–OHR18` | CRUD User, Job Title, Location, Skill |
| **1. UI Functional** | `PIM` | 10 | 13 | `OHR19–25`, `OHR67–69` | `OHR23` đọc 4 dòng từ `EmployeeSearchTestData.xlsx`; `OHR67–69` (`CDPIMTest`) |
| **1. UI Functional** | `Recruitment` | 8 | 10 | `OHR26–31`, `OHR70–71` | `OHR27` đọc 4 dòng từ `CandidatesTestData.xlsx` |
| **1. UI Functional** | `My Info` | 6 | 6 | `OHR32–OHR37` | Contact, Personal, Emergency Contact, Dependent |
| **1. UI Functional** | `Performance` | 3 | 3 | `OHR38–OHR40` | Add Review -> Write Review -> Delete Review |
| **2. Bug Regression** | `GitHub Issues` | 10 | 12 | `OHR41–OHR50` | 7 GitHub Issues thực tế: `#1876`, `#1928`, `#1897`, `#1923`, `#1926`, `#1890`, `#1787`, `#1820` |
| **3. API Testing** | `PIMAPI` | 8 | 8 | `OHR56–62`, `OHR75` | GET list/detail/filter/pagination/404 + POST/PUT/DELETE |
| **3. API Testing** | `AdminAPI` | 4 | 4 | `OHR63`, `OHR72–74` | GET users, filter user by username, GET job-titles, GET locations |
| **3. API Testing** | `RecruitmentAPI` | 4 | 9 | `OHR64–66`, `OHR76` | Required fields (3), Email format (2), Boundary 30/31 chars (3), GET lists (1) |
| **4. Security API** | `Security` | 5 | 9 | `OHR51–OHR55` | AuthN (3), AuthZ/IDOR ESS vs Admin (3), SQLi/XSS Injection (3) |
| **TỔNG CỘNG** | **Toàn bộ Project** | **76 `@Test`** | **100 lượt thực thi** | **`OHR1` – `OHR76`** | **Đạt chuẩn 4 tầng kiểm thử + CI/CD Docker** |

---

## Phần 2: Khung Chương Mục Báo Cáo Đồ Án Tốt Nghiệp (Chuẩn Đại học VN)

### Chương 1: Tổng quan Đề tài và Hệ thống Kiểm thử
1. **Lý do chọn đề tài**: Nhu cầu tự động hóa kiểm thử hồi quy (Regression Testing) trong quy trình DevOps/CI-CD hiện đại và bài toán kết hợp kiểm thử đa tầng (UI + API + Security).
2. **Mục tiêu và phạm vi đề tài**:
   - Đối tượng kiểm thử (SUT): Hệ thống mã nguồn mở **OrangeHRM OS 5.9** triển khai trên Docker + MariaDB 10.4.
   - Phạm vi kiểm thử: 8 phân hệ UI (`Login`, `Admin`, `PIM`, `Recruitment`, `My Info`, `Performance`, `Time`, `Claim`), 3 phân hệ REST API (`PIM`, `Admin`, `Recruitment`), và Kiểm thử bảo mật API (`Authentication`, `Authorization/IDOR`, `Injection`).
3. **Đóng góp thực tiễn của đồ án**:
   - Xây dựng thư viện [`componentLocator`](../src/main/java/com/hrm/Util/componentLocator.java) và [`TestUtil`](../src/test/java/com/hrm/Base/TestUtil.java) giúp rút ngắn 60–70% số dòng code trong Page Object.
   - Tái hiện và kiểm thử hồi quy 7 lỗi thực tế từ GitHub Issues của OrangeHRM (`#1787`, `#1820`, `#1876`, `#1890`, `#1897`, `#1923`, `#1926`, `#1928`).
   - Xây dựng môi trường CD độc lập trên cổng `8081` có cơ chế tự động cài đặt, seed dữ liệu idempotent và dọn dẹp dữ liệu sau khi chạy.

### Chương 2: Cơ sở Lý thuyết và Công nghệ Sử dụng
1. **Lý thuyết Kiểm thử Phần mềm**:
   - Mô hình Kim tự tháp kiểm thử (Test Pyramid): Kết hợp UI Test, API Test (Boundary Value Analysis, Equivalence Partitioning) và Security Test (OWASP API Security: Broken Authentication, Broken Object Level Authorization / IDOR, Injection).
   - Kiểm thử hướng dữ liệu (Data-Driven Testing - DDT) với Apache POI và `@DataProvider`.
2. **Kiến trúc Page Object Model (POM) kết hợp Component-Driven**:
   - Giải thích cách chuẩn hóa các thành phần Vue/OXD của OrangeHRM (`oxd-input-group`, `oxd-select-text`, `oxd-table-card`, `oxd-toast`) thành các hàm dùng chung trong [`componentLocator`](../src/main/java/com/hrm/Util/componentLocator.java) và [`waitUtils`](../src/main/java/com/hrm/Util/waitUtils.java).
3. **Công nghệ sử dụng**:
   - Java 21, Maven, Selenium WebDriver 4.48, TestNG 7.12, RestAssured 6.0.1, OAuth2 Authorization Code + PKCE (SHA-256), Apache POI 5.2.5, Log4j2, Allure Report 2.29, Docker Compose, GitHub Actions.

### Chương 3: Thiết kế Kiến trúc Automation Framework
1. **Cấu trúc thư mục và phân tầng kiến trúc**:
   - Tầng Core & Config: [`TestBase`](../src/main/java/com/hrm/Base/TestBase.java), [`TestBaseAPI`](../src/main/java/com/hrm/Base/TestBaseAPI.java), [`WebDriverFactory`](../src/main/java/com/hrm/Util/WebDriverFactory.java), [`Config`](../src/main/java/com/hrm/Util/Config.java), [`TestConfig`](../src/main/java/com/hrm/Util/TestConfig.java), [`TestDataShare`](../src/main/java/com/hrm/Util/TestDataShare.java).
   - Tầng Utility & Listener: [`componentLocator`](../src/main/java/com/hrm/Util/componentLocator.java), [`waitUtils`](../src/main/java/com/hrm/Util/waitUtils.java), [`ExelReader`](../src/main/java/com/hrm/Util/ExelReader.java), [`OAuthTokenManager`](../src/main/java/com/hrm/Util/OAuthTokenManager.java), [`TestListener`](../src/main/java/com/hrm/Util/TestListener.java), [`Retry`](../src/main/java/com/hrm/Util/Retry.java).
   - Tầng Page Objects (`src/main/java/com/hrm/Pages/**`) và Helper điều hướng [`TestUtil`](../src/test/java/com/hrm/Base/TestUtil.java).
   - Tầng Test Cases (`src/test/java/com/hrm/TestCase/**`) và Setup/Seed (`src/test/java/com/hrm/Setup/**`).
2. **Sơ đồ luồng hoạt động**:
   - Luồng UI Test: `testng.xml` -> `TestBase (@BeforeMethod)` -> `TestUtil` -> `Page Object` -> `componentLocator` + `waitUtils` -> `TestListener (Allure Screenshot + Log)`.
   - Luồng API & Security Test: `testng.xml` -> `TestBaseAPI (@BeforeClass)` -> `OAuthTokenManager (PKCE S256)` -> `RestAssured` -> `Cleanup (@AfterMethod)`.

### Chương 4: Thiết kế Kịch bản Kiểm thử và Quản lý Dữ liệu Kiểm thử (Test Data)
1. **Thiết kế Testcase UI theo từng phân hệ**: Trình bày bảng testcase `OHR1–OHR40` và `OHR67–OHR71`.
2. **Phân tích và Kiểm thử Hồi quy từ GitHub Issues (`OHR41–OHR50`)**:
   - Phân tích nguyên nhân gốc rễ (Root Cause) trên source code OrangeHRM 5.9 cho 4 lỗi thực tế vẫn còn tồn tại trên bản 5.9 (`#1876`, `#1897`, `#1923`, `#1928`) và các kịch bản đối chứng (`#1926`, `#1890`, `#1787`, `#1820`).
3. **Thiết kế Testcase API & Security (`OHR51–OHR66`, `OHR72–OHR76`)**:
   - Giải thích cách tự động lấy Bearer Token qua OAuth2 PKCE và cách thiết kế các ca kiểm thử phân quyền IDOR giữa `ESS` và `Admin`.
4. **Chiến lược quản lý Test Data 3 lớp**:
   - Lớp Baseline Idempotent (`CDSeedTest` -> `CDPerformanceSeedTest`).
   - Lớp Dữ liệu chuỗi theo lượt chạy (`TestDataShare`).
   - Lớp Dọn dẹp tự động (`@AfterMethod` / `@AfterTest`).

### Chương 5: Triển khai Pipeline CI/CD và Đánh giá Kết quả Thực nghiệm
1. **Kiến trúc tách biệt môi trường DEV (`8080`) và CD (`8081`)**:
   - Trình bày cách cô lập container, port, database (`orangehrm_cd`) và Docker volume.
2. **Quy trình CI/CD tự động trên GitHub Actions**:
   - Job `ci` (compile trên `ubuntu-latest`) -> Job `deploy-verify` (Self-hosted Windows runner: kiểm tra trạng thái DEV -> Deploy CD 8081 -> Wait Ready -> Seed Data -> Smoke & Regression Suites -> Publish Allure Report -> Kiểm chứng DEV không bị ảnh hưởng).
3. **Phân tích kết quả thực nghiệm**:
   - Thống kê tỷ lệ Pass/Fail, phân tích các bug thực tế phát hiện được qua bộ test regression và biểu đồ Allure Report.

### Chương 6: Kết luận và Hướng phát triển
- Tổng kết kết quả đạt được so với mục tiêu ban đầu.
- Hướng mở rộng: Chạy song song (`ThreadLocal<WebDriver>`) trên Selenium Grid, tích hợp kiểm thử hiệu năng (JMeter/k6).

---

## Phần 3: Giải thích Ngắn gọn các File Vừa Hoàn Thiện (Để bạn nắm vững 100%)

1. **[`AddReviewPage.java`](../src/main/java/com/hrm/Pages/PerformancePage/AddReviewPage.java) & [`ManageReviewPage.java`](../src/main/java/com/hrm/Pages/PerformancePage/ManageReviewPage.java)**:
   - Sửa lỗi gõ từ khóa tìm Supervisor (`keySupervisor` lấy từ tên `supervisor` thay vì lấy nhầm từ tên `employee`).
   - Thêm các hàm ngắn gọn gọi trực tiếp `componentLocator` và `waitUtils`: `isDeleteReviewSuccess()`, `countReviews()`, `openKpiPage()`, `countKpis()`, `addKpi()`.
2. **[`AddReviewFunctionality.java`](../src/test/java/com/hrm/TestCase/PerformanceTab/AddReviewFunctionality.java), [`WriteReviewFunctionality.java`](../src/test/java/com/hrm/TestCase/PerformanceTab/WriteReviewFunctionality.java), [`DeleteReviewFunctionality.java`](../src/test/java/com/hrm/TestCase/PerformanceTab/DeleteReviewFunctionality.java)**:
   - Bổ sung `groups = "performance-add"` và `dependsOnGroups = "performance-add"` giống hệt cách làm ở `Admin`/`PIM` để khi Add Review lỗi thì Write/Delete sẽ `SKIP` rõ ràng.
   - Bổ sung `Assert.assertTrue(manageReviewPage.isDeleteReviewSuccess())` và kiểm tra số dòng giảm sau khi xóa ở `OHR40`.
3. **[`CDPerformanceSeedTest.java`](../src/test/java/com/hrm/Setup/CDPerformanceSeedTest.java) & [`CDPerformanceSetup.java`](../src/test/java/com/hrm/Setup/CDPerformanceSetup.java)**:
   - Tự động gán Job Title (`AUTO CD Job`) và Supervisor (`CD Tester`) cho nhân viên `AUTO SeedEmployee`, đồng thời tạo 2 KPI trên CD `8081` để 3 testcase `OHR38–40` chạy mượt trên CD.
4. **[`TimesheetPage.java`](../src/main/java/com/hrm/Pages/TimePage/TimesheetPage.java) & [`ClaimPage.java`](../src/main/java/com/hrm/Pages/ClaimPage/ClaimPage.java)**:
   - Dùng `componentLocator` để tự động tạo Customer/Project/Activity (`ISS1890`) và Event/Claim nháp (`ISS1820`) nếu chạy trên môi trường CD mới chưa có sẵn fixture, không làm ảnh hưởng dữ liệu đã có trên DEV.
5. **Bổ sung 5 testcase API (`OHR72–OHR76`)**:
   - [`GetUserListAPITest.java`](../src/test/java/com/hrm/TestCase/TestAPI/AdminAPI/GetUserListAPITest.java): thêm `OHR72` (lọc user theo username), `OHR73` (lấy danh sách Job Titles), `OHR74` (lấy danh sách Locations).
   - [`APIGetEmployeeListTest.java`](../src/test/java/com/hrm/TestCase/TestAPI/PIMAPI/APIGetEmployeeListTest.java): hoàn thiện class (trước đây bị bỏ trống) với `OHR75` (kiểm tra phân trang `limit` & `offset`).
   - [`APICandidateRequiredFieldTest.java`](../src/test/java/com/hrm/TestCase/TestAPI/RecruitmentAPI/APICandidateRequiredFieldTest.java): thêm `OHR76` (lấy danh sách Candidates & Vacancies qua API).
