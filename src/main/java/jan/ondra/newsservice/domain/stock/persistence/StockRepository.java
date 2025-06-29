package jan.ondra.newsservice.domain.stock.persistence;

import jan.ondra.newsservice.domain.stock.model.Stock;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class StockRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final StockRowMapper stockRowMapper = new StockRowMapper();

    public StockRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean stockExists(String stockTicker) {
        var sqlStatement = """
            SELECT EXISTS (
                SELECT 1 FROM stocks WHERE ticker = :ticker
            );
            """;

        var parameters = Map.of("ticker", stockTicker);

        return jdbcTemplate.queryForObject(sqlStatement, parameters, Boolean.class);
    }

    public void assignStockToUser(String stockTicker, String userId) {
        var sqlStatement = """
            INSERT INTO user_stock_junction (
                user_id,
                stock_ticker
            ) VALUES (
                :user_id,
                :stock_ticker
            );
            """;

        var parameters = Map.of(
            "user_id", userId,
            "stock_ticker", stockTicker
        );

        jdbcTemplate.update(sqlStatement, parameters);
    }

    public void addStockAndAssignToUser(Stock stock, String userId) {
        var sqlStatement = """
            BEGIN;

            -- Step 1: Insert the stock into the stocks table
            INSERT INTO stocks (
                ticker,
                company_name,
                latest_news_link
            ) VALUES (
                :stock_ticker,
                :company_name,
                :latest_news_link
            );

            -- Step 2: Assign the stock to the user
            INSERT INTO user_stock_junction (
                user_id,
                stock_ticker
            ) VALUES (
                :user_id,
                :stock_ticker
            );

            COMMIT;
            """;

        var parameters = Map.of(
            "stock_ticker", stock.ticker(),
            "company_name", stock.companyName(),
            "latest_news_link", stock.latestNewsLink(),
            "user_id", userId
        );

        jdbcTemplate.update(sqlStatement, parameters);
    }

    public void removeStockFromUser(String stockTicker, String userId) {
        var sqlStatement = """
            BEGIN;
            
            -- Step 1: Delete the junction entry
            DELETE FROM user_stock_junction
            WHERE user_id = :user_id AND stock_ticker = :stock_ticker;
            
            -- Step 2: Delete the stock if no users reference it
            DELETE FROM stocks
            WHERE ticker = :stock_ticker
                AND NOT EXISTS (
                    SELECT 1
                    FROM user_stock_junction
                    WHERE stock_ticker = :stock_ticker
                );
            
            COMMIT;
            """;

        var parameters = Map.of(
            "user_id", userId,
            "stock_ticker", stockTicker
        );

        jdbcTemplate.update(sqlStatement, parameters);
    }

    public void updateLatestNewsLink(String ticker, String newLink) {
        var sqlStatement = """
            UPDATE stocks
            SET
                latest_news_link = :latest_news_link
            WHERE
                ticker = :stock_ticker;
            """;

        var parameters = Map.of(
            "stock_ticker", ticker,
            "latest_news_link", newLink
        );

        jdbcTemplate.update(sqlStatement, parameters);
    }

    public List<Stock> getAllStocks() {
        var sqlStatement = "SELECT ticker, company_name, latest_news_link FROM stocks";

        return jdbcTemplate.query(sqlStatement, stockRowMapper);
    }

    public List<Stock> getStocksForUser(String userId) {
        var sqlStatement = """
            SELECT
                stocks.ticker,
                stocks.company_name,
                stocks.latest_news_link
            FROM stocks
                INNER JOIN user_stock_junction ON user_stock_junction.stock_ticker = stocks.ticker
            WHERE user_stock_junction.user_id = :user_id
            """;

        var parameters = Map.of("user_id", userId);

        return jdbcTemplate.query(sqlStatement, parameters, stockRowMapper);
    }

}
