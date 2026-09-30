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

}
