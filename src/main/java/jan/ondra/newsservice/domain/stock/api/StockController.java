package jan.ondra.newsservice.domain.stock.api;

import jan.ondra.newsservice.domain.stock.model.Stock;
import jan.ondra.newsservice.domain.stock.service.StockService;
import jan.ondra.newsservice.util.UserIdExtractor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;
    private final UserIdExtractor userIdExtractor;

    public StockController(StockService stockService, UserIdExtractor userIdExtractor) {
        this.stockService = stockService;
        this.userIdExtractor = userIdExtractor;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<StockResponse> getStocksForUser(@RequestHeader(AUTHORIZATION) String bearerToken) {
        return stockService
            .getStocksForUser(userIdExtractor.extractFromBearerToken(bearerToken))
            .stream()
            .map(Stock::toResponse)
            .toList();
    }

    @PostMapping("/{stockTicker}")
    @ResponseStatus(CREATED)
    public void assignStockToUser(
        @RequestHeader(AUTHORIZATION) String bearerToken,
        @PathVariable String stockTicker
    ) {
        stockService.assignStockToUser(stockTicker, userIdExtractor.extractFromBearerToken(bearerToken));
    }

    @DeleteMapping("/{stockTicker}")
    @ResponseStatus(NO_CONTENT)
    public void removeStockFromUser(
        @RequestHeader(AUTHORIZATION) String bearerToken,
        @PathVariable String stockTicker
    ) {
        stockService.removeStockFromUser(stockTicker, userIdExtractor.extractFromBearerToken(bearerToken));
    }

}
