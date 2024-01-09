import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SignUp_POM {


    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "username")
    public WebElement inputUsername;

    @FindBy(id = "email")
    public WebElement inputEmail;

    @FindBy(id = "password")
    public WebElement inputPassword;

    @FindBy(id = "retype-password")
    public WebElement inputRetypePassword;

    @FindBy(id = "toggle-pw-visibility-button")
    public WebElement buttonShowPassword;

    @FindBy(id = "submit")
    public WebElement submitButton;

    @FindBy(linkText = "Log in")
    public WebElement LogInLink;

    @FindBy(css = "p[class*='errorMessage']")
    public WebElement ErrorMessage;
    






    public SignUp_POM(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(3));
    }

    void navigateSignup(){
        driver.get("http://localhost:3000/signup");
    }

    void signingUp(String username, String email, String password, String passwordRetype){
        wait.until(ExpectedConditions.elementToBeClickable(inputUsername));
        inputUsername.click();
        inputUsername.sendKeys(username);
        wait.until(ExpectedConditions.elementToBeClickable(inputEmail));
        inputEmail.click();
        inputEmail.sendKeys(email);
        wait.until(ExpectedConditions.elementToBeClickable(inputPassword));
        inputPassword.click();
        inputPassword.sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(inputRetypePassword));
        inputRetypePassword.click();
        inputRetypePassword.sendKeys(passwordRetype);
        submitButton.click();
    }

}
