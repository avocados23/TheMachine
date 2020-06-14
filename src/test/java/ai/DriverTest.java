package ai;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriverTest {

    private WebDriver driver;

    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "resources/chromedriver");
        driver = new ChromeDriver();
        driver.get("https://google.com/");
        System.out.println(driver.getTitle());
    }

    public static void main (String[] args) {
        DriverTest test = new DriverTest();
        test.setUp();
    }

}
