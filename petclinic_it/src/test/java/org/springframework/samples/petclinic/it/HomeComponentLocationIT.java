package org.springframework.samples.petclinic.it;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Assume;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeComponentLocationIT extends SeleniumIntegrationTestBase {

	private static final Logger logger = LoggerFactory.getLogger(HomeComponentLocationIT.class);

	@Test
	public void testChrome() throws MalformedURLException, IOException {

		Assume.assumeTrue(RUN_CHROME);

		logger.info("executing test in chrome");

		WebDriver driver = null;
		try {
			Capabilities browser = new ChromeOptions();
			driver = new RemoteWebDriver(new URL(SELENIUM_HUB_URL), browser);
			testHomeComponentLocation(driver, TARGET_SERVER_URL);
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
            testHomeComponentLocation(driver, TARGET_SERVER_URL);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
	}

	public void testHomeComponentLocation(final WebDriver driver, final String baseUrl) {

		driver.get(baseUrl);

		// wait for the application to get fully loaded
		(new WebDriverWait(driver, 25)).until(d -> d.findElement(By.linkText("Find owner")));

		logger.info("looking for elements in root of DOM");

		List<WebElement> elements = driver.findElements(By.xpath("//*"));

		logElements(elements);
		findElementAtCoordinates(elements, 100, 100).getTagName();
		findElementAtCoordinates(elements, 240, 100).getTagName();

		logger.info("looking for elements under top div element");

		elements = driver.findElements(By.xpath("//div/*"));

		logElements(elements);
		findElementAtCoordinates(elements, 100, 100);
		findElementAtCoordinates(elements, 240, 100);
	}

	private void logElements(List<WebElement> elements) {

		for (WebElement e : elements) {

			Point loc = e.getLocation();
			Dimension size = e.getSize();

			logger.info("  found element {} at position ({},{}) with size ({},{})", e.getTagName(), loc.getX(),
					loc.getY(), size.getWidth(), size.getHeight());
		}
	}

	private WebElement findElementAtCoordinates(List<WebElement> elements, int x, int y) {

		for (WebElement e : elements) {

			Point pos = e.getLocation();
			Dimension size = e.getSize();

			if (x >= pos.getX() && x <= pos.getX() + size.getWidth() && y >= pos.getY()
					&& y <= pos.getY() + size.getHeight()) {

				logger.info("  found element {} at position ({},{})", e.getTagName(), x, y);

				return e;
			}
		}

		logger.info("  no element found at position ({},{})", x, y);

		return null;
	}
}
