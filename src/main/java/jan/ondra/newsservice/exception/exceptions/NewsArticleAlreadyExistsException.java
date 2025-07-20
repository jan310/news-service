package jan.ondra.newsservice.exception.exceptions;

public class NewsArticleAlreadyExistsException extends RuntimeException {
    public NewsArticleAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
