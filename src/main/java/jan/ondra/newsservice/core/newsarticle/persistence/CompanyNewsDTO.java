package jan.ondra.newsservice.core.newsarticle.persistence;

import jan.ondra.newsservice.common.enums.Sentiment;

public record CompanyNewsDTO(
    String companyName,
    String stockTicker,
    String newsLink,
    String newsSummary,
    Sentiment newsSentiment
) {}
