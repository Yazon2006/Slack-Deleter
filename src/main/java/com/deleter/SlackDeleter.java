package com.deleter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import rx.Observable;

public class SlackDeleter {

    private static WebDriver driver = new FirefoxDriver();
    private static WebDriverWait wait = new WebDriverWait(driver, 36000);

    public static void main(String[] args) {

        String url = "https://jelvix.slack.com/";

        driver.get(url);

        waitAndClick(By.id("flex_menu_toggle"));
        waitAndClick(By.id("files_user"));

        Observable.just(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("file_listing_bottom_button"))))
                .subscribe((webElement) -> {
                    driver.get(webElement.getAttribute("href"));
                }, SlackDeleter::handleError);

        while (driver.findElements(By.xpath("//div[starts-with(@id,'file_')]")) != null) {
            deleteFile();
        }
    }

    private static void waitAndClick(By by) {
        Observable.just(wait.until(ExpectedConditions.visibilityOfElementLocated(by)))
                .subscribe(WebElement::click, SlackDeleter::handleError);
    }

    private static void deleteFile() {
        waitAndClick(By.xpath("//div[starts-with(@id,'file_')]"));
        waitAndClick(By.id("file_action_cog"));
        waitAndClick(By.id("delete_file"));
        waitAndClick(By.xpath("//*[@id='generic_dialog']/div[3]/a[3]"));
        waitAndClick(By.xpath("//*[@id=\"page_contents\"]/div[1]/a[2]"));
    }

    private static void handleError(Throwable throwable) {
        throwable.printStackTrace();
    }

}
