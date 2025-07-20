package jan.ondra.newsservice.exception.exceptions;

public class StockTickerNotFoundException extends RuntimeException {
    public StockTickerNotFoundException(Throwable cause) {
        super(cause);
    }
}
