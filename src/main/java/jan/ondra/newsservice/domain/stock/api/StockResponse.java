package jan.ondra.newsservice.domain.stock.api;

public record StockResponse(
    String ticker,
    String companyName
) {}
