package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.WebDriverManager;

import java.util.List;

public class AmazonCartTest {
    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = WebDriverManager.getDriver();
    }

    @Test
    public void addToysToCartAndValidatePrices() throws InterruptedException {
        driver.get("https://www.amazon.com");

        // Search for toys
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys("toys");
        driver.findElement(By.id("nav-search-submit-button")).click();

               List<WebElement> productLinks = driver.findElements(By.cssSelector("h2 .a-link-normal"));
        List<WebElement> productPrices = driver.findElements(By.cssSelector(".a-price"));

        // Store price of first product from the search results page
        String firstProductPrice = productPrices.get(1).getText();
        

        // Select first product
        productLinks.get(1).click();
        String firstDetailsPrice = driver.findElement(By.id("corePrice_feature_div")).getText();
        
        if (!firstProductPrice.contains(firstDetailsPrice)) {
        	
            System.out.println("Price mismatch for the first product");
        }
        else {
        	System.out.println("Price matches for the first product");
        }


        // Add first product to cart
        driver.findElement(By.id("add-to-cart-button")).click();
        Thread.sleep(2000);

        // Navigate back to search results
        driver.navigate().back();
        driver.navigate().back();

        productLinks = driver.findElements(By.cssSelector("h2 .a-link-normal"));
        productPrices = driver.findElements(By.cssSelector(".a-price"));
        
        // Store price of first product from the search results page
        String secondProductPrice = productPrices.get(0).getText();
        
        // Select second product
        productLinks.get(0).click();
        String secondDetailsPrice = driver.findElement(By.id("corePrice_feature_div")).getText();
        
        if (!secondProductPrice.contains(secondDetailsPrice)) {
            System.out.println("Price mismatch for the second product");
        }
        else {
        	System.out.println("Price matches for the second product");
        }

        // Add second product to cart
        driver.findElement(By.id("add-to-cart-button")).click();
        Thread.sleep(2000);

        // Open cart
        driver.findElement(By.id("nav-cart")).click();

        // Validate prices in the cart
        List<WebElement> cartPrices = driver.findElements(By.cssSelector(".sc-product-price"));
        if(cartPrices.get(0).getText().contains(firstProductPrice)||cartPrices.get(0).getText().contains(secondProductPrice))
        	{
        		System.out.println("Cart price matches for the first product");
        }
        else {
        	System.out.println("Cart price mismatch for the first product");
        }

        if(cartPrices.get(1).getText().contains(firstProductPrice)||cartPrices.get(1).getText().contains(secondProductPrice))
        	{
        		System.out.println("Cart price matches for the second product");
        }
        else {
        	System.out.println("Cart price mismatch for the second product");
        }
    }

    @AfterMethod
    public void tearDown() {
        WebDriverManager.quitDriver();
    }
}
