package com.hrm.Pages.TimePage;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.Log;
import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class TimesheetPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private String openedPeriod;
    private final By period = By.cssSelector(".orangehrm-timeperiod-picker input");
    private final By edit = By.xpath("//button[normalize-space()='Edit']");
    private final By save = By.cssSelector(".orangehrm-timesheet-footer--options button[type='submit']");
    private final By editRows = By.xpath("//tbody[contains(@class,'orangehrm-timesheet-table-body')]/tr[td[1]//input]");

    public TimesheetPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public void openMyTimesheet(String startDate) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//li[contains(@class,'oxd-topbar-body-nav-tab') and contains(.,'Timesheets')]")));
        componentLocator.clickDropdownNavi(driver, "Timesheets", "My Timesheets");
        wait.until(ExpectedConditions.visibilityOfElementLocated(period))
                .sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, startDate, Keys.TAB);
        wait.until(ExpectedConditions.attributeContains(period, "value", startDate));
        waitForView();
        openedPeriod = getPeriod();
    }

    public String getPeriod() {
        return driver.findElement(period).getAttribute("value");
    }

    public String getFirstWeekday() {
        return driver.findElement(By.cssSelector(
                ".orangehrm-timesheet-table-header th:nth-child(3) span:last-child")).getText();
    }

    public String getStatus() {
        return driver.findElement(By.cssSelector(".orangehrm-timesheet-footer--title")).getText();
    }

    public boolean isZeroBaseline(String project, String activity) {
        List<WebElement> rows = driver.findElements(By.xpath(
                "//tbody[contains(@class,'orangehrm-timesheet-table-body')]/tr[td[2]/span]"));
        return rows.size() == 1
                && rows.get(0).findElement(By.cssSelector("td:nth-child(1)")).getText().equals(project)
                && rows.get(0).findElement(By.cssSelector("td:nth-child(2)")).getText().equals(activity)
                && rows.get(0).findElement(By.cssSelector("td:nth-child(10)")).getText().equals("00:00");
    }

    public void openEdit() {
        waitUtils.waitElementClick(driver, driver.findElement(edit));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".orangehrm-timesheet-table-body tr:first-child input")));
    }

    public void fillFirstDay(String hours) {
        Log.info("Nhập giờ ngày đầu của dòng A: " + hours);
        driver.findElement(By.cssSelector(".orangehrm-timesheet-table-body tr:first-child td:nth-child(3) input"))
                .sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, hours, Keys.TAB);
    }

    public void addEmptyRow(String projectName, String projectOption, String activity) {
        waitUtils.waitElementClick(driver, driver.findElement(By.xpath(
                "//tbody[contains(@class,'orangehrm-timesheet-table-body')]//button[i[contains(@class,'bi-plus')]]")));
        WebElement row = driver.findElements(editRows).get(1);
        row.findElement(By.cssSelector("td:first-child input")).sendKeys(projectName);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//div[@role='listbox']//span[normalize-space()='" + projectOption + "']"))).click();
        waitUtils.waitElementClick(driver, row.findElement(By.cssSelector("td:nth-child(2) .oxd-select-text")));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//div[@role='listbox']//span[normalize-space()='" + activity + "']"))).click();
        // Không nhập hoặc click ô giờ của B: phải giữ nguyên dữ liệu ngày trống.
    }

    public boolean secondRowHoursAreBlank() {
        List<WebElement> hours = driver.findElements(By.cssSelector(
                ".orangehrm-timesheet-table-body tr:nth-child(2) td.--duration-input input"));
        if (hours.size() != 7) return false;
        for (WebElement input : hours) {
            if (!input.getAttribute("value").isEmpty()) return false;
        }
        return true;
    }

    public void saveAndReload() {
        // Save Timesheet không nằm trong .oxd-form-actions như component buttonSave().
        waitUtils.waitElementClick(driver, driver.findElement(save));
        waitForView();
        driver.navigate().refresh();
        waitForView();
        if (!getPeriod().equals(openedPeriod)) throw new IllegalStateException("Sai tuần sau refresh; dừng kiểm tra giờ");
    }

    public String getFirstDayHours(String activity) {
        List<WebElement> rows = driver.findElements(By.xpath(
                "//tbody[contains(@class,'orangehrm-timesheet-table-body')]/tr[td[2][normalize-space()='"
                        + activity + "']]"));
        return rows.isEmpty() ? "Không có dòng A" : rows.get(0).findElement(By.cssSelector("td:nth-child(3)")).getText();
    }

    public void resetToZero(String project, String activityA, String activityB) {
        if (!driver.findElements(save).isEmpty()) {
            waitUtils.waitElementClick(driver, driver.findElement(By.xpath("//button[normalize-space()='Cancel']")));
            waitForView();
        }
        if (!getPeriod().equals(openedPeriod) || !getStatus().equals("Status: Not Submitted")) {
            throw new IllegalStateException("Dừng reset: sai tuần fixture hoặc timesheet không còn Not Submitted");
        }
        openEdit();
        List<WebElement> rows = driver.findElements(editRows);
        if (rows.size() < 1 || rows.size() > 2) throw new IllegalStateException("Dừng reset: số dòng fixture không đúng");
        for (int i = 0; i < rows.size(); i++) {
            String expectedActivity = i == 0 ? activityA : activityB;
            if (!rows.get(i).findElement(By.cssSelector("td:first-child input")).getAttribute("value").equals(project)
                    || !rows.get(i).findElement(By.cssSelector("td:nth-child(2) .oxd-select-text")).getText().equals(expectedActivity)) {
                throw new IllegalStateException("Dừng reset: có dòng ngoài fixture được phép sửa");
            }
        }
        if (rows.size() == 2) {
            waitUtils.waitElementClick(driver, rows.get(1).findElement(By.xpath(".//button[i[contains(@class,'bi-trash')]]")));
        }
        fillFirstDay("00:00");
        saveAndReload();
    }

    private void waitForView() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(edit));
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector(".oxd-loading-spinner"), 0));
    }
}
