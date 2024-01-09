package com.heima.wemedia;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.print.Doc;
import java.io.IOException;

/**
 * ClassName: ReptilsDemo
 * Package: com.heima.wemedia
 * Description:
 *
 * @Author R
 * @Create 2024/1/9 12:13
 * @Version 1.0
 */
public class ReptilsDemo {
    @Test
    public void test() throws IOException {
        System.setProperty("webdriver.chrome.driver", "E:\\Java_Project\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("https://m.163.com/touch/news/sub/history/?ver=c&clickfrom=index2018_header_main");
        Document  document = Jsoup.parse(driver.getPageSource());
//        Document document = Jsoup.connect("https://3g.163.com/touch/news/sub/history/?ver=c&clickfrom=index2018_header_main")
//                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36")
//                .get();
        Elements article = document.getElementsByTag("article");
        for (Element element : article) {
            Elements h4 = element.getElementsByTag("h4");
            System.out.println(h4);
        }

    }
}
