package jan.ondra.newsservice.dto;

import jan.ondra.newsservice.domain.enums.Sentiment;

public record CompanyNews(
    String companyName,
    String stockTicker,
    String newsLink,
    String newsSummary,
    Sentiment newsSentiment
) {}
