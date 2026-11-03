package com.hrm.Setup;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.EducationCatalogPage;
import com.hrm.Util.TestConfig;
import com.hrm.Util.Log;

// Giữ prerequisites đã có của PIM/Recruitment; chỉ thêm Education level.
public class CDMyInfoSeedTest extends CDRecruitmentSeedTest {
    @Test(groups = "data-preparation", dependsOnMethods = "prepareRecruitmentVacancies",
            description = "CD setup: Education level, không tính functional PASS")
    public void prepareEducationLevel() throws Exception {
        String level = TestConfig.educationLevel;
        Assert.assertTrue(level.startsWith("AUTO CD "), "Chỉ seed Education riêng của CD");
        TestUtil.userUtil();
        EducationCatalogPage page = new EducationCatalogPage(driver);
        page.open();
        int count = page.count(level);
        Assert.assertTrue(count <= 1, "Duplicate Education level");
        if (count == 0) page.add(level);
        Assert.assertEquals(page.count(level), 1, "Phải có đúng một Education level");
        String id = page.getId(level);
        Assert.assertTrue(id.matches("\\d+"), "Không đọc được Education ID từ UI");
        Properties result = new Properties();
        result.setProperty("kind", "DATA_PREPARATION");
        result.setProperty("result", "SUCCESS");
        result.setProperty("fixture.key", level);
        result.setProperty("education.id", id);
        result.setProperty("matches", "1");
        try (Writer out = Files.newBufferedWriter(Path.of(System.getProperty("seed.output"), "myinfo-seed-result.properties"))) {
            result.store(out, "Setup only");
        }
        Log.info("[MYINFO SEED] " + level + " | id=" + id + " | matches=1");
    }
}
