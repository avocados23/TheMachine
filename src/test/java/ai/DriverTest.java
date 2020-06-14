package ai;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriverTest {

    private WebDriver driver;

    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "resources/chromedriver");
        driver = new ChromeDriver();
    }

    public static void main (String[] args) {
        DriverTest test = new DriverTest();
        test.setUp();
    }

}
