package jan.ondra.newsservice.domain.news.model;

import jan.ondra.newsservice.enums.Sentiment;

public record NewsArticle(
    String link,
    String summary,
    Sentiment sentiment
) {}
