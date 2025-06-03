package jan.ondra.newsservice.core.stock.persistence;

record StockDTO(
    String ticker,
    String companyName,
    String latestNewsLink
) {}
