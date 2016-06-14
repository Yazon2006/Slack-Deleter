package com.deleter;


import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionNotFoundException;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class DriverFactory {

    private final static Properties properties = Properties.getInstance();
    private final static int TIME_FOR_WAITING_SECONDS = 60;

    private static int instances = 0;
    private static WebDriver driver;

    public static WebDriver getDriver() {
        if (!isClosed()) {
            return driver;
        }

        System.out.println("Refresh driver");
        URL remoteServer;
        try {
            remoteServer = new URL("http://localhost:4444/wd/hub");
        } catch (MalformedURLException e) {
            System.out.println("Cannot create url: " + e);
            throw new RuntimeException(e);
        }

        Properties.Browsers browser = properties.getBrowser();
        boolean isLocal = properties.isRunLocally();
        DesiredCapabilities capabilities = new DesiredCapabilities();

        switch (browser) {
            case HTMLUNIT:
                driver = new HtmlUnitDriver();
                System.out.println("HtmlUnit browser is selected");
                break;
            case FIREFOX:
                if (isLocal)
                    driver = new FirefoxDriver();
                else {
                    capabilities.setBrowserName("firefox");
                    driver = new RemoteWebDriver(remoteServer, capabilities);
                }
                System.out.println("Firefox browser is selected");
                break;
            case CHROME:
                if (SystemUtils.IS_OS_LINUX) {
                    System.setProperty("webdriver.chrome.driver",
                            "src/main/resources/chromedriver.sh");
                    capabilities.setPlatform(Platform.LINUX);
                } else if (SystemUtils.IS_OS_WINDOWS) {
                    System.setProperty("webdriver.chrome.driver",
                            "src/main/resources/chromedriver.exe");
                    capabilities.setPlatform(Platform.WINDOWS);
                } else if (SystemUtils.IS_OS_MAC_OSX) {
                    File file = new File("./src/main/resources/chromedriver");
                    if (!file.canExecute())
                        file.setExecutable(true, true);
                    System.setProperty("webdriver.chrome.driver",
                            "./src/main/resources/chromedriver");
                    capabilities.setPlatform(Platform.MAC);
                }
                if (isLocal)
                    driver = new ChromeDriver();
                else {
                    capabilities.setBrowserName("chrome");
                    driver = new RemoteWebDriver(remoteServer, capabilities);
                }
                System.out.println("CHROME browser is selected");
                break;
        }
        setDefaultImplicitWait();
        driver.manage().window().maximize();
        instances++;
        System.out.println("WebDriver instances: " + instances);
        return driver;
    }

    public static void setDefaultImplicitWait() {
        setImplicitWait(TIME_FOR_WAITING_SECONDS);
    }

    public static void setImplicitWait(int sec) {
        driver.manage().timeouts().implicitlyWait(sec, TimeUnit.SECONDS);
    }

    public static boolean isClosed() {
        if (driver == null || driver.toString().contains("(null)")) {
            return true;
        }
        try {
            driver.getWindowHandle();
        } catch (SessionNotFoundException | NoSuchWindowException | UnreachableBrowserException e) {
            return true;
        }
        return false;
    }
}
