package jan.ondra.newsservice.core.newsarticle.model;

import jan.ondra.newsservice.common.enums.Sentiment;

public record CompanyNews(
    String companyName,
    String stockTicker,
    String newsLink,
    String newsSummary,
    Sentiment newsSentiment
) {}
