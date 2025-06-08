package jan.ondra.newsservice.core.stock.service;

import jan.ondra.newsservice.core.stock.model.Stock;
import jan.ondra.newsservice.core.stock.persistence.StockRepository;
import jan.ondra.newsservice.scheduled.newsgathering.scraping.YahooFinanceParser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final YahooFinanceParser yahooFinanceParser;

    public StockService(StockRepository stockRepository, YahooFinanceParser yahooFinanceParser) {
        this.stockRepository = stockRepository;
        this.yahooFinanceParser = yahooFinanceParser;
    }

    public void assignStockToUser(String stockTicker, String userId) {
        if (stockRepository.stockExists(stockTicker)) {
            stockRepository.assignStockToUser(stockTicker, userId);
        } else {
            var companyName = yahooFinanceParser.getCompanyNameOfTicker(stockTicker);
            var latestNewsLink = yahooFinanceParser.getNewsLinksForStockTicker(stockTicker).getFirst();
            stockRepository.addStockAndAssignToUser(new Stock(stockTicker, companyName, latestNewsLink), userId);
        }
    }

    public void removeStockFromUser(String stockTicker, String userId) {
        stockRepository.removeStockFromUser(stockTicker, userId);
    }

    public void updateLatestNewsLink(String ticker, String newLink) {
        stockRepository.updateLatestNewsLink(ticker, newLink);
    }

    public List<Stock> getStocks() {
        return stockRepository.getStocks();
    }

}
