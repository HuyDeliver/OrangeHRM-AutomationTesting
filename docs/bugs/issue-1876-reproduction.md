# Issue #1876 — Include hiển thị Past nhưng tìm Current sau khi nhấn Tab

> Cách chạy hiện tại (26/10/2026): `mvn test "-Dmodule=PIM"` qua `testng.xml`; lệnh này chạy toàn bộ module đã chọn, gồm cả issue. Các tên XML/lệnh riêng và số liệu bên dưới là lịch sử trước khi gộp. Xem [hướng dẫn module](../test-execution.md).

**Trạng thái: REPRODUCED trên local 5.9, ngày 17/10/2026.** Lặp lại từ trang mới: thao tác Tab gặp lỗi **2/2 lần**; thao tác Enter đối chứng đúng **1/1 lần**.

Issue tham chiếu: [#1876 — Select component does not update model when TAB key is pressed](https://github.com/orangehrm/orangehrm/issues/1876), reporter ghi version **5.7**. Môi trường thực tế dưới đây là **5.9**, vì vậy kết quả này không được ghi thành reproduce trên 5.7.

## Môi trường và dữ liệu đối chứng

- OrangeHRM OS **5.9**, kiểm tra từ giao diện và source container đang chạy.
- Chrome **155.0.8059.80**, đăng nhập bằng Admin.
- Màn hình: `http://localhost:8080/web/index.php/pim/viewEmployeeList`.
- API: `GET http://localhost:8080/web/index.php/api/v2/pim/employees`.
- Đọc API với `includeEmployees=onlyCurrent`: HTTP 200, `meta.total=3`; ba employee có `terminationId=null`.
- Đọc API với `includeEmployees=onlyPast`: HTTP 200, `meta.total=0`, `data=[]`.

Không tạo employee hoặc termination record. Không có employee đã nghỉ vẫn là dữ liệu đối chứng hợp lệ: chọn Past phải gửi `onlyPast` và trả danh sách rỗng đã được xác minh.

## Các bước tái hiện

1. Mở mới Employee List, giữ Include mặc định **Current Employees Only**.
2. Click dropdown Employment Status, nhấn `Escape`, sau đó nhấn `Tab` để focus vào Include.
3. Nhấn `ArrowDown` ba lần, kiểm tra Include hiển thị **Past Employees Only**.
4. Nhấn `Tab` để rời Include, rồi click **Search**.
5. Ghi lại nhãn Include, request, response và bảng kết quả.
6. Mở lại Employee List để reset trạng thái. Lặp lại bước 2–3 nhưng dùng `Enter` thay cho `Tab` để xác nhận lựa chọn, rồi click **Search**.

Không click trực tiếp option Past trong dropdown: thao tác đó sẽ không kiểm tra luồng chọn bằng bàn phím mà issue mô tả.

## Expected và Actual

**Expected:** Khi Include hiển thị **Past Employees Only**, request Search phải gửi `includeEmployees=onlyPast`. Với dữ liệu hiện tại, kết quả đúng là HTTP 200, `meta.total=0`, không có employee trong bảng.

| Quan sát | Chọn rồi Tab | Chọn rồi Enter — đối chứng |
|---|---|---|
| Nhãn Include | Past Employees Only | Past Employees Only |
| Query thực tế | `includeEmployees=onlyCurrent` | `includeEmployees=onlyPast` |
| HTTP status | 200 | 200 |
| `meta.total` | 3 | 0 |
| `empNumber` trong response | 1, 7, 6; đều `terminationId=null` | Không có |
| Bảng giao diện | 3 dòng, Employee ID 0001, 0004, 0003 | 0 dòng, No Records Found |

`empNumber` là ID nội bộ từ API, khác Employee ID hiển thị trên bảng. Các ID trong báo cáo chỉ là dữ liệu đã quan sát, không phải giá trị cần hard-code vào test sau này.

**Kết luận:** Đã tái hiện sự không khớp giữa lựa chọn hiển thị và filter được gửi khi dùng Tab trên local 5.9. Nhánh Enter gửi đúng filter và trả đúng dữ liệu đối chứng. HTTP 200 không có nghĩa chức năng đúng: vấn đề nằm ở filter của request.

Chưa xác định nguyên nhân kỹ thuật cuối cùng trong component. Kết quả không chứng minh mọi build đều gặp lỗi và không đủ cơ sở gọi đây là lỗ hổng bảo mật. Kiểm tra lần này dùng lệnh Selenium chẩn đoán qua JShell; chưa tạo class `@Test` hoặc sửa source framework.

## Bằng chứng

- [Baseline API](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1876-20261017/baseline.json): version, thời gian và hai bộ lọc đối chứng.
- [Request/response Tab, Enter và Tab lặp lại](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1876-20261017/network.json): method, URL, status, total và employee IDs; không chứa token/cookie.
- [UI sau Tab](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1876-20261017/tab-result.json) và [ảnh sau Tab](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1876-20261017/tab-result.png).
- [UI sau Enter](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1876-20261017/enter-result.json) và [ảnh sau Enter](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1876-20261017/enter-result.png).
- [UI sau lần Tab thứ hai](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1876-20261017/tab-repeat-result.json): nhãn vẫn Past, bảng vẫn có 3 nhân viên; request tương ứng là `TAB_REPEAT` trong network JSON.

Các file bằng chứng được lưu cùng báo cáo trong `docs/bugs/evidence/issue-1876-20261017`, không phụ thuộc thư mục build `target`.

**Cleanup:** Không cần hoàn tác dữ liệu; các thao tác kiểm tra chỉ đọc danh sách và thay đổi filter trên giao diện.

## Regression automation

Đã bổ sung [Issue1876RegressionTest.java](E:/Studying/GraduateProject/orangehrmtesting/src/test/java/com/hrm/TestCase/PIMTab/Issue1876RegressionTest.java) và suite chạy riêng `testng_issue1876.xml` (suite lịch sử, đã gộp). Phần chẩn đoán và bằng chứng phía trên là kết quả trước khi tạo class này; không được dùng thay cho kết quả chạy TestNG mới.

Class ghi đúng nguồn gốc: “Regression test based on GitHub Issue #1876, originally reported on OrangeHRM 5.7 and reproduced on local OrangeHRM 5.9.” Đây là regression dựa trên issue đã có, không ghi bug do project tự phát hiện.

Chạy riêng hai testcase:

```powershell
mvn -o "-Dtestng.suite=testng_issue1876.xml" test
```

Compile bằng `mvn -o -DskipTests test-compile`: **BUILD SUCCESS**. Lần xác minh sau khi rút gọn và dùng component hiện có hoàn tất lúc **20:29:28 ngày 17/10/2026 (Asia/Saigon)** trên local **5.9**:

| Testcase | UI label | Expected Past count — baseline động | Actual count | TestNG | Bug reproduced |
|---|---|---|---|---|---|
| `includeFilterShouldApplyPastEmployeesWhenLeavingWithTab` | Past Employees Only | 1 | 3 | **FAIL** | **YES** |
| `includeFilterShouldApplyPastEmployeesWhenConfirmingWithEnter` | Past Employees Only | 1 | 1 | **PASS** | Không xuất hiện trong luồng Enter |

Tổng: **2 test, 1 PASS, 1 FAIL, 0 ERROR, 0 SKIP**. Cả hai testcase tự đo được baseline Current=3 và Past=1; số liệu này là kết quả quan sát, không được hard-code trong assertion. Dữ liệu Past hiện tại khác lần chẩn đoán trước (0); giữ nguyên bằng chứng lịch sử phía trên. Test ENTER vẫn chạy sau khi TAB FAIL và dùng browser/baseline riêng.

Maven kết thúc với `BUILD FAILURE` do đúng assertion của TAB: `expected [1] but found [3]`. Giữ nguyên assertion. **Regression testcase correctly detected Issue #1876.**

Bằng chứng chạy tự động: [kết quả trích từ Allure](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1876-20261017/regression-results.json) và [ảnh FAIL do listener chụp](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1876-20261017/regression-tab-failure.png). Allure raw results ghi một `failed`, một `passed`; ảnh được attach đúng `image/png`. Chưa tạo báo cáo HTML trong lần này.

### Cấu trúc đơn giản

- Class extends `TestBase`. Mỗi testcase có browser mới nhờ `@BeforeMethod` của base, sau đó login Admin và mở PIM bằng `TestUtil.employeeUtil()`. `TestBase` đóng browser sau mỗi testcase.
- Credentials đọc từ config hiện có. Chỉ bỏ giá trị password khỏi dòng log trong `LoginPage`; không thay đổi cách đăng nhập.
- Hai method `@Test` độc lập, không dùng `dependsOnMethods`. Suite chỉ gắn `TestListener`, không gắn `RetryListener` để tránh tự chạy lại testcase đang phát hiện defect.
- Gọi trực tiếp `componentLocator.clickSearch(driver)` để bấm Search, bỏ import/field/khởi tạo `EmployeeListPage` chỉ làm nhiệm vụ chuyển tiếp lời gọi này. `TestUtil.employeeUtil()` vẫn phụ trách login và vào PIM. Giữ nguyên source của `componentLocator` và `TestUtil`.
- `@BeforeMethod prepareEmployeeList()` tự lấy baseline Current rồi Past bằng ENTER trước mỗi testcase, sau đó reset trang. Hai field `currentCount`/`expectedPastCount` được gán lại cho mỗi test; không phụ thuộc kết quả test trước. Bỏ phần chuẩn bị trùng lặp trong hai test và helper `getPastEmployeeCountUsingEnter()`.
- Hai component hiện có chưa có hàm chọn bằng bàn phím, đọc tổng kết quả hoặc chờ summary mới. Giữ ba helper nhỏ bên dưới cho các thao tác đó. `chooseSelect()`/`chooseSelectAction()` chọn option bằng chuột nên không thay thế được keyboard flow; `checkTable()`/`checkSearch()` trả boolean khi tìm thấy dòng phù hợp, không trả tổng kết quả. Không viết lại component hoặc tạo utility mới.
- Không tạo, sửa, terminate hoặc xóa employee. Không cần cleanup dữ liệu.

Chỉ còn ba helper nhỏ:

| Helper | Công việc |
|---|---|
| `openEmployeeList()` | Refresh trang, xác nhận Include về Current và chờ summary xuất hiện. |
| `selectPastByKeyboard()` | Click Employment Status, gửi Escape → Tab, rồi ArrowDown ba lần trên Include. |
| `searchAndGetResultCount()` | Bấm Search, chờ summary cũ bị thay, đọc tổng kết quả mới. |

### Giải thích cho Junior

1. **Arrange làm gì?** Trước mỗi test, `@BeforeMethod` tự mở trang mới và Search Current để lấy `currentCount`, rồi lấy `expectedPastCount` bằng luồng Past → Enter → Search. Tiếp theo reset trang để bắt đầu thao tác cần kiểm tra từ Current. Hai test dùng chung cách chuẩn bị, nhưng mỗi test đo lại baseline của riêng nó. Không hard-code tổng ba employee hoặc employee ID.
2. **Gửi những phím nào?** Selenium click Employment Status, gửi `Keys.ESCAPE`, `Keys.TAB` để đi tới Include, rồi gửi ba `Keys.ARROW_DOWN`. Test assert nhãn đã là `Past Employees Only` trước khi gửi phím xác nhận.
3. **Tab khác Enter ở đâu?** Cả hai test chọn cùng nhãn Past. Test defect gửi `Keys.TAB` để rời dropdown; test control gửi `Keys.ENTER` để xác nhận lựa chọn. Sau đó cả hai cùng click Search.
4. **Đọc UI label thế nào?** `include.getText()` đọc chữ hiển thị trên Include. Assertion kiểm tra nhãn Past trước khi xác nhận; sau Search kiểm tra lại nhãn để tránh nhận nhầm một luồng đã đổi lựa chọn.
5. **Lấy result count thế nào?** Đọc span summary đang hiển thị `(N) Records Found`, lấy phần số và chuyển thành `int`. Nếu text là `No Records Found`, trả `0`. Đây là tổng kết quả từ header, không chỉ số dòng của trang đầu nên không bị giới hạn bởi pagination. Khi Search chạy, local `TableHeader.vue` dùng `v-if="!loading"` để gỡ summary cũ; test dùng `stalenessOf(previousResult)` rồi chờ summary mới, tránh đọc count của request trước.
6. **Assertion so sánh gì?** Test TAB so sánh `actualTabCount` với `expectedPastCount` lấy bằng Enter. Test ENTER tự chuẩn bị baseline riêng rồi so sánh `actualEnterCount` với baseline đó. Nếu Current và Past bằng nhau, test TAB ghi SKIP với lý do không đủ dữ liệu phân biệt hai bộ lọc bằng count; không tính trường hợp này là PASS.
7. **Tại sao TAB có thể FAIL?** Nếu bug còn tồn tại như chẩn đoán trước, UI hiện Past nhưng kết quả vẫn theo Current; khi hai baseline khác nhau, actual TAB sẽ khác expected Past và assertion FAIL. Khi chạy mới phải ghi số liệu thực tế; không sửa expected để ép PASS. Count khác nhau là bằng chứng sai hành vi filter trong điều kiện đối chứng, chưa phải phân tích nguyên nhân kỹ thuật hoặc lỗ hổng bảo mật.
8. **Tại sao ENTER có thể PASS?** Nếu Enter vẫn xác nhận lựa chọn đúng như lần chẩn đoán, Search sau Enter sẽ cho tổng giống baseline Past của chính testcase đó. Hai lần Enter bằng nhau là phép kiểm tra đối chứng lặp lại; không thay thế kiểm tra đầy đủ mọi business rule của danh sách employee.
9. **Regression có tác dụng gì?** Khi bug còn, test TAB phải phát hiện sai lệch và FAIL. Sau khi OrangeHRM sửa bug, cùng thao tác TAB trả kết quả giống Enter baseline thì test tự PASS. ENTER control giúp phân biệt lỗi riêng của Tab với lỗi chung của dropdown/filter.

### Log và screenshot

Log ghi version local thực tế, module, lựa chọn Past, phím TAB/ENTER, baseline Current/Past, actual count và kết quả. Không ghi password, token, cookie hoặc authorization code.

Khi testcase TAB FAIL, `TestListener.onTestFailure()` hiện có gọi `TestBase.takeScreenshot()` để attach ảnh vào Allure trước khi `@AfterMethod` đóng browser. Chỉ sửa metadata của helper sẵn có: tên ảnh lấy từ `result.getName()` và định dạng `image/png`/`.png`, thay cho tên null và thiếu loại ảnh. Không thêm screenshot framework mới. TestNG FAIL do assertion phát hiện bug là kết quả regression hợp lệ; dòng “Regression testcase correctly detected Issue #1876” được đối chiếu với số liệu chạy thật phía trên.
