import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class Crawler {

    // location of chomedriver.exe
    private final static String DRIVER_LOC = "C:\\WebDrivers\\chromedriver.exe";

    public static void main(String[] args) {

        // set the location of chromedriver.exe
        System.setProperty("webdriver.chrome.driver", DRIVER_LOC);

        // set options for chromedriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");

        // start chrome driver
        WebDriver driver = new ChromeDriver(options);

        String profileUrl = "https://www.instagram.com/loeya/";
        List<String> urls = getUrls(driver, profileUrl);

        System.out.println(urls);

        ArrayList<Post> posts = new ArrayList<Post>();

        for (String url : urls) {
            posts.add(parsePost(driver, url));
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("C:\\Users\\Yalchin Aliyev\\Desktop\\comment.json"), posts);
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver.close();
        driver.quit();
    }

    private static Post parsePost(WebDriver driver, String url) {
        try {
            Post post = new Post();

            driver.get(url);

            // get full article
            WebElement article = driver.findElement(By.tagName("article"));
            List<WebElement> commentSection = article.findElements(By.tagName("li"));

            post.setUrl(url);
            post.setUser(article.findElement(By.tagName("header"))
                                .findElements(By.tagName("div")).get(1)
                                .findElement(By.tagName("a")).getText());

            WebElement moreCommentsButton = null;
            try {
                moreCommentsButton = commentSection.get(0).findElement(By.tagName("button"));
            }
            catch (NoSuchElementException ex) {
                System.out.println("No load button");
            }

            try {
                if (commentSection.get(0).findElement(By.tagName("h2")).getText().equals(post.getUser())) {
                    post.setCaption(commentSection.get(0).findElement(By.tagName("span")).getText());
                    moreCommentsButton = commentSection.get(1).findElement(By.tagName("button"));
                }
            }
            catch (NoSuchElementException ex) {
                System.out.println("No caption!");
            }

            try {
                while (moreCommentsButton != null && moreCommentsButton.isDisplayed() && moreCommentsButton.isEnabled()) {
                    moreCommentsButton.click();
                    Thread.sleep(500);
                }
            }
            catch (StaleElementReferenceException ex) {
                System.out.println("All comments are loaded!");
            }

            commentSection = driver.findElement(By.tagName("article")).findElements(By.tagName("li"));

            for (int i = 1; i < commentSection.size(); i++) {
                WebElement comment = commentSection.get(i);

                post.getComments().add(new Comment(comment.findElement(By.tagName("h3")).getText(),
                                                   comment.findElement(By.tagName("span")).getText()));
            }

            return post;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            driver.close();
            driver.quit();

            return null;
        }
    }

    private static List<String> getUrls(WebDriver driver, String profileUrl) {

        driver.get(profileUrl);

        WebElement article = driver.findElement(By.tagName("article"));
        List<WebElement> urlElements = article.findElements(By.tagName("a"));

        List<String> urls = new ArrayList<String>();

        for (WebElement element : urlElements) {
            urls.add(element.getAttribute("href"));

            if (urls.size() == 3)
                break;
        }

        return urls;
    }
}
