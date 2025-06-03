package jan.ondra.newsservice.scheduled.newsgathering.model;

import jan.ondra.newsservice.common.enums.Sentiment;

public record NewsArticleAnalysis(
    boolean relevant,
    Sentiment sentiment,
    String summary
) {}
