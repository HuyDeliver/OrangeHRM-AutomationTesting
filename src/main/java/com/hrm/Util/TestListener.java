package com.hrm.Util;

import org.testng.ITestListener;
import org.testng.ITestResult;

import com.hrm.Base.TestBase;

public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        Log.startTestCase(result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Log.info("PASS: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Log.error("FAIL: " + result.getName());
        Log.error("REASON: " + result.getThrowable());
        Object testInstance = result.getInstance();
        if (testInstance instanceof TestBase) {
            ((TestBase) testInstance).takeScreenshot(result.getTestName());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Log.warn("SKIPPED: " + result.getName());
    }

}
