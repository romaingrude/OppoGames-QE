
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


public class Login_POM {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "email")
    public WebElement inputEmail;

    @FindBy(id = "password")
    public WebElement inputPassword;

    @FindBy(id = "login-button")
    public WebElement submitLoginButton;

    @FindBy(linkText = "Register")
    public WebElement registerLink;

    @FindBy(id = "toggle-pw-visibility-button")
    public WebElement passwordShowButton;

    @FindBy(css = "p[aria-label='Login Error Message']")
    public WebElement loginErrorMessage;




    public Login_POM(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(3));
    }

    public void navigateLogin(){
        driver.get("http://localhost:3000/login");
    }

    public void loggingIn(String email, String password){
        WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(inputEmail));
        emailInput.click();
        emailInput.sendKeys(email);

        WebElement passwordInput = wait.until(ExpectedConditions.elementToBeClickable(inputPassword));
        passwordInput.click();
        passwordInput.sendKeys(password);

        submitLoginButton.click();
    }



    public void clearLocalStorage(){
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("window.localStorage.clear();");
    }


}
