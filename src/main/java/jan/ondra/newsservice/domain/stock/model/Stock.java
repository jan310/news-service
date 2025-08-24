package jan.ondra.newsservice.domain.stock.model;

import jan.ondra.newsservice.domain.stock.api.StockDTO;

public record Stock(
    String ticker,
    String companyName,
    String latestNewsLink
) {
    public StockDTO toStockDTO() {
        return new StockDTO(ticker, companyName);
    }
}
