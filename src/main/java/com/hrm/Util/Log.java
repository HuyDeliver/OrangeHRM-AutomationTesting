package com.hrm.Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.qameta.allure.Allure;

public class Log {
    private static final Logger log = LogManager.getLogger(Log.class);

    public static void startTestCase(String testCaseName) {
        log.info("*******************************************************************");
        log.info("$$$$$$$$$$$$$$$$$$$$   " + testCaseName + "       $$$$$$$$$$$$$$$$$$$");
        log.info("*******************************************************************");
    }

    public static void endTestCase() {
        log.info("$$$$$$$$$$$$$$$$$$$$      " + "E---N---D-" + "     $$$$$$$$$$$$$$$$$$$ ");
    }

    public static void info(String message) {
        log.info(message);
        Allure.step(message);
    }

    public static void warn(String message) {
        log.warn(message);
        Allure.step("[WARN]: " + message);
    }

    public static void error(String message) {
        log.error(message);
        Allure.step("[ERROR]: " + message);
    }

    public static void fatal(String message) {
        log.fatal(message);
        Allure.step("[FATAL]: " + message);
    }

    public static void debug(String message) {
        log.debug(message);
        Allure.step("[DEBUG]: " + message);
    }
}
