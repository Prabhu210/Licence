package Origin.Licence;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class Sampletest {

    private WebDriver driver;
    private static boolean loggedIn = false;
    private static final String REMINDER_EMAIL = "prabumohan59@gmail.com"; // Replace with recipient email
    private static final String FROM_EMAIL = "prabhu.m@acviss.com"; // Replace with your email
    private static final String EMAIL_PASSWORD = "xdqy feic tspf fxxn"; // Replace with your email password

    @BeforeClass
    public void setUp() {
        // Set up WebDriver
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("headless");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Login if not already logged in
        if (!loggedIn) {
            try {
                driver.get("https://test.acviss.co/dashboard/login/");
                loggedIn = true;
                driver.findElement(By.id("username")).sendKeys("acvissadmin");
                driver.findElement(By.id("password")).sendKeys("Uniqolabel@123$");
                WebElement button = driver.findElement(By.id("submit"));
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click()", button);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test(priority = 1)
    public void click_on_Licence_Management_dropdown_option() {
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            Select mainCustomerDropDown = new Select(driver.findElement(By.id("selectCustomer")));
            mainCustomerDropDown.selectByVisibleText("Biocon");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 2)
    public void clickonadd_button() {
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            WebElement automationMenuOption = driver.findElement(By.xpath("//aside/div[2]/nav/a[9]"));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click()", automationMenuOption);

            WebElement addButton = driver.findElement(By.xpath("//button[3]/span[3]"));
            js.executeScript("arguments[0].click()", addButton);

            WebElement expiryDateElement = driver.findElement(By.xpath("//div[4]/div/table/tbody/tr[1]/td[7]"));
            String expiryDateText = expiryDateElement.getText().trim();
            System.out.println("Expiry Date: " + expiryDateText);

            // Call the method to check and schedule email reminder
            checkAndScheduleEmailReminder(expiryDateText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkAndScheduleEmailReminder(String expiryDateText) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            LocalDate expiryDate = LocalDate.parse(expiryDateText, formatter);
            System.out.println("Parsed Date: " + expiryDate);
            LocalDate today = LocalDate.now();
            long daysUntilExpiry = Duration.between(today.atStartOfDay(), expiryDate.atStartOfDay()).toDays();
            System.out.println("Days until expiry: " + daysUntilExpiry);

            if (daysUntilExpiry == 5) {
                sendEmailReminder();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendEmailReminder() {
        String subject = "License Expiry Reminder";
        String body = "Reminder: Your license will expire in 5 days.";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(REMINDER_EMAIL));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Reminder email sent successfully to " + REMINDER_EMAIL);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Failed to send reminder email: " + e.getMessage());
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
