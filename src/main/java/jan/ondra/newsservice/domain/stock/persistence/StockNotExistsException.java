package jan.ondra.newsservice.domain.stock.persistence;

import org.springframework.dao.DataIntegrityViolationException;

public class StockNotExistsException extends RuntimeException {
    public StockNotExistsException(DataIntegrityViolationException cause) {
        super(cause);
    }
}
