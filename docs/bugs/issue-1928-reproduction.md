# Issue #1928 — Xóa Education báo Records Not Found dù bản ghi tồn tại

> Cách chạy hiện tại (26/10/2026): `mvn test "-Dmodule=MyInfo"` qua `testng.xml`; lệnh này chạy toàn bộ module đã chọn, gồm cả issue. Các tên XML/lệnh riêng và số liệu bên dưới là lịch sử trước khi gộp. Xem [hướng dẫn module](../test-execution.md).

**Đã tái hiện trên OrangeHRM OS 5.9 local ngày 17/10/2026.** Trong phép thử chẩn đoán lịch sử, UI gửi DELETE cho bản ghi Education đang tồn tại nhưng API trả **404 — Records Not Found**. Đọc lại API và refresh giao diện đều xác nhận bản ghi chưa bị xóa.

**Từ ngày 18/10/2026, regression chuyển sang Selenium/POM/TestNG/Log4j thuần UI theo yêu cầu mới.** Không dùng Rest Assured, không tạo hoặc xóa danh mục Education Level. Các request, ID, baseline rỗng và cleanup API dưới đây thuộc lần chẩn đoán ngày 17/10, không mô tả trạng thái dữ liệu hoặc cách chạy của regression hiện tại.

Issue tham chiếu: [#1928](https://github.com/orangehrm/orangehrm/issues/1928), được báo trên **5.8**. Kết quả dưới đây thuộc bản local **5.9**, không được ghi thành kiểm thử trên 5.8. Đây là lỗi chức năng xóa dữ liệu; chưa có bằng chứng để kết luận là lỗ hổng bảo mật.

## Chẩn đoán lịch sử 17/10 — môi trường và dữ liệu

- Màn hình: **My Info → Qualifications → Education**.
- Đăng nhập Admin bằng config của project; employee đang đăng nhập có `empNumber=1` trong lần quan sát này.
- API danh mục Education: `/web/index.php/api/v2/admin/educations`.
- API Education của employee: `/web/index.php/api/v2/pim/employees/{empNumber}/educations`.
- Baseline trước thử nghiệm: danh mục Education và danh sách Education của employee đều rỗng.
- Tạo một Education level tạm có tên `ISS1928_1792247546318`, API trả level ID `2`.
- Hai bản ghi A và B được thêm lần lượt bằng UI, dùng trường **GPA/Score** để phân biệt: marker kết thúc `_A` và `_B`.

Các ID ở đây là dữ liệu quan sát trong lần chẩn đoán, không phải giá trị cần hard-code vào regression. Education level ID, employee number và ID bản ghi Education là ba loại ID khác nhau.

## Các bước tái hiện và đối chứng

1. Đăng nhập, mở **My Info → Qualifications**.
2. Tạo Education A với level và institute là tên test riêng; GPA/Score kết thúc `_A`.
3. Đọc API để xác nhận A tồn tại. Lần này A có record ID `1`.
4. Xóa A bằng biểu tượng Delete trên UI và xác nhận trong dialog. Ghi nhận request và đọc lại danh sách.
5. Tạo Education B tương tự, GPA/Score kết thúc `_B`. Đọc API xác nhận B tồn tại; lần này B có record ID `2`.
6. Xóa B bằng đúng thao tác UI như bước 4.
7. Ghi nhận toast, request/response, đọc lại API rồi refresh giao diện để kiểm tra dữ liệu thực tế.

| Quan sát | Education A — đối chứng | Education B — tái hiện lỗi |
|---|---|---|
| Employee number | 1 | 1 |
| Record ID trước xóa | 1 | 2 |
| DELETE body do UI gửi | `{"ids":[1]}` | `{"ids":[2]}` |
| DELETE status | **200** | **404** |
| DELETE response | `data: [1]` | `Records Not Found` |
| GET sau thao tác | Danh sách rỗng | Bản ghi ID 2 vẫn tồn tại |
| UI | Xóa thành công | Toast `Error / Records Not Found`; bản ghi vẫn hiện sau refresh |

**Expected:** Bản ghi Education của chính employee đang đăng nhập đã được xác nhận tồn tại phải xóa được. Sau khi xóa thành công, GET không còn record ID đó và UI không hiển thị bản ghi sau refresh.

**Actual:** Với B, UI gửi đúng ID đã xác nhận tồn tại nhưng DELETE trả 404; GET vẫn trả bản ghi và refresh vẫn còn dòng tương ứng. Đây là bằng chứng lỗi thao tác xóa, không chỉ là thông báo toast sai.

Không phải mọi thao tác xóa Education đều lỗi: A đã xóa thành công. A có record ID trùng employee number, B thì khác. Source local `/var/www/html/src/plugins/orangehrmPimPlugin/Dao/EmployeeEducationDao.php`, method `getExistingEmpEducationIdsByEmpNumber()`, dòng 68 dùng điều kiện `employeeEducation.employee IN (:ids)` trong khi `ids` chứa ID bản ghi Education. Với B, employee number là 1 nhưng `ids=[2]`, nên bước kiểm tra không tìm được bản ghi để xóa. Đây là nguyên nhân phù hợp với cả kết quả lỗi và đối chứng A.

Không đổi ID trong request thành employee number để ép test PASS; việc đó không còn kiểm tra thao tác xóa bản ghi đã chọn. Framework chỉ kiểm tra hành vi, không sửa source OrangeHRM trong lần này.

## Bằng chứng chẩn đoán

Các JSON chỉ chứa request/response cần thiết; không lưu password, cookie hoặc token.

- [Setup và baseline](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1928-20261017/setup.json).
- [A trước xóa](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1928-20261017/before-delete-A.json), [network A](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1928-20261017/network-A.json), [GET sau xóa A](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1928-20261017/after-delete-A.json).
- [B trước xóa](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1928-20261017/before-delete-B.json), [network B có DELETE 404](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1928-20261017/network-B.json), [GET sau xóa B](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1928-20261017/after-delete-B.json).
- [Ảnh toast lỗi khi xóa B](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1928-20261017/delete-B-error.png), [ảnh B vẫn còn sau refresh](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1928-20261017/after-refresh-B.png).
- [Kết quả cleanup](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1928-20261017/cleanup.json), [ảnh sau cleanup](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1928-20261017/after-cleanup.png).

**Cleanup chẩn đoán:** Sau khi lưu bằng chứng, xóa đúng Education level tạm ID 2 do phép thử tạo qua Admin API, nhận 200. Local cascade xóa Education còn tham chiếu level đó; GET xác nhận cả danh mục Education và Education của employee trở lại rỗng như baseline. Không xóa employee, tài khoản hoặc dữ liệu có sẵn.

## Automation lịch sử 17/10 — bản có Rest Assured

Bản regression trước đã chạy lúc **21:44:49 ngày 17/10/2026**, compile thành công và có **1 test FAIL**: Education record ID 5 vẫn tồn tại sau thao tác xóa trên UI; GET trả 200, toast Delete success không xuất hiện. Cleanup level tạm qua API đã PASS. Bản đó không capture network DELETE nên không ghi 404 là status đo được trong lần chạy TestNG.

Giữ [kết quả Allure lịch sử](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1928-20261017/regression-results.json) và [ảnh FAIL lịch sử](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1928-20261017/regression-failure.png) làm bằng chứng ngày 17/10. **Kết quả này không thay thế kết quả chạy phiên bản thuần UI ngày 18/10.** Code hiện tại không tiếp tục cách setup, đọc ID hoặc cleanup API của bản trước.

## Regression hiện tại — Selenium và Page Object

[Issue1928RegressionTest.java](E:/Studying/GraduateProject/orangehrmtesting/src/test/java/com/hrm/TestCase/MyInfoTab/Issue1928RegressionTest.java) extends `TestBase`, chạy bằng `testng_issue1928.xml` (suite lịch sử, đã gộp). Dùng TestNG để kiểm tra kết quả, Log4j qua `Log` để ghi từng bước và `TestListener` hiện có để chụp ảnh khi FAIL.

```powershell
mvn -o "-Dtestng.suite=testng_issue1928.xml" test
```

Flow hiện tại chỉ thao tác trên giao diện:

1. `TestUtil.myInfoUtil()` đăng nhập và mở My Info bằng config hiện có.
2. `PersonalDetailPage.goToQualificationPage()` mở Qualifications; `QualificationPage` phụ trách form và bảng Education theo POM của project.
3. Chọn **Education Level có sẵn** từ `TestConfig.educationLevel`, điền institute và GPA/Score có marker riêng cho lần chạy, rồi Save.
4. Xác nhận dòng có marker đã xuất hiện trước khi Delete để tránh test PASS vì chưa từng tạo được bản ghi.
5. Xóa đúng dòng chứa marker và xác nhận dialog trên UI.
6. Refresh, chờ bảng tải xong rồi assert dòng có marker không còn.

Các level người dùng đã chuẩn bị gồm **HANUI, HUMG, HUST, NEU, UNETI**. Config chọn `HANUI`; testcase chỉ chọn level, không tạo, đổi tên hoặc xóa level nào. Marker giúp phân biệt Education do test thêm với Education có sẵn của người dùng.

### Tái sử dụng component

| Component | Công việc |
|---|---|
| `TestUtil.myInfoUtil()` | Đăng nhập và mở My Info. |
| `componentLocator.clickSideBarMyInfo()` | Page Object mở Qualifications. |
| `buttonAddMyInfo()` | Mở form thêm Education. |
| `chooseSelectAction()` | Chọn level đã có trong dropdown. |
| `fillInput()` | Điền institute và GPA/Score. |
| `buttonSave()` / `checkToasstSuccess()` | Lưu và kiểm tra bước tạo thành công. |
| `checkTable()` | Tìm dòng Education chứa marker riêng. |
| `deleteButtonTable()` | Xóa đúng dòng đó và xác nhận. |

Page Object gom các lời gọi component theo thao tác dễ đọc như `fillEducation(level, institute, score)`. Không viết lại component và không tạo API client/service. Test không đọc cookie, employee number, Education record ID hay HTTP status; không dùng Rest Assured, SQL hoặc truy cập database.

### Giải thích cho Junior

- **Input:** Level từ config, institute và GPA/Score riêng của lần chạy.
- **Arrange:** Mở Qualifications, Add Education, Save, xác nhận dòng test tồn tại.
- **Act:** Bấm Delete trên đúng dòng test, xác nhận rồi refresh.
- **Assert:** Dòng chứa marker phải không còn sau khi trang tải lại. Kiểm tra dữ liệu trong bảng giúp tránh chỉ dựa vào toast.
- **PASS:** Đã xác nhận tạo được Education và sau thao tác xóa, dòng đó không xuất hiện lại khi refresh.
- **FAIL:** Nếu setup đã thành công nhưng dòng vẫn còn sau Delete và refresh thì hành vi xóa chưa đạt expected. Lỗi login hoặc không tìm thấy level là lỗi chuẩn bị/chạy test, không tự động chứng minh issue #1928.
- **Giới hạn:** Bản thuần UI không đo status 404 hoặc ID nội bộ nên không thể khẳng định những giá trị đó từ lần chạy này. Không còn logic SKIP dựa trên việc record ID trùng employee number. Một lần PASS cũng không chứng minh đã sửa nguyên nhân source cho mọi ID; đối chứng chẩn đoán trước đã cho thấy trường hợp ID trùng có thể xóa thành công.

### Dữ liệu còn lại khi bug xảy ra

Nếu Delete trên UI thành công thì chính thao tác đang test đã dọn bản ghi Education vừa tạo. Nếu bug còn và dòng không xóa được, test ghi rõ marker bản ghi còn lại để người dùng nhận diện. Không chạy cleanup API/database, không xóa Education Level để cascade và không thử đổi ID làm workaround. Các Education Level và dòng Education có sẵn của người dùng được giữ nguyên.

### Kết quả chạy ngày 18/10/2026

Compile bằng `mvn -o -DskipTests test-compile`: **BUILD SUCCESS**, hoàn tất lúc **10:52:10 ngày 18/10/2026 (Asia/Saigon)**. Chạy riêng suite thuần Selenium/POM hoàn tất lúc **10:53:07** cùng ngày:

| Quan sát của bản thuần UI | Kết quả thực tế |
|---|---|
| Education Level đã chọn | `HANUI` có sẵn |
| Institute và GPA/Score của dòng vừa thêm | `ISS1928_1792295559755` |
| Save và kiểm tra dòng trước Delete | Thành công |
| Toast sau Delete | `Error / Records Not Found` |
| Dòng sau refresh và đợi bảng tải xong | Vẫn còn, `stillExists=true` |
| Assertion chính | Expected `false`, actual `true` |
| TestNG | **1 test, 1 FAIL, 0 ERROR, 0 SKIP** |
| HTTP status / record ID nội bộ | Không đo trong bản thuần UI |
| Cleanup API/database hoặc xóa Level | Không thực hiện |

Maven kết thúc **BUILD FAILURE** vì assertion phát hiện dòng Education chưa bị xóa, không phải lỗi compile hay lỗi setup. Giữ nguyên expected. Dòng test có GPA/Score `ISS1928_1792295559755` vẫn còn để đối chiếu; các Level mẫu được giữ nguyên. Class testcase còn **50 dòng**, phần thao tác form/bảng nằm trong `QualificationPage` và dùng lại component.

Bằng chứng của lần chạy mới: [kết quả Selenium](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1928-20261018/selenium-regression-results.json) và [ảnh FAIL từ listener](E:/Studying/GraduateProject/orangehrmtesting/docs/bugs/evidence/issue-1928-20261018/selenium-regression-failure.png). Các bằng chứng API ngày 17/10 ở trên chỉ thuộc lịch sử chẩn đoán, không được dùng thành HTTP status hoặc cleanup của lần chạy thuần UI này.
