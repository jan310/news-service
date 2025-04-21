package jan.ondra.newsservice.domain.models;

import jan.ondra.newsservice.domain.enums.Sentiment;

public record NewsArticle(String link, String summary, Sentiment sentiment) {}
