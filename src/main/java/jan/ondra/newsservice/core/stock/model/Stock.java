package jan.ondra.newsservice.core.stock.model;

public record Stock(
    String ticker,
    String companyName,
    String latestNewsLink
) {}
