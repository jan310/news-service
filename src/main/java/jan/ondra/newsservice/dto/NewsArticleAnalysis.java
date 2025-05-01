package jan.ondra.newsservice.dto;

import jan.ondra.newsservice.domain.enums.Sentiment;

public record NewsArticleAnalysis(boolean relevant, Sentiment sentiment, String summary) {}
