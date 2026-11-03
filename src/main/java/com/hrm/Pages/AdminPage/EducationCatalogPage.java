package com.hrm.Pages.AdminPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class EducationCatalogPage {
    private final WebDriver driver;
    public EducationCatalogPage(WebDriver driver) { this.driver = driver; }

    public void open() {
        componentLocator.clickDropdownNavi(driver, "Qualifications", "Education");
        waitUtils.waitForTable(driver);
    }

    public int count(String level) { return componentLocator.countTableRows(driver, level); }

    public void add(String level) {
        componentLocator.clickButtonAdd(driver);
        componentLocator.fillInput(driver, "Level", level);
        componentLocator.buttonSave(driver);
        waitUtils.waitForTable(driver);
    }

    public String getId(String level) {
        componentLocator.editButtonTable(driver, level);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[normalize-space()='Level']/following::input[1]")));
        return driver.getCurrentUrl().substring(driver.getCurrentUrl().lastIndexOf('/') + 1);
    }
}
