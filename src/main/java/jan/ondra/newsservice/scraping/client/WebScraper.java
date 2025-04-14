package jan.ondra.newsservice.scraping.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import static org.springframework.http.HttpHeaders.USER_AGENT;

@Component
public class WebScraper {

    private final RestClient restClient;

    public WebScraper(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
            .defaultHeader(
                USER_AGENT,
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:137.0) Gecko/20100101 Firefox/137.0"
            )
            .build();
    }

    public String getHtml(String uri) {
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
