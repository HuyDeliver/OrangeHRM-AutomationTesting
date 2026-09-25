package com.hrm.Util;

import java.io.File;

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
}
