package jan.ondra.newsservice.domain.news.service;

import jan.ondra.newsservice.client.openai.OpenAiClient;
import jan.ondra.newsservice.client.yahoofinance.YahooFinanceClient;
import jan.ondra.newsservice.domain.news.model.CompanyNews;
import jan.ondra.newsservice.domain.news.model.NewsArticle;
import jan.ondra.newsservice.domain.news.persistence.NewsRepository;
import jan.ondra.newsservice.domain.stock.model.Stock;
import jan.ondra.newsservice.domain.stock.service.StockService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class NewsService {

    private final NewsRepository newsRepository;
    private final StockService stockService;
    private final YahooFinanceClient yahooFinanceClient;
    private final OpenAiClient openAiClient;
    private final JavaMailSender javaMailSender;

    public NewsService(
        NewsRepository newsRepository,
        StockService stockService,
        YahooFinanceClient yahooFinanceClient,
        OpenAiClient openAiClient,
        JavaMailSender javaMailSender
    ) {
        this.newsRepository = newsRepository;
        this.stockService = stockService;
        this.yahooFinanceClient = yahooFinanceClient;
        this.openAiClient = openAiClient;
        this.javaMailSender = javaMailSender;
    }

    /**
     * @return All company news relevant for the specified user, grouped by stock ticker.
     */
    public Map<String, List<CompanyNews>> getCompanyNewsForUserGroupedByTicker(String userId) {
        List<CompanyNews> companyNewsRelevantForUser = newsRepository.getCompanyNewsForUser(userId);

        Map<String, List<CompanyNews>> companyNewsGroupedByTicker = new HashMap<>();

        for (CompanyNews companyNews : companyNewsRelevantForUser) {
            companyNewsGroupedByTicker
                .computeIfAbsent(companyNews.stockTicker(), k -> new ArrayList<>())
                .add(companyNews);
        }

        return companyNewsGroupedByTicker;
    }

    public void gatherNews(LocalDateTime creationTimeStamp) {
        var stocks = stockService.getAllStocks();

        for (Stock stock : stocks) {
            var newsLinks = yahooFinanceClient.getNewsLinksForStockTicker(stock.ticker());

            var indexOfLatestNewsLink = newsLinks.indexOf(stock.latestNewsLink());
            if (indexOfLatestNewsLink != -1) {
                newsLinks.subList(indexOfLatestNewsLink, newsLinks.size()).clear();
            }

            if (newsLinks.isEmpty()) continue;

            stockService.updateLatestNewsLink(stock.ticker(), newsLinks.getFirst());

            for (String link : newsLinks) {
                if (!link.startsWith("https://finance.yahoo.com/")) continue;

                var newsArticle = yahooFinanceClient.getContentFromNewsLink(link);
                var newsArticleAnalysis = openAiClient.evaluateAndSummarizeCompanyNews(
                    stock.companyName(),
                    newsArticle
                );
                if (newsArticleAnalysis.relevant()) {
                    newsRepository.addNewsArticle(
                        new NewsArticle(
                            link,
                            stock.ticker(),
                            newsArticleAnalysis.summary(),
                            newsArticleAnalysis.sentiment(),
                            creationTimeStamp
                        )
                    );
                }
            }
        }
    }

    @Async
    public CompletableFuture<Void> generateAndSendNewsletter(String userId, String notificationEmail) {
        var companyNewsGroupedByTicker = getCompanyNewsForUserGroupedByTicker(userId);

        var newsletterContent = new StringBuilder();

        for (List<CompanyNews> companyNewsList : companyNewsGroupedByTicker.values()) {
            newsletterContent
                .append("News for ")
                .append(companyNewsList.getFirst().companyName())
                .append(" (")
                .append(companyNewsList.getFirst().stockTicker())
                .append(")\n");

            for (CompanyNews companyNews : companyNewsList) {
                newsletterContent
                    .append("Sentiment: ")
                    .append(companyNews.newsSentiment())
                    .append("\n")
                    .append(companyNews.newsSummary())
                    .append("\nSource: ")
                    .append(companyNews.newsLink())
                    .append("\n\n");
            }

            newsletterContent.append("\n");
        }

        var message = new SimpleMailMessage();
        message.setTo(notificationEmail);
        message.setSubject("Daily News-Service Newsletter");
        message.setText(newsletterContent.toString());
        javaMailSender.send(message);

        return CompletableFuture.completedFuture(null);
    }

    public void deleteNewsArticlesCreatedAtOrBefore(LocalDateTime localDateTime) {
        newsRepository.deleteNewsArticlesCreatedAtOrBefore(localDateTime);
    }

}
