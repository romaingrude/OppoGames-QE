import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class Authentication_TEST {


    @Nested
    @Order(1)
    public class SignUp_TEST {
        static WebDriver driver;
        static SignUp_POM signup;

        @BeforeAll
        public static void createDriverAndClearsDatabase() {
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
            MongoDBConnection.clearDatabase();
        }

        @BeforeEach
        public void setupPage() throws InterruptedException {
            signup = new SignUp_POM(driver);
            signup.navigateSignup();
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

        @Test
        void passwordsNotMatching(){
            signup.signingUp("testuser", "random@gmail.com", "Password123!", "Passwod123!");
            assertEquals("http://localhost:3000/signup", driver.getCurrentUrl());
            assertTrue(signup.ErrorMessage.isDisplayed());
            assertEquals("Passwords do not match", signup.ErrorMessage.getText());
        }

        @DisplayName("Testing with set of invalid passwords")
        @ParameterizedTest()
        @CsvSource({
                //Empty space passwords
                "testuser, random@gmail.com, ' ', ' '",
                //7 characters
                "testuser, random@gmail.com, 1234Aa!, 1234Aa!",
                //No capital letters
                "testuser, random@gmail.com, 1234abcd!, 1234abcd!",
                //No lowercase letters
                "testuser, random@gmail.com, 1234ABC@, 1234ABC@",
                //No numbers
                "testuser, random@gmail.com, ABCDabc@, ABCDabc@",
                //No special characters
                "testuser, random@gmail.com, ABC123abc, ABC123abc",
                //Multiple problems
                "testuser, random@gmail.com, '1, ?!£$%^&*()-=, ?', '1, ?!£$%^&*()-=, ?'"
        })
        void testInvalidPassword(String username, String email, String password, String retypePassword){
            signup.signingUp(username, email, password, retypePassword);
            assertEquals("http://localhost:3000/signup", driver.getCurrentUrl());
            assertTrue(signup.ErrorMessage.isDisplayed());
            assertEquals("Password must have at least 8 characters with no spaces and must include at least 1 lowercase letter, 1 uppercase letter, 1 special character and 1 number", signup.ErrorMessage.getText());
        }

        @DisplayName("Testing with set of invalid usernames")
        @ParameterizedTest(name = "{0}")
        @CsvSource({
                //Username with length 5
                "User1, random@gmail.com, Password123!, Password123!",
                //Username with special character
                "User12@, random@gmail.com, Password123!, Password123!",
        })
        void testInvalidUsernames(String username, String email, String password, String retypePassword){
            signup.signingUp(username, email, password, retypePassword);
            assertEquals("http://localhost:3000/signup", driver.getCurrentUrl());
            assertTrue(signup.ErrorMessage.isDisplayed());
            assertEquals("Username must have at least 6 characters and must not include any spaces or special characters", signup.ErrorMessage.getText());
        }

        @Test
        void signupWithValidCredentials(){
            signup.signingUp("Testuser", "testemail@gmail.com", "Password123!", "Password123!");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            wait.until(ExpectedConditions.urlToBe("http://localhost:3000/login"));
            assertEquals("http://localhost:3000/login", driver.getCurrentUrl());
        }


        @DisplayName("Testing with sets of empty fields")
        @ParameterizedTest()
        @CsvSource({
                //Missing username
                "'', random@gmail.com, Password123!, Password123!",
                //Missing email
                "User11, '', Password123!, Password123!",
                //Missing passwords
                "User11, random@gmail.com, '', ''",
                //Missing password retype
                "User11, random@gmail.com, Password123!, ''",
                //Missing first password
                "User11, random@gmail.com, '', Password123!",
                //Missing all
                "'', '', '', ''",
        })
        void testWithEmptyFieds(String username, String email, String password, String retypePassword){
            signup.signingUp(username, email, password, retypePassword);
            assertEquals("http://localhost:3000/signup", driver.getCurrentUrl());
            assertTrue(signup.ErrorMessage.isDisplayed());
            assertEquals("All fields must be filled", signup.ErrorMessage.getText());
        }

        @Test
        void checksPasswordIsHidden(){
            signup.inputPassword.click();
            signup.inputPassword.sendKeys("MyPass123!");
            signup.inputRetypePassword.click();
            signup.inputRetypePassword.sendKeys("MyPass123!");
            assertEquals("password", signup.inputPassword.getAttribute("type"));
            assertEquals("password", signup.inputRetypePassword.getAttribute("type"));
        }

        @Test
        void checksPasswordIsShown(){
            signup.inputPassword.click();
            signup.inputPassword.sendKeys("MyPass123!");
            signup.inputRetypePassword.click();
            signup.inputRetypePassword.sendKeys("MyPass123!");
            signup.buttonShowPassword.click();
            assertEquals("text", signup.inputPassword.getAttribute("type"));
            assertEquals("text", signup.inputRetypePassword.getAttribute("type"));
        }

    }


    @Nested
    @Order(2)
    class Login_TEST {

        static WebDriver driver;
        static Login_POM login;


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
            login = new Login_POM(driver);
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

        private static Iterable<Object[]> getLoginData() throws IOException {
            String filePath = "src/test/resources/login_data.xlsx";
            String sheetName = "Sheet1";

            Workbook workbook = WorkbookFactory.create(new FileInputStream(filePath));
            Sheet sheet = workbook.getSheet(sheetName);

            Iterator<Row> rowIterator = sheet.rowIterator();
            Collection<Object[]> data = new ArrayList<>();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell emailCell = row.getCell(0);
                Cell passwordCell = row.getCell(1);

                String email = emailCell.getStringCellValue().trim();
                String password = passwordCell.getStringCellValue().trim();

                // Skip the row if both email and password are empty
                if (!email.isEmpty() || !password.isEmpty()) {
                    data.add(new Object[]{email, password});
                }
            }

            workbook.close();
            return data;
        }

//        @ParameterizedTest
//        @MethodSource("getLoginData")
//        void testLoginWithCredentials(String email, String password) {
//            login.loggingIn(email, password);
//            // Add assertions for successful login or whatever you need to test
//            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(500));
//            wait.until(ExpectedConditions.urlToBe("http://localhost:3000/lobby"));
//            assertEquals("http://localhost:3000/lobby", driver.getCurrentUrl());
//        }

        @Test void checkPageTitleName() {
            // This title is meant to change as it is currently using the default React title
            assertEquals("Oppo Games", driver.getTitle());
        }

        @Test
        void userLogsInRedirectedToLobby() throws InterruptedException {
            login.loggingIn("testemail@gmail.com", "Password123!");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
            wait.until(ExpectedConditions.urlToBe("http://localhost:3000/"));
            assertEquals("http://localhost:3000/", driver.getCurrentUrl());
            //Clear local storage to avoid being already logged in other tests
            login.clearLocalStorage();
        }

        @Test
        void testWithInvalidCredentials(){
            login.loggingIn("fakeemail@fake.com", "Fakepwd123!");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(500));
            wait.until(ExpectedConditions.visibilityOf(login.loginErrorMessage));
            assertTrue(login.loginErrorMessage.isDisplayed());
            assertEquals("http://localhost:3000/login", driver.getCurrentUrl());
            assertEquals("Enter a valid email or password", login.loginErrorMessage.getText());
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
        void checksPasswordIsHidden() throws InterruptedException {
//            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
//            wait.until(ExpectedConditions.elementToBeClickable(login.inputPassword));
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
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
            wait.until(ExpectedConditions.visibilityOf(login.submitLoginButton));
            login.submitLoginButton.click();
            wait.until(ExpectedConditions.visibilityOf(login.loginErrorMessage));
            assertTrue(login.loginErrorMessage.isDisplayed());
            assertEquals("Enter a valid email or password", login.loginErrorMessage.getText());
        }

        @Test
        void checksRegisterLinkIsPresentAndRedirectsToSignupPage(){
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(500));
            wait.until(ExpectedConditions.elementToBeClickable(login.registerLink));
            assertTrue(login.registerLink.isDisplayed());
            login.registerLink.click();
            assertEquals("http://localhost:3000/signup", driver.getCurrentUrl());
        }


    }
}
