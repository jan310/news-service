package jan.ondra.newsservice.business;

import jan.ondra.newsservice.ai.OpenAiClient;
import jan.ondra.newsservice.domain.models.NewsArticle;
import jan.ondra.newsservice.persistence.NewsArticleRepository;
import jan.ondra.newsservice.persistence.StockRepository;
import jan.ondra.newsservice.scraping.YahooFinanceScraper;
import org.springframework.stereotype.Service;

@Service
public class NewsGatheringService {

    private final StockRepository stockRepository;
    private final NewsArticleRepository newsArticleRepository;
    private final YahooFinanceScraper yahooFinanceScraper;
    private final OpenAiClient openAiClient;

    public NewsGatheringService(
        StockRepository stockRepository,
        NewsArticleRepository newsArticleRepository,
        YahooFinanceScraper yahooFinanceScraper,
        OpenAiClient openAiClient
    ) {
        this.stockRepository = stockRepository;
        this.yahooFinanceScraper = yahooFinanceScraper;
        this.openAiClient = openAiClient;
        this.newsArticleRepository = newsArticleRepository;
    }

    public void gatherNews() {
        var stocks = stockRepository.getStocks();

        stocks.forEach(stock -> {
            var newsLinks = yahooFinanceScraper.getNewsLinksForStockTicker(stock.ticker());

            var indexOfLatestNewsLink = newsLinks.indexOf(stock.latestNewsLink());
            if (indexOfLatestNewsLink != -1) {
                newsLinks.subList(indexOfLatestNewsLink, newsLinks.size()).clear();
            }

            if (!newsLinks.isEmpty()) {
                stockRepository.updateLatestNewsLink(stock.ticker(), newsLinks.getFirst());

                newsLinks.forEach(link -> {
                    var newsArticle = yahooFinanceScraper.getContentFromNewsLink(link);
                    var newsArticleAnalysis = openAiClient.evaluateAndSummarizeCompanyNews(
                        stock.companyName(),
                        newsArticle
                    );
                    if (newsArticleAnalysis.relevant()) {
                        newsArticleRepository.addNewsArticleToStock(
                            new NewsArticle(link, newsArticleAnalysis.summary(), newsArticleAnalysis.sentiment()),
                            stock.ticker()
                        );
                    }
                });
            }
        });
    }

}
