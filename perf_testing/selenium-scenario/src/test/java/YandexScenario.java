import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.openqa.selenium.Keys.CONTROL;

public class YandexScenario {
    WebDriver driver;

    @BeforeTest
    public void beforeTest() {
        String webDriverKey = "webdriver.gecko.driver";
        String webDriverValue = System.getProperty("user.dir") +
                "/target/tmp_webdrivers/geckodriver-windows-64bit.exe";
        System.setProperty(webDriverKey, webDriverValue);
        driver = new FirefoxDriver();
    }

    @Test
    public void testYandexImagesScenario() throws InterruptedException {
        driver.manage().window().maximize();
        driver.get("http://www.yandex.ru");
        WebElement element = driver.findElement(By.partialLinkText("Картинки"));
        element.click();
        String newWindow = driver.getWindowHandle();
//        driver.navigate().refresh();
        //driver.findElement(By.cssSelector("body")).sendKeys(CONTROL +"\t");
//        WebElement body = driver.findElement(By.cssSelector("body"));
//        body.sendKeys(CONTROL +"t");
//        driver.switchTo().window(newWindow);
//        String searchImagesXpath = "/html/body/div[1]/div/div[1]/header/div/div[1]/div[2]/form/div[1]/span/span/input";
        switchToTab();
        WebElement searchBox = driver.findElement(By.name("text"));
        searchBox.click();
        searchBox.clear();
        searchBox.sendKeys("котики");
        WebElement searchButton = driver.findElement(By.className("websearch-button"));
        searchButton.click();
        // "https://yandex.ru/images/search?text=котики"
        List<WebElement> childs = driver.findElements(By.xpath("//a[@class='serp-item__link']"));
        WebElement imagesList = driver.findElement(By.className("serp-list"));
        Assert.assertTrue(childs.size() > 0);
        childs.get(0).click();
        //"https://yandex.ru/images/search?text=котики&ncrnd=1587157380590-4033227287517722&pos=0&img_url=https%3A%2F%2Fcs5.pikabu.ru%2Fpost_img%2Fbig%2F2015%2F09%2F29%2F8%2F1443532339_1029923217.jpg&rpt=simage"
    }

    public void switchToTab() throws InterruptedException {
        //Switching between tabs using CTRL + tab keys.
//        driver.findElement(By.cssSelector("body")).sendKeys(CONTROL +"t");
        //Switch to current selected tab's content.
//        driver.switchTo().defaultContent();
        ArrayList<String> tabs2 = new ArrayList<>();
        while (tabs2.size() < 2) {
            tabs2 = new ArrayList<>(driver.getWindowHandles());
        }
        Thread.sleep(100);
        driver.switchTo().window(tabs2.get(1));
    }

    @AfterTest
    public void afterTest() {
        driver.quit();
    }
}
