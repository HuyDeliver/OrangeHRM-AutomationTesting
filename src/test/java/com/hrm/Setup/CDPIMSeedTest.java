package com.hrm.Setup;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.AdminPage.AddLocationPage;
import com.hrm.Pages.AdminPage.JobTitlePage;
import com.hrm.Pages.AdminPage.LocationPage;
import com.hrm.Pages.AdminPage.PayGradePage;
import com.hrm.Pages.AdminPage.PIMCatalogPage;
import com.hrm.Pages.AdminPage.UserManagePage;
import com.hrm.Util.Config;
import com.hrm.Util.Log;
import com.hrm.Util.TestConfig;
import com.hrm.Util.componentLocator;

// Kế thừa seed employee và guard CD; cả hai method đều chỉ là data preparation.
public class CDPIMSeedTest extends CDSeedTest {
    private final Properties result = new Properties();

    @Test(groups = "data-preparation", dependsOnMethods = "prepareMinimalData",
            description = "CD setup: chuẩn bị danh mục cho PIM, không tính functional PASS")
    public void preparePIMCatalogs() throws Exception {
        for (String key : new String[]{"pim.job.title", "pim.job.category", "pim.employment.status",
                "pim.subunit", "pim.location", "pim.paygrade"}) {
            Assert.assertTrue(Config.get(key, "").startsWith("AUTO CD "), "Thiếu tên fixture CD riêng: " + key);
        }
        Assert.assertEquals(TestConfig.nameSupervisor, Config.get("admin.employee.name"), "Supervisor phải là employee baseline");
        TestUtil.jobUtil();
        JobTitlePage jobs = new JobTitlePage(driver);
        int count = jobs.countJobTitles(TestConfig.jobTitleName);
        Assert.assertTrue(count <= 1, "Duplicate Job Title fixture");
        if (count == 0) {
            jobs.addUserPage().addTitleJob(TestConfig.jobTitleName, "CD PIM fixture", TestConfig.jobSpecification, "CD seed");
            Assert.assertTrue(componentLocator.checkToasstSuccess(driver, "Seed Job"), "Không tạo được Job Title");
        }
        Assert.assertEquals(jobs.countJobTitles(TestConfig.jobTitleName), 1, "Sai số Job Title");
        jobs.goToEditPage(TestConfig.jobTitleName);
        record("job", TestConfig.jobTitleName);

        PIMCatalogPage catalog = new PIMCatalogPage(driver);
        ensureName(catalog, "Employment Status", TestConfig.employmentStatus, "status");
        ensureName(catalog, "Job Categories", TestConfig.jobCategorize, "category");
        catalog.openStructure();
        count = catalog.countSubUnits(TestConfig.subUnit);
        Assert.assertTrue(count <= 1, "Duplicate Sub Unit fixture");
        if (count == 0) catalog.addSubUnit(TestConfig.subUnit);
        Assert.assertEquals(catalog.countSubUnits(TestConfig.subUnit), 1, "Sai số Sub Unit");
        record("subunit", TestConfig.subUnit);

        LocationPage locations = new UserManagePage(driver).goToLocationPage("Organization", "Locations");
        locations.searchLocation(TestConfig.locationName, "", "");
        count = locations.countLocations(TestConfig.locationName);
        Assert.assertTrue(count <= 1, "Duplicate Location fixture");
        if (count == 0) {
            AddLocationPage add = locations.goToAddLocationPage();
            add.fillLocatinSpecific(TestConfig.locationName, "CD City", "", "", "Viet Nam", "", "", "", "CD seed");
            add.saveLocation();
            Assert.assertTrue(add.isAddLocationSucess(), "Không tạo được Location");
        }
        locations.searchLocation(TestConfig.locationName, "", "");
        Assert.assertEquals(locations.countLocations(TestConfig.locationName), 1, "Sai số Location");
        locations.clickEditLocation(TestConfig.locationName);
        record("location", TestConfig.locationName);

        PayGradePage grades = new UserManagePage(driver).goToPayGradePage();
        count = componentLocator.countTableRows(driver, TestConfig.payGrade);
        Assert.assertTrue(count <= 1, "Duplicate Pay Grade fixture");
        if (count == 0) grades.addPayGrade(TestConfig.payGrade);
        else grades.openPayGrade(TestConfig.payGrade);
        if (!grades.hasCurrency(TestConfig.currency)) {
            grades.addCurrency(Config.get("pim.currency.code"), Config.get("pim.salary.minimum"), Config.get("pim.salary.maximum"));
        }
        Assert.assertTrue(grades.hasSalaryRange(TestConfig.currency, Config.get("pim.salary.minimum"),
                Config.get("pim.salary.maximum")), "Pay Grade fixture có khoảng lương khác dự kiến; không tự ghi đè");
        record("paygrade", TestConfig.payGrade);
        result.setProperty("result", "SUCCESS");
        result.setProperty("kind", "DATA_PREPARATION");
        try (Writer writer = Files.newBufferedWriter(Path.of(System.getProperty("seed.output"), "pim-seed-result.properties"))) {
            result.store(writer, "PIM setup only; excluded from functional PASS");
        }
    }

    private void ensureName(PIMCatalogPage page, String menu, String name, String key) {
        page.openJobCatalog(menu);
        int count = componentLocator.countTableRows(driver, name);
        Assert.assertTrue(count <= 1, "Duplicate fixture: " + name);
        if (count == 0) {
            page.addName(name);
            Assert.assertTrue(componentLocator.checkToasstSuccess(driver, menu), "Không tạo được " + name);
        }
        Assert.assertEquals(componentLocator.countTableRows(driver, name), 1, "Sai số record: " + name);
        componentLocator.editButtonTable(driver, name);
        record(key, name);
    }

    private void record(String key, String name) {
        result.setProperty(key + ".name", name);
        result.setProperty(key + ".url", driver.getCurrentUrl());
        result.setProperty(key + ".matches", "1");
        Log.info("[PIM SEED] " + key + "=" + name + " | matches=1 | " + driver.getCurrentUrl());
    }
}
