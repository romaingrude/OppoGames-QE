import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class OppoGames_Login_Test {

    public static WebDriver driver;

    private static OppoGames_Login_POM login;


    @BeforeAll
    public static void createDriver() {
        final String browser = System.getProperty("browser", "chrome").toLowerCase();

        switch (browser) {
            case "chrome":
                ChromeOptions optionsC = new ChromeOptions();
                optionsC.addArguments("--headless");
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(optionsC);
                break;

            case "firefox":
                FirefoxOptions optionsF = new FirefoxOptions();
                optionsF.addArguments("--headless");
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(optionsF);
                break;

            case "safari":
                WebDriverManager.safaridriver().setup();
                driver = new SafariDriver();
                driver.manage().window().maximize();
                break;

            default:
                throw new RuntimeException("Invalid browser specified!");
        }
    }

    @BeforeEach
    public void setupPage() throws InterruptedException {
        login = new OppoGames_Login_POM(driver);
        login.navigateLogin();
    }

    @AfterEach
    public void clearBrowserStorage() {
//        login.clearLocalStorage();
    }

    @AfterAll
    public static void closeBrowser() {
        driver.quit();
    }

    @RegisterExtension
    ScreenshotWatcher watcher = new ScreenshotWatcher(driver, "failed_screenshots");

    @Test void checkPageTitleName() {
        // This title is meant to change as it is currently using the default React title
        assertEquals("React App", driver.getTitle());
    }

    @Test
    void userLogsInRedirectedToLobby() throws InterruptedException {
        login.loggingIn("test@test.com", "55555");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(500));
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/lobby"));
        assertEquals("http://localhost:3000/lobby", driver.getCurrentUrl());
    }

    @Test
    void testWithInvalidCredentials(){
        login.loggingIn("fakeemail@fake.com", "Fakepwd123!");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(500));
        wait.until(ExpectedConditions.visibilityOf(login.loginErrorMessage));
        assertTrue(login.loginErrorMessage.isDisplayed());
        assertEquals("http://localhost:3000/login", driver.getCurrentUrl());
    }

    @Test
    void testWithEmptyFields(){
        login.loggingIn("", "");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(500));
        wait.until(ExpectedConditions.visibilityOf(login.loginErrorMessage));
        assertTrue(login.loginErrorMessage.isDisplayed());
        assertEquals("http://localhost:3000/login", driver.getCurrentUrl());
    }

    @Test
    void checksPasswordIsHidden(){
        login.inputPassword.click();
        login.inputPassword.sendKeys("randompassword");
        assertEquals("password", login.inputPassword.getAttribute("type"));
    }

    @Test
     void checksPasswordIsDisplayed(){
        login.inputPassword.click();
        login.inputPassword.sendKeys("randompassword");
        login.passwordShowButton.click();
        assertEquals("text", login.inputPassword.getAttribute("type"));
    }

    @Test
    void checkErrorMessageWithInvalidOrEmptyFields(){
        /* This error message is expected to change from "or password" to
        "and password". Please refer to the bug tracker and Log In test report.
    */
        login.submitLoginButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        wait.until(ExpectedConditions.visibilityOf(login.loginErrorMessage));
        assertTrue(login.loginErrorMessage.isDisplayed());
        assertEquals("Enter a valid email or password", login.loginErrorMessage.getText());
    }

    @Test
    void checksRegisterLinkIsPresentAndRedirectsToSignupPage(){
        assertTrue(login.registerLink.isDisplayed());
        login.registerLink.click();
        assertEquals("http://localhost:3000/signup", driver.getCurrentUrl());
    }


}