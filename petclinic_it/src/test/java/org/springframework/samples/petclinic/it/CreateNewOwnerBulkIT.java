package org.springframework.samples.petclinic.it;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateNewOwnerBulkIT extends SeleniumIntegrationTestBase{

    private static final Logger logger = LoggerFactory.getLogger(CreateNewOwnerBulkIT.class);

    private static String TEST_DATA_FILE_NAME;

    @BeforeClass
    public static void initEnvironment() {
    	SeleniumIntegrationTestBase.initEnvironment();
        TEST_DATA_FILE_NAME = getConfigurationProperty(
            "TEST_DATA_FILE_NAME",
            "test.data.fileName",
            "/test-data.csv");

        logger.info("using test data at: " + TEST_DATA_FILE_NAME);
    }

    @Test
    public void testChrome()
        throws MalformedURLException, IOException {

        Assume.assumeTrue(RUN_CHROME);

        logger.info("executing test in chrome");

        WebDriver driver = null;
        try {
            Capabilities browser = new ChromeOptions();
            driver = new RemoteWebDriver(new URL(SELENIUM_HUB_URL), browser);
            createNewUserBulk(driver, TARGET_SERVER_URL);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
    
    @Test
	public void testHtmlUnit() throws MalformedURLException, IOException {

        Assume.assumeTrue(RUN_HTMLUNIT);

        logger.info("executing test in htmlunit");

        WebDriver driver = null;

        try {
            driver = new HtmlUnitDriver(true);
            createNewUserBulk(driver, TARGET_SERVER_URL);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
	}

    private void createNewUserBulk(final WebDriver driver, final String baseUrl) {

        Optional<List<String>> testData = readTestData();
        if (testData.isPresent()) {
            for (String testDataLine : testData.get()) {
                Optional<Owner> owner = parseOwner(testDataLine);
                if (owner.isPresent()) {
                    createNewUser(driver, baseUrl, owner.get());
                }
            }
        }
    }

    private void createNewUser(final WebDriver driver, final String baseUrl, Owner owner) {

        driver.get(baseUrl + "/owners/new");

        (new WebDriverWait(driver, 25)).until(
            d -> d.getCurrentUrl().startsWith(baseUrl + "/owners/new"));

        driver.findElement(By.id("firstName")).sendKeys(owner.getFirstName());
        driver.findElement(By.id("lastName")).sendKeys(owner.getLastName());
        driver.findElement(By.id("address")).sendKeys(owner.getAddress());
        driver.findElement(By.id("city")).sendKeys(owner.getCity());
        driver.findElement(By.id("telephone")).sendKeys(owner.getTelephone());

        driver.findElement(By.id("addowner")).click();

        (new WebDriverWait(driver, 5)).until(
            d -> d.getCurrentUrl().startsWith(baseUrl + "/owners/")
                && !d.getCurrentUrl().contains("/new"));

        assertTrue(driver.getPageSource().contains(owner.getFirstName()));
        assertTrue(driver.getPageSource().contains(owner.getLastName()));
        assertTrue(driver.getPageSource().contains(owner.getAddress()));
        assertTrue(driver.getPageSource().contains(owner.getCity()));
        assertTrue(driver.getPageSource().contains(owner.getTelephone()));

        String ownerId = driver.getCurrentUrl().substring(driver.getCurrentUrl().lastIndexOf('/') + 1);

        logger.info("owner {} {} with id {} successfully created", owner.getFirstName(), owner.getLastName(), ownerId);
    }

    private Optional<List<String>> readTestData() {

        try {
            File testDataFile = new File(this.getClass().getResource(TEST_DATA_FILE_NAME).toURI());
            return Optional.of(readTextFile(testDataFile, 1024));
        } catch (IOException | URISyntaxException e) {
            return Optional.empty();
        }
    }

    private List<String> readTextFile(File file, int bufferSize)
        throws IOException {

        List<String> contents = new ArrayList<String>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                contents.add(line);
            }

            return contents;
        }
    }

    private Optional<Owner> parseOwner(String testDataLine) {
        String[] testDataTokens = testDataLine.split("\t");
        if (testDataTokens.length != 5) {
            logger.warn("insufficient test data in line: " + testDataLine);
            return Optional.empty();
        } else {
            Owner owner = new Owner();
            owner.setFirstName(testDataTokens[0]);
            owner.setLastName(testDataTokens[1]);
            owner.setAddress(testDataTokens[2]);
            owner.setCity(testDataTokens[3]);
            owner.setTelephone(testDataTokens[4]);
            return Optional.of(owner);
        }
    }
}
