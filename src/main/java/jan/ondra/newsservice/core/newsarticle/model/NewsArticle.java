package jan.ondra.newsservice.core.newsarticle.model;

import jan.ondra.newsservice.common.enums.Sentiment;

public record NewsArticle(
    String link,
    String summary,
    Sentiment sentiment
) {}
