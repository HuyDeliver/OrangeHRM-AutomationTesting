package com.hrm.Pages.PerformancePage;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hrm.Util.Log;
import com.hrm.Util.componentLocator;
import com.hrm.Util.waitUtils;

public class WriteReviewPage {
        private WebDriver driver;

        public WriteReviewPage(WebDriver driver) {
                this.driver = driver;
                PageFactory.initElements(driver, this);
        }

        @FindBy(xpath = "//div[@class='orangehrm-performance-review-actions']/child::button[normalize-space()='Complete']")
        private WebElement completeButton;

        public boolean isAddReviewsVisible(String title) {
                return componentLocator.checkTitleH5(driver, title);
        }

        public void fillKpiEvaluation(String kpiName, int rating, String comment, int minRate, int maxRate) {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                String kpiXpath = String.format(
                                "//p[@title='%s']/ancestor::div[contains(@class,'orangehrm-evaluation-grid-kpi')][1]",
                                kpiName);
                WebElement kpiContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(kpiXpath)));

                WebElement ratingInput = kpiContainer.findElement(
                                By.xpath("./following-sibling::div[.//input][1]//input"));

                WebElement commentInput = kpiContainer.findElement(
                                By.xpath("./following-sibling::div[.//textarea][1]//textarea"));

                Log.info("Nhập rating cho " + kpiName + ": " + rating);
                if (rating < minRate || rating > maxRate) {
                        throw new IllegalArgumentException(
                                        "Rating must be between " + minRate + " and " + maxRate);
                }
                ratingInput.sendKeys(String.valueOf(rating));
                Log.info("Nhập comment cho " + kpiName + ": " + comment);
                commentInput.sendKeys(comment);
        }

        public void enterGeneralComment(String comment) {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                if (comment != null && !comment.isEmpty()) {
                        Log.info("Nhập general comment: " + comment);
                        WebElement generalComment = wait.until(ExpectedConditions.presenceOfElementLocated(
                                        By.xpath("//p[normalize-space()='General Comment']/ancestor::div[contains(@class,'oxd-grid-item')][1]/following-sibling::div[1]//textarea")));
                        generalComment.sendKeys(comment);
                }

        }

        public void reviewFinalization(String date, String rate, String finalcomment) {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                if (date != null && !date.isEmpty()) {
                        Log.info("Nhập date input: " + date);
                        WebElement dateInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                                        By.xpath("//p[normalize-space()='Date of Completion']/ancestor::div[contains(@class,'oxd-grid-item')][1]/descendant::input[1]")));
                        dateInput.sendKeys(date);
                }

                if (rate != null && !rate.isEmpty()) {
                        Log.info("Nhập final rating: " + rate);
                        WebElement finalRating = wait.until(ExpectedConditions.presenceOfElementLocated(
                                        By.xpath("//p[normalize-space()='Final Rating']/ancestor::div[contains(@class,'oxd-grid-item')][1]/descendant::input[1]")));

                        if (finalRating.isEnabled()) {
                                finalRating.sendKeys(rate);
                        } else {
                                Log.info("Ô Final Rating ở trạng thái Read-only, tự động bỏ qua.");
                        }
                }

                if (finalcomment != null && !finalcomment.isEmpty()) {
                        Log.info("Nhập final comment: " + finalcomment);
                        WebElement finalComment = wait.until(ExpectedConditions.presenceOfElementLocated(
                                        By.xpath("//p[normalize-space()='Final Comments']/ancestor::div[contains(@class,'oxd-grid-item')][1]/descendant::textarea[1]")));
                        finalComment.sendKeys(finalcomment);
                }
        }

        public void clickComplete() {
                Log.info("Click Complete");
                waitUtils.waitElementClick(driver, completeButton);

                By confirmModal = By.cssSelector(".oxd-dialog-container-default .oxd-sheet");
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                wait.until(ExpectedConditions.visibilityOfElementLocated(confirmModal));

                Log.info("Xác nhận complete");
                WebElement confirmComplete = driver
                                .findElement(By.cssSelector(".orangehrm-modal-footer > button:nth-child(2)"));
                waitUtils.waitElementClick(driver, confirmComplete);

                wait.until(ExpectedConditions.invisibilityOfElementLocated(confirmModal));
        }

        public ManageReviewPage goToManageReviewPage(String tab, String option) {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                By loadingSpinner = By.xpath("//div[contains(@class, 'oxd-loading-spinner')]");
                try {
                        wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingSpinner));
                } catch (Exception e) {
                        Log.info("Spinner không xuất hiện hoặc đã biến mất.");
                }

                componentLocator.clickDropdownNavi(driver, tab, option);
                componentLocator.takeScreenshotResult(driver, "Check review after add");
                return new ManageReviewPage(driver);
        }

}
