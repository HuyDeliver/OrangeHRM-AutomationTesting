package com.hrm.Util;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {
    private int retryCount = 0;
    private int maxretryCount = 1;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxretryCount) {
            System.out.println("Retrying test: " + result.getName() + "with status"
                    + getResultStatusName(result.getStatus()) + "for the" + retryCount + 1 + "time.s");
            retryCount++;
            return true;
        }
        return false;
    }

    public String getResultStatusName(int status) {
        String resultName = null;
        if (status == 1)
            resultName = "SUCCESS"; // Thành công
        if (status == 2)
            resultName = "FAILURE"; // Thất bại
        if (status == 3)
            resultName = "SKIP"; // Bị bỏ qua
        return resultName;
    }
}
