package jan.ondra.newsservice.scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpHeaders.USER_AGENT;

@Component
public class YahooFinanceScraper {

    private final RestClient restClient;

    public YahooFinanceScraper(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
            .defaultHeader(
                USER_AGENT,
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:137.0) Gecko/20100101 Firefox/137.0"
            )
            .build();
    }

    public List<String> getNewsLinksForStockTicker(String ticker) {
        var doc = Jsoup.parse(getWebContent("https://finance.yahoo.com/quote/" + ticker + "/news/"));

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
        var doc = Jsoup.parse(getWebContent(link));

        var paragraphs = doc.select("p.yf-1090901");

        var stringBuilder = new StringBuilder();

        for (Element paragraph : paragraphs) {
            if (paragraph.text().contains("was originally created and published by")) break;

            stringBuilder.append(paragraph.text()).append(" ");
        }

        return stringBuilder.toString();
    }

    public String getCompanyNameOfTicker(String ticker) {
        var doc = Jsoup.parse(getWebContent("https://finance.yahoo.com/quote/" + ticker));

        var docTitle = doc.title();

        return docTitle.substring(0, docTitle.indexOf('(') - 1);
    }

    private String getWebContent(String uri) {
        String websiteContent;

        try {
            websiteContent = restClient
                .get()
                .uri(uri)
                .retrieve()
                .body(String.class);
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }

        return websiteContent;
    }

}
