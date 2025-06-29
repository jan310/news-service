package jan.ondra.newsservice.domain.stock.service;

import jan.ondra.newsservice.domain.stock.model.Stock;
import jan.ondra.newsservice.domain.stock.persistence.StockRepository;
import jan.ondra.newsservice.client.yahoofinance.YahooFinanceClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final YahooFinanceClient yahooFinanceClient;

    public StockService(StockRepository stockRepository, YahooFinanceClient yahooFinanceClient) {
        this.stockRepository = stockRepository;
        this.yahooFinanceClient = yahooFinanceClient;
    }

    public void assignStockToUser(String stockTicker, String userId) {
        if (stockRepository.stockExists(stockTicker)) {
            stockRepository.assignStockToUser(stockTicker, userId);
        } else {
            var companyName = yahooFinanceClient.getCompanyNameOfTicker(stockTicker);
            var latestNewsLink = yahooFinanceClient.getNewsLinksForStockTicker(stockTicker).getFirst();
            stockRepository.addStockAndAssignToUser(new Stock(stockTicker, companyName, latestNewsLink), userId);
        }
    }

    public void removeStockFromUser(String stockTicker, String userId) {
        stockRepository.removeStockFromUser(stockTicker, userId);
    }

    public void updateLatestNewsLink(String ticker, String newLink) {
        stockRepository.updateLatestNewsLink(ticker, newLink);
    }

    public List<Stock> getAllStocks() {
        return stockRepository.getAllStocks();
    }

    public List<Stock> getStocksForUser(String userId) {
        return stockRepository.getStocksForUser(userId);
    }

}
