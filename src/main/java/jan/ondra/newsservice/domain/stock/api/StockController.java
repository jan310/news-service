package jan.ondra.newsservice.domain.stock.api;

import jan.ondra.newsservice.domain.stock.model.Stock;
import jan.ondra.newsservice.domain.stock.service.StockService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<StockDTO> getStocksForUser(@AuthenticationPrincipal Jwt jwt) {
        return stockService
            .getStocksForUser(jwt.getSubject())
            .stream()
            .map(Stock::toStockDTO)
            .toList();
    }

    @PostMapping("/{stockTicker}")
    @ResponseStatus(CREATED)
    public void assignStockToUser(@AuthenticationPrincipal Jwt jwt, @PathVariable String stockTicker) {
        stockService.assignStockToUser(stockTicker, jwt.getSubject());
    }

    @DeleteMapping("/{stockTicker}")
    @ResponseStatus(NO_CONTENT)
    public void removeStockFromUser(@AuthenticationPrincipal Jwt jwt, @PathVariable String stockTicker) {
        stockService.removeStockFromUser(stockTicker, jwt.getSubject());
    }

}
