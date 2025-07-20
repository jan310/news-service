package jan.ondra.newsservice.domain.news.model;

import jan.ondra.newsservice.enums.Sentiment;

import java.time.LocalDateTime;

public record NewsArticle(
    String link,
    String summary,
    Sentiment sentiment,
    LocalDateTime createdAt
) {}
