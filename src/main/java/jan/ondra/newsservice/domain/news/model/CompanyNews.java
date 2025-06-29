package jan.ondra.newsservice.domain.news.model;

import jan.ondra.newsservice.enums.Sentiment;

public record CompanyNews(
    String companyName,
    String stockTicker,
    String newsLink,
    String newsSummary,
    Sentiment newsSentiment
) {}
