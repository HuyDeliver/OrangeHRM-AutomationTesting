package com.hrm.Pages.AdminPage;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

// Hai danh mục chỉ có field Name và cây Organization Structure.
public class PIMCatalogPage {
    private final WebDriver driver;
    public PIMCatalogPage(WebDriver driver) { this.driver = driver; }

    public void openJobCatalog(String menu) {
        componentLocator.clickDropdownNavi(driver, "Job", menu);
        waitUtils.waitForTable(driver);
    }

    public void addName(String name) {
        componentLocator.clickButtonAdd(driver);
        componentLocator.fillInput(driver, "Name", name);
        componentLocator.buttonSave(driver);
    }

    public void openStructure() {
        componentLocator.clickDropdownNavi(driver, "Organization", "Structure");
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".org-root-container")));
    }

    public int countSubUnits(String name) {
        return driver.findElements(By.cssSelector(".org-name")).stream()
                .filter(e -> e.getText().trim().equals(name)).toList().size();
    }

    public void addSubUnit(String name) {
        waitUtils.waitElementClick(driver, driver.findElement(By.cssSelector(".oxd-switch-input")));
        waitUtils.waitElementClick(driver, driver.findElement(By.cssSelector(".org-root-container .org-structure-add")));
        componentLocator.fillInput(driver, "Name", name);
        componentLocator.buttonSave(driver);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> countSubUnits(name) == 1);
    }
}
