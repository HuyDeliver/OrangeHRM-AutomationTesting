package com.hrm.TestCase;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Util.Log;

public class SanityTest extends TestBase {

    @Test
    public void testBase() {
        Assert.assertTrue(driver.getTitle().contains("XYZ_KHONG_TON_TAI"));
        Log.endTestCase();
    }
}
