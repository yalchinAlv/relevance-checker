import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.vdurmont.emoji.EmojiParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Crawler {

    // location of chomedriver.exe
    private final static String DRIVER_LOC = "C:\\WebDrivers\\chromedriver.exe";

    public static void main(String[] args) {

        // set the location of chromedriver.exe
//        System.setProperty("webdriver.chrome.driver", DRIVER_LOC);
//
//        // set options for chromedriver
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("start-maximized");
//        options.addArguments("disable-infobars");
//
//        // start chrome driver
//        WebDriver driver = new ChromeDriver(options);

//        String profileUrl = "https://www.instagram.com/loeya/";
//        List<String> urls = getUrls(driver, profileUrl);
//
//        System.out.println(urls);
//
//        ArrayList<Post> posts = new ArrayList<Post>();
//
//        for (String url : urls) {
//            posts.add(parsePost(driver, url));
//        }

//        long startTime = System.currentTimeMillis();
//        Post post = parsePost(driver, "https://www.instagram.com/p/BqIX9H2gc2V/");
//        System.out.println("Elapse time: " + (System.currentTimeMillis() - startTime) / 1000 + " s");
//
//        ArrayList<String> comments = new ArrayList<String>();
//        for (Comment comment : post.getComments())
//            comments.add(comment.getText());
//
////        System.out.println(post.getComments());
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            mapper.writeValue(new File("comments\\posts6.json"), post);
//            mapper.writeValue(new File("comments\\comments6.json"), comments);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        driver.close();
//        driver.quit();

        ObjectMapper mapper = new ObjectMapper();
        ArrayList<String> comments = null;
        try {
            comments = mapper.readValue(new File("comments\\comments2.json"), ArrayList.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder fileContent = new StringBuilder();
        for (int i = 0; i < comments.size(); i ++) {

            fileContent.append(i);
            fileContent.append(" ");
            fileContent.append(filterComment(comments.get(i)));
            fileContent.append("\n");
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("comments\\comments2.txt"));
            writer.write(fileContent.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            int startingComment = 1;
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
                startingComment = 0;
            }

            try {
                while(true) {
                    while (moreCommentsButton != null && moreCommentsButton.isDisplayed() && moreCommentsButton.isEnabled()) {
                        moreCommentsButton.click();
                        Thread.sleep(500);
                    }

                    commentSection = driver.findElement(By.tagName("article")).findElements(By.tagName("li"));

                    try {
                        // check if the load button is really disappeared
                        commentSection.get(startingComment).findElement(By.tagName("h3"));
                    } catch (NoSuchElementException ex) {
                        System.out.println("Load button is still there");
                        continue;
                    }
                    break;
                }

                System.out.println("Comments are loaded gracefully");
            }
            catch (StaleElementReferenceException ex) {
                System.out.println("All comments are loaded!");
            }

            commentSection = driver.findElement(By.tagName("article")).findElements(By.tagName("li"));
            System.out.println("Num of comments: " + (commentSection.size() - startingComment));

            System.out.println("Parsing comments");

            // use jsoup for parsing the comments
            parseComments(driver.findElement(By.tagName("article")).getAttribute("outerHTML"), post, startingComment);

//            for (int i = startingComment; i < commentSection.size(); i++) {
//                WebElement comment = commentSection.get(i);
//
//                post.getComments().add(new Comment(comment.findElement(By.tagName("h3")).getText(),
//                                                   comment.findElement(By.tagName("span")).getText(), i));
//
//                System.out.print("\r");
//                System.out.print("[" + "####################".substring(19 - i * 20 / commentSection.size()) + "                    ".substring(i * 20 / commentSection.size() + 1) + "] " + (i * 100 / commentSection.size() + 1) + "%");
//            }

            return post;
        }
        catch (Exception ex) {
            ex.printStackTrace();

            new Scanner(System.in).nextLine();
            driver.close();
            driver.quit();

            return null;
        }
    }

    private static final String[] stopwords = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "you're", "you've", "you'll", "you'd", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "she's", "her", "hers", "herself", "it", "it's", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "that'll", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "don't", "should", "should've", "now", "d", "ll", "m", "o", "re", "ve", "y", "ain", "aren", "aren't", "couldn", "couldn't", "didn", "didn't", "doesn", "doesn't", "hadn", "hadn't", "hasn", "hasn't", "haven", "haven't", "isn", "isn't", "ma", "mightn", "mightn't", "mustn", "mustn't", "needn", "needn't", "shan", "shan't", "shouldn", "shouldn't", "wasn", "wasn't", "weren", "weren't", "won", "won't", "wouldn", "wouldn't"};

    private static String filterComment(String comment) {

        StringBuilder fixed = new StringBuilder();

        String filtered = comment.replaceAll("[^\\x00-\\x7F]", "")
                                .replace("\n", "")
                                .replaceAll("\\p{Punct}", "");

        for (String stopword : stopwords)
            filtered = filtered.replace(" " + stopword + " ", "");

        fixed.append(filtered);

        for (String emoji : EmojiParser.extractEmojis(comment)) {
            fixed.append(" ").append(emoji);
        }

        return fixed.toString();
    }

    private static void parseComments(String source, Post post, int start) {
        Document doc = Jsoup.parse(source);
        Elements commentSection = doc.getElementsByTag("li");

        for (int i = start; i < commentSection.size(); i++) {
            Element comment = commentSection.get(i);

            post.getComments().add(new Comment(comment.getElementsByTag("h3").text(),
                                               comment.getElementsByTag("span").text(), i));

            System.out.print("\r");
            System.out.print("[" + "####################".substring(19 - i * 20 / commentSection.size()) + "                    ".substring(i * 20 / commentSection.size() + 1) + "] " + (i * 100 / commentSection.size() + 1) + "%");

//            System.out.println(comment.getElementsByTag("h3").text());
//            System.out.println(comment.getElementsByTag("span").text() + "\n");

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
