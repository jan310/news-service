package jan.ondra.newsservice.scheduled.newsgathering.service;

import jan.ondra.newsservice.core.newsarticle.model.NewsArticle;
import jan.ondra.newsservice.core.newsarticle.service.NewsArticleService;
import jan.ondra.newsservice.core.stock.model.Stock;
import jan.ondra.newsservice.core.stock.service.StockService;
import jan.ondra.newsservice.scheduled.newsgathering.analysis.OpenAiClient;
import jan.ondra.newsservice.scheduled.newsgathering.scraping.YahooFinanceParser;
import org.springframework.stereotype.Service;

@Service
public class NewsGatheringService {

    private final StockService stockService;
    private final NewsArticleService newsArticleService;
    private final YahooFinanceParser yahooFinanceParser;
    private final OpenAiClient openAiClient;

    public NewsGatheringService(
        StockService stockService,
        NewsArticleService newsArticleService,
        YahooFinanceParser yahooFinanceParser,
        OpenAiClient openAiClient
    ) {
        this.stockService = stockService;
        this.yahooFinanceParser = yahooFinanceParser;
        this.openAiClient = openAiClient;
        this.newsArticleService = newsArticleService;
    }

    public void gatherNews() {
        var stocks = stockService.getStocks();

        for (Stock stock : stocks) {
            var newsLinks = yahooFinanceParser.getNewsLinksForStockTicker(stock.ticker());

            var indexOfLatestNewsLink = newsLinks.indexOf(stock.latestNewsLink());
            if (indexOfLatestNewsLink != -1) {
                newsLinks.subList(indexOfLatestNewsLink, newsLinks.size()).clear();
            }

            if (newsLinks.isEmpty()) continue;

            stockService.updateLatestNewsLink(stock.ticker(), newsLinks.getFirst());

            for (String link : newsLinks) {
                if (!link.startsWith("https://finance.yahoo.com/")) continue;

                var newsArticle = yahooFinanceParser.getContentFromNewsLink(link);
                var newsArticleAnalysis = openAiClient.evaluateAndSummarizeCompanyNews(
                    stock.companyName(),
                    newsArticle
                );
                if (newsArticleAnalysis.relevant()) {
                    newsArticleService.addNewsArticleToStock(
                        new NewsArticle(link, newsArticleAnalysis.summary(), newsArticleAnalysis.sentiment()),
                        stock.ticker()
                    );
                }
            }
        }
    }

}
