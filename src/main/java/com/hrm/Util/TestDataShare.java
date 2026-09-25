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

    private TestDataShare() {
    }
}
