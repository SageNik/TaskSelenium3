import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.List;

/**
 * Created by Ник on 08.06.2018.
 */
public class Test1 {

    public String currentProductName = "";
    public String currentProductPrice = "";
    public String currentProductAmount = "";
    private EventFiringWebDriver webDriver;

    @DataProvider(name = "loginData")
    public Object[][] getLoginData(){
        Object[][] data = new Object[][]{{"webinar.test@gmail.com","Xcg7299bnSmMuRLp9ITw"}};
        return data;
    }

@BeforeTest
@Parameters("browser")
public void setUp(String browser){
        if(browser.equalsIgnoreCase("Chrome")) {
            webDriver = new EventFiringWebDriver(initChromeDriver());
        }else if(browser.equalsIgnoreCase("Firefox")){
            webDriver = new EventFiringWebDriver(initFirefoxDriver());
        }else if(browser.equalsIgnoreCase("InternetExplorer")){
            webDriver = new EventFiringWebDriver(initIEDriver());
        }
    webDriver.register(new WebDriverLogger());
}

    @Test(dataProvider = "loginData")
    public void createProduct(String login, String password) {

        webDriver.get("http://prestashop-automation.qatestlab.com.ua/admin147ajyvk0/");
        WebElement emailField = webDriver.findElement(By.id("email"));
        emailField.sendKeys(login);
        WebElement passwordField = webDriver.findElement(By.id("passwd"));
        passwordField.sendKeys(password);
        WebElement submitButton = webDriver.findElement(By.name("submitLogin"));
        submitButton.click();

        (new WebDriverWait(webDriver, 3)).until(ExpectedConditions.presenceOfElementLocated(By.id("subtab-AdminCatalog")));

        WebElement mainMenuCatalog = webDriver.findElement(By.id("subtab-AdminCatalog"));
        Actions actions = new Actions(webDriver);
        actions.moveToElement(mainMenuCatalog).build().perform();

        WebElement subMenuProducts = webDriver.findElement(By.id("subtab-AdminProducts"));
        subMenuProducts.click();

        (new WebDriverWait(webDriver, 5)).until(ExpectedConditions.presenceOfElementLocated(By.id("page-header-desc-configuration-add")));

        WebElement newProduct = webDriver.findElement(By.id("page-header-desc-configuration-add"));
        newProduct.click();

        (new WebDriverWait(webDriver, 3)).until(ExpectedConditions.presenceOfElementLocated(By.id("form_step1_name_1")));

        WebElement nameProductField = webDriver.findElement(By.id("form_step1_name_1"));
        currentProductName = getRandomProductName();
        nameProductField.sendKeys(currentProductName);
        WebElement amountProductField = webDriver.findElement(By.id("form_step1_qty_0_shortcut"));
        currentProductAmount = getRandomProductAmount();
        amountProductField.sendKeys(currentProductAmount);
        WebElement priceProductField = webDriver.findElement(By.id("form_step1_price_ttc_shortcut"));
        currentProductPrice = getRandomProductPrice();
        priceProductField.sendKeys(currentProductPrice);

        WebElement activateProduct = webDriver.findElement(By.className("switch-input"));
        activateProduct.click();

        waiteAndCloseGrowlMessage();

        WebElement btnSaveProduct = webDriver.findElement(By.className("js-btn-save"));
        btnSaveProduct.click();
        waiteAndCloseGrowlMessage();

    }

    @Test(dependsOnMethods = "createProduct")
    public void checkMappingOfProduct(){

    webDriver.get("http://prestashop-automation.qatestlab.com.ua/");
        WebElement toAllGoods = webDriver.findElement(By.className("all-product-link"));
        toAllGoods.click();

        List<WebElement> foundProducts = webDriver.findElements(By.tagName("article"));

        for(WebElement element : foundProducts){
            String productName = element.findElement(By.tagName("h1")).getText();
            if(currentProductName.equals(productName)){
                String price = element.findElement(By.className("price")).getText();
                Assert.assertEquals(currentProductName, productName);
                Assert.assertTrue(price.startsWith(currentProductPrice));
            }
        }
            webDriver.close();
    }

    @AfterTest
    public void setDown(){
        webDriver.quit();
    }

    private void waiteAndCloseGrowlMessage() {
        (new WebDriverWait(webDriver, 3)).until(ExpectedConditions.presenceOfElementLocated(By.className("growl-close")));
        WebElement closeMessage = webDriver.findElement(By.className("growl-close"));
        closeMessage.click();
    }

    private String getRandomProductPrice(){
        return String.format("%.2f",0.1 + Math.random()*100);
    }

    private String getRandomProductAmount(){
    return String.format("%d",1+(int)(Math.random()*100));
    }

    private String getRandomProductName(){
        String[] productNames = new String[]{"MainProduct", "FirstProduct", "Some Product", "Big Product", "SmallProduct"};
        int currentIndex =(int)(Math.random() * productNames.length);
        return productNames[currentIndex];
    }

    private WebDriver initChromeDriver() {
        System.setProperty("webdriver.chrome.driver", Test1.class.getResource("chromedriver.exe").getPath());
        return new ChromeDriver();
    }

    private WebDriver initFirefoxDriver() {
        System.setProperty("webdriver.gecko.driver", Test1.class.getResource("geckodriver.exe").getPath());
        return new FirefoxDriver();
    }

    private WebDriver initIEDriver() {
        System.setProperty("webdriver.ie.driver", Test1.class.getResource("IEDriverServer.exe").getPath());
        return new InternetExplorerDriver();
    }
}