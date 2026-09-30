package com.hrm.Util;

import java.util.UUID;

public class TestDataShare {
    private static final String RUN_ID = UUID.randomUUID().toString().substring(0, 8);

    public static final String USERNAME = "HuyDeliver_" + RUN_ID;
    public static final String JOB_TITLE = "Devops_" + RUN_ID;
    public static final String LOCATION = "Location_" + RUN_ID;
    public static final String SKILL = "Coding_" + RUN_ID;
    public static final String IDEMPLOYEE = RUN_ID;
    public static final String LASTNAME = "PHAT" + RUN_ID;
    public static final String VACANCY_NAME = "QA_Vacancy_" + RUN_ID;
    public static final String EDITED_VACANCY_NAME = "QA_Vacancy_Edit_" + RUN_ID;
    public static final String MYINFO_OTHER_ID = "QA_" + RUN_ID;
    public static final String MYINFO_STREET = "Automation " + RUN_ID;
    public static final String MYINFO_CITY = "QA_City_" + RUN_ID;
    public static final String MYINFO_OTHER_EMAIL = "qa." + RUN_ID + "@example.com";
    public static final String EMERGENCY_NAME = "Nguyễn Trí Hiếu " + RUN_ID;

    private TestDataShare() {
    }
}
