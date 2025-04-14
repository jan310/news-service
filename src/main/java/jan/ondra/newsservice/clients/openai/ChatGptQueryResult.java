package jan.ondra.newsservice.clients.openai;

public record ChatGptQueryResult(boolean relevant, String sentiment, String summary) {}
