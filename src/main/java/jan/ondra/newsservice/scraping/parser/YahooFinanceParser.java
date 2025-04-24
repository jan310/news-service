package jan.ondra.newsservice.scraping.parser;

import jan.ondra.newsservice.scraping.client.WebScraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceParser {

    private final WebScraper webScraper;

    public YahooFinanceParser(WebScraper webScraper) {
        this.webScraper = webScraper;
    }

    public List<String> getNewsLinksForStockTicker(String ticker) {
        var doc = Jsoup.parse(webScraper.getHtml("https://finance.yahoo.com/quote/" + ticker + "/news/"));

        var listItems = doc.select(".stream-item.story-item.yf-1usaaz9");

        var links = new ArrayList<String>();

        for (Element li : listItems) {
            Element section = li.selectFirst("section");
            if (section != null) {
                Element link = section.selectFirst("a");
                if (link != null) {
                    String href = link.attr("href");
                    if (!href.isEmpty()) {
                        links.add(href);
                    }
                }
            }
        }

        return links;
    }

    public String getContentFromNewsLink(String link) {
        var doc = Jsoup.parse(webScraper.getHtml(link));

        var paragraphs = doc.select("p.yf-1090901");

        var stringBuilder = new StringBuilder();

        for (Element paragraph : paragraphs) {
            if (paragraph.text().contains("was originally created and published by")) break;

            stringBuilder.append(paragraph.text()).append(" ");
        }

        return stringBuilder.toString();
    }

    public String getCompanyNameOfTicker(String ticker) {
        var doc = Jsoup.parse(webScraper.getHtml("https://finance.yahoo.com/quote/" + ticker));

        var docTitle = doc.title();

        return docTitle.substring(0, docTitle.indexOf('(') - 1);
    }

}
