package jan.ondra.newsservice.dto;

import jan.ondra.newsservice.domain.enums.Sentiment;

public record ChatGptQueryResult(boolean relevant, Sentiment sentiment, String summary) {}
