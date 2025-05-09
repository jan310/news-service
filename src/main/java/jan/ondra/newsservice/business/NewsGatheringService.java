package jan.ondra.newsservice.business;

import jan.ondra.newsservice.ai.OpenAiClient;
import jan.ondra.newsservice.domain.models.NewsArticle;
import jan.ondra.newsservice.domain.models.Stock;
import jan.ondra.newsservice.persistence.NewsArticleRepository;
import jan.ondra.newsservice.persistence.StockRepository;
import jan.ondra.newsservice.scraping.YahooFinanceParser;
import org.springframework.stereotype.Service;

@Service
public class NewsGatheringService {

    private final StockRepository stockRepository;
    private final NewsArticleRepository newsArticleRepository;
    private final YahooFinanceParser yahooFinanceParser;
    private final OpenAiClient openAiClient;

    public NewsGatheringService(
        StockRepository stockRepository,
        NewsArticleRepository newsArticleRepository,
        YahooFinanceParser yahooFinanceParser,
        OpenAiClient openAiClient
    ) {
        this.stockRepository = stockRepository;
        this.yahooFinanceParser = yahooFinanceParser;
        this.openAiClient = openAiClient;
        this.newsArticleRepository = newsArticleRepository;
    }

    public void gatherNews() {
        var stocks = stockRepository.getStocks();

        for (Stock stock : stocks) {
            var newsLinks = yahooFinanceParser.getNewsLinksForStockTicker(stock.ticker());

            var indexOfLatestNewsLink = newsLinks.indexOf(stock.latestNewsLink());
            if (indexOfLatestNewsLink != -1) {
                newsLinks.subList(indexOfLatestNewsLink, newsLinks.size()).clear();
            }

            if (newsLinks.isEmpty()) continue;

            stockRepository.updateLatestNewsLink(stock.ticker(), newsLinks.getFirst());

            for (String link : newsLinks) {
                if (!link.startsWith("https://finance.yahoo.com/")) continue;

                var newsArticle = yahooFinanceParser.getContentFromNewsLink(link);
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
            }
        }
    }

}
