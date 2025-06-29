package jan.ondra.newsservice.domain.news.model;

import jan.ondra.newsservice.enums.Sentiment;

public record NewsArticleAnalysis(
    boolean relevant,
    Sentiment sentiment,
    String summary
) {}
