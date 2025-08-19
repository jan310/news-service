package jan.ondra.newsservice.domain.stock.service;

import jan.ondra.newsservice.domain.stock.model.Stock;
import jan.ondra.newsservice.domain.stock.persistence.StockNotExistsException;
import jan.ondra.newsservice.domain.stock.persistence.StockRepository;
import jan.ondra.newsservice.client.yahoofinance.YahooFinanceClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final YahooFinanceClient yahooFinanceClient;

    public StockService(StockRepository stockRepository, YahooFinanceClient yahooFinanceClient) {
        this.stockRepository = stockRepository;
        this.yahooFinanceClient = yahooFinanceClient;
    }

    @Transactional
    public void assignStockToUser(String stockTicker, String userId) {
        try {
            stockRepository.assignStockToUser(stockTicker, userId);
        } catch (StockNotExistsException e) {
            var companyName = yahooFinanceClient.getCompanyNameOfTicker(stockTicker);
            var latestNewsLink = yahooFinanceClient.getNewsLinksForStockTicker(stockTicker).getFirst();
            stockRepository.addStock(new Stock(stockTicker, companyName, latestNewsLink));
            stockRepository.assignStockToUser(stockTicker, userId);
        }
    }

    @Transactional
    public void removeStockFromUser(String stockTicker, String userId) {
        stockRepository.removeStockFromUser(stockTicker, userId);
        stockRepository.removeStockIfUnassigned(stockTicker);
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
