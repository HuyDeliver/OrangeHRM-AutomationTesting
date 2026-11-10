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
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void openMyTimesheet(String startDate) {
        componentLocator.clickDropdownNavi(driver, "Timesheets", "My Timesheets");
        if (driver.getCurrentUrl().contains("defineTimesheetPeriod")
                || !driver.findElements(By.xpath("//h6[normalize-space()='Define Timesheet Period']")).isEmpty()) {
            componentLocator.chooseSelectAction(driver, "First Day of Week", "Monday");
            componentLocator.buttonSave(driver);
            componentLocator.checkToasstSuccess(driver, "Success");
            componentLocator.clickDropdownNavi(driver, "Timesheets", "My Timesheets");
        }
        WebElement periodInput = wait.until(ExpectedConditions.visibilityOfElementLocated(period));
        periodInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, startDate, Keys.TAB);
        wait.until(ExpectedConditions.attributeContains(period, "value", startDate));
        if (!driver.findElements(By.xpath("//button[normalize-space()='Create Timesheet']")).isEmpty()) {
            waitUtils.waitElementClick(driver, driver.findElement(By.xpath("//button[normalize-space()='Create Timesheet']")));
        }
        waitForView();
        openedPeriod = getPeriod();
    }

    public void ensureBaselineReady(String startDate, String projectName, String projectOption,
            String activityA, String activityB) {
        if (isZeroBaseline(projectOption, activityA)) return;
        String customerName = projectOption.split(" - ")[0].trim();
        componentLocator.clickDropdownNavi(driver, "Project Info", "Customers");
        if (componentLocator.countTableRows(driver, customerName) == 0) {
            componentLocator.clickButtonAdd(driver);
            componentLocator.fillInput(driver, "Name", customerName);
            componentLocator.buttonSave(driver);
            componentLocator.checkToasstSuccess(driver, "Success");
        }
        componentLocator.clickDropdownNavi(driver, "Project Info", "Projects");
        if (componentLocator.countTableRows(driver, projectName) == 0) {
            componentLocator.clickButtonAdd(driver);
            componentLocator.fillInput(driver, "Name", projectName);
            componentLocator.inputDropdown(driver, "Customer Name", customerName.split("\\s+")[0], customerName);
            componentLocator.buttonSave(driver);
            componentLocator.checkToasstSuccess(driver, "Success");
            for (String act : new String[]{activityA, activityB}) {
                componentLocator.buttonAddMyInfo(driver, "Activities");
                componentLocator.fillInput(driver, "Name", act);
                componentLocator.buttonSave(driver);
                componentLocator.checkToasstSuccess(driver, "Success");
            }
        }
        openMyTimesheet(startDate);
        openEdit();
        WebElement row = driver.findElements(editRows).get(0);
        WebElement projInput = row.findElement(By.cssSelector("td:first-child input"));
        projInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, projectName);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//div[@role='listbox']//span[normalize-space()='" + projectOption + "']"))).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@role='listbox']")));
        selectActivity(row, activityA);
        fillFirstDay("00:00");
        saveAndReload();
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
        WebElement projInput = row.findElement(By.cssSelector("td:first-child input"));
        projInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, projectName);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//div[@role='listbox']//span[normalize-space()='" + projectOption + "']"))).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@role='listbox']")));
        selectActivity(row, activity);
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

    private void selectActivity(WebElement row, String activity) {
        WebElement actDropdown = row.findElement(By.cssSelector("td:nth-child(2) .oxd-select-text"));
        for (int attempt = 0; attempt < 4; attempt++) {
            waitUtils.waitElementClick(driver, actDropdown);
            try {
                WebElement option = new WebDriverWait(driver, Duration.ofSeconds(4)).until(
                        ExpectedConditions.elementToBeClickable(By.xpath(
                                "//div[@role='listbox']//span[normalize-space()='" + activity + "']")));
                option.click();
                return;
            } catch (Exception e) {
                if (!driver.findElements(By.xpath("//div[@role='listbox']")).isEmpty()) {
                    actDropdown.click();
                }
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        }
        throw new IllegalStateException("Không chọn được activity: " + activity);
    }
}