package jan.ondra.newsservice.domain.stock.model;

import jan.ondra.newsservice.domain.stock.api.StockResponse;

public record Stock(
    String ticker,
    String companyName,
    String latestNewsLink
) {
    public StockResponse toResponse() {
        return new StockResponse(ticker, companyName);
    }
}
