package jan.ondra.newsservice.ai.dto;

public record ChatGptQueryResult(boolean relevant, String sentiment, String summary) {}
