package com.hrm.Util;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestConfig {
        public static final String userName = "HuyDeliver";
        public static final String userRole = "Admin";
        public static final String employeeName = "Nguyễn";
        public static final String status = "Enabled";
        public static final String password = "HuyDeliver@1234";
        public static final String confirmPassword = "HuyDeliver@1234";

        // Data Job title
        public static final String jobTitleName = "Devops";
        public static final String jobDescription = "DevOps is a software development methodology that combines and automates the work of software development (Dev) and IT operations (Ops) to improve and shorten the systems development life cycle.";
        public static final String jobSpecification = System.getProperty("user.dir")
                        + File.separator + "src" + File.separator + "test" + File.separator
                        + "resources" + File.separator + "JobSpecification.pdf";
        public static final String jobNote = "DevOps is characterized by several key principles, including shared ownership, workflow automation, and rapid feedback. ";

        // Data location
        public static final String locationName = "Đống Đa";
        public static final String cityName = "Hà Nội";
        public static final String provinceName = "Đống Đa";
        public static final String postalCode = "2004";
        public static final String countryName = "Viet Nam";
        public static final String phoneNumber = "0917196589";
        public static final String faxNumber = "554466";
        public static final String addressSpecific = "100 Yên Lãng";
        public static final String locationNote = "Alo Vũ à Vũ, anh ở 120 Yên Lãng đây";

        // addEmployee
        public static final String firstName = "Nguyễn";
        public static final String middleName = "Huy";
        public static final String lastName = "Phát";
        public static final String employeeID = String.valueOf(System.currentTimeMillis()).substring(7);
        public static final String statusEnabled = "Enabled";
        public static final String statusDisabled = "Disabled";
        public static final String image = System.getProperty("user.dir")
                        + File.separator + "src" + File.separator + "test" + File.separator
                        + "resources" + File.separator + "image" + File.separator + "portraitphoto.jpg";

        // Skills
        public static final String skill = "Coding";

        // Job detail in My infor
        public static final String joinedDate = "2026-5-10";
        public static final String jobCategorize = "Craft Workers";
        public static final String subUnit = "Administration";
        public static final String employmentStatus = "Full Time Contract";

        // Salary in MyInfo
        public static final String salary = "base salary";
        public static final String payGrade = "Engineer";
        public static final String payFrequency = "Monthly";
        public static final String currency = "Vietnamese Dong";
        public static final String amount = "10000";
        public static final String comment = "nothing";

        // supervisor
        public static final String nameSupervisor = "Nguyễn Huy Bách";
        public static final String surbordinate = "Nguyễn Trí Quân";
        public static final String reportMethod = "Direct";

        public static final String email = "Channelbaby15@gmail.com";

        // Issue #1897: currency phải chưa được Pay Grade khác sử dụng
        public static final String payGradeTestCurrency = "EUR - Euro";
        public static final String payGradeTestCurrencyName = "Euro";
        public static final String payGradeAMinimum = "1000";
        public static final String payGradeAMaximum = "2000";
        public static final String payGradeBMinimum = "3000";
        public static final String payGradeBMaximum = "4000";
        public static final String payGradeANewMinimum = "1500";
        public static final String payGradeANewMaximum = "2500";

        // Issue #1926: các vacancy Active đã kiểm tra trên local, không tạo vacancy giả.
        public static final String[] shortlistVacancies = { "Devops", "Sales IT", "Tester" };
        public static final String shortlistEmailDomain = "@example.invalid";

        // Issue #1890: fixture riêng đã tạo qua UI và được phép giữ lại; không Submit.
        public static final String timesheetStartDate = "2026-10-12";
        public static final String timesheetPeriod = "2026-10-12 to 2026-10-18";
        public static final String timesheetProjectName = "ISS1890 Project";
        public static final String timesheetProjectOption = "ISS1890 Customer - ISS1890 Project";
        public static final String timesheetActivityA = "ISS1890 Activity A";
        public static final String timesheetActivityB = "ISS1890 Activity B";
        public static final String timesheetHours = "01:00";

        // Issue 1820: giữ Event và Claim nháp đã được phép; chỉ thêm/xóa file thử.
        public static final String claimTestEvent = "ISS1820 Attachment Test";
        public static final String claimTestRemarks = "ISS1820 reusable draft - attachment regression";
        public static final String claimTestCurrency = "Vietnamese Dong";

        // Education: Level đã có sẵn trong Admin > Qualifications > Education
        public static final String educationLevel = "HANUI";

        // personal detail
        public static final String id = "0382040431386";
        public static final String license = "0382040431386";
        public static final String licenseExpired = "2030-01-01";

        public static final String maritualStatus = "Single";
        public static final String dateOfBirth = "2004-04-18";
        public static final String gender = "Male";

        // Contact detail
        public static String street1 = "123 Nguyen Trai";
        public static String street2 = "Thanh Xuan";
        // Telephone
        public static String homePhone = "0241234567";
        public static String mobilePhone = "0987654321";
        public static String workPhone = "0917196589";

        // Email
        public static String otherEmail = "Stunanguyen@gmail.com";

        public static String relationShip = "Mother";

        public static String terminateDate = "2026-09-28";
        public static String terminateReason = "Dismissed";

        public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyy-MM-dd");

        public static final String startDate = LocalDate.now().minusDays(30).format(dateFormat);
        public static final String endDate = LocalDate.now().minusDays(1).format(dateFormat);
        public static final String dueDate = LocalDate.now().plusDays(7).format(dateFormat);

        public static String shortenName(String name) {
                String[] nameParts = name.trim().split("\\s+");

                String first = nameParts[0];
                String last = nameParts[nameParts.length - 1];

                return first + " " + last;
        }

        //token bearer Oauth
        public static final String token ="def502001f64fdfdc63165f8f62269dba1e133d0727e6ef8c84b9ce32ca18ee0a9f05f7f63f3704af82ed481dcf76956ea382fabf8b66a5668e94d190071d6d1d2e1eed28ac86645eb6d4b0900efcf6e5da9e9dfd87527a2b2da77019089b3be8a2cc8a8f9ff073f8a64933cc54967ce440462c01142766f071771d19fcd37015cb6de198df5be6ed000fd6ef1408f32641769d8746d3539cd52c8b53c3ea442fafafce0";

}
