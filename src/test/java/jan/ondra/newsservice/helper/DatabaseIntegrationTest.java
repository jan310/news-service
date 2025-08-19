package jan.ondra.newsservice.helper;

import jan.ondra.newsservice.domain.news.model.NewsArticle;
import jan.ondra.newsservice.domain.stock.model.Stock;
import jan.ondra.newsservice.domain.stock.persistence.StockRowMapper;
import jan.ondra.newsservice.domain.user.model.User;
import jan.ondra.newsservice.domain.user.persistence.UserRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;
import java.util.Map;

@JdbcTest
public class DatabaseIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.4");

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM users; DELETE FROM stocks;", Map.of());
    }

    public void insertUser(User user) {
        var sqlStatement = """
            INSERT INTO users (id, notification_enabled, notification_email, notification_time, time_zone)
            VALUES (:id, :notification_enabled, :notification_email, :notification_time, :time_zone);
            """;

        var parameters = Map.of(
            "id", user.id(),
            "notification_enabled", user.notificationEnabled(),
            "notification_email", user.notificationEmail(),
            "notification_time", user.notificationTime(),
            "time_zone", user.timeZone()
        );

        jdbcTemplate.update(sqlStatement, parameters);
    }

    public void insertStock(Stock stock) {
        var sqlStatement = """
            INSERT INTO stocks (ticker, company_name, latest_news_link)
            VALUES (:ticker, :company_name, :latest_news_link);
            """;

        var parameters = Map.of(
            "ticker", stock.ticker(),
            "company_name", stock.companyName(),
            "latest_news_link", stock.latestNewsLink()
        );

        jdbcTemplate.update(sqlStatement, parameters);
    }

    public void insertUserStockJunction(String userId, String stockTicker) {
        var sqlStatement = """
            INSERT INTO user_stock_junction (user_id, stock_ticker)
            VALUES (:user_id, :stock_ticker);
            """;

        var parameters = Map.of(
            "user_id", userId,
            "stock_ticker", stockTicker
        );

        jdbcTemplate.update(sqlStatement, parameters);
    }

    public void insertNewsArticle(NewsArticle newsArticle) {
        var sqlStatement = """
            INSERT INTO news_articles (link, stock_ticker, summary, sentiment, created_at)
            VALUES (:link, :stock_ticker, :summary, :sentiment, :created_at)
            """;

        var parameters = Map.of(
            "link", newsArticle.link(),
            "stock_ticker", newsArticle.stockTicker(),
            "summary", newsArticle.summary(),
            "sentiment", newsArticle.sentiment().name(),
            "created_at", newsArticle.createdAt()
        );

        jdbcTemplate.update(sqlStatement, parameters);
    }

    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", new UserRowMapper());
    }

    public List<Stock> getAllStocks() {
        return jdbcTemplate.query("SELECT * FROM stocks", new StockRowMapper());
    }

    public List<NewsArticle> getAllNewsArticles() {
        return jdbcTemplate.query("SELECT * FROM news_articles", new NewsArticleRowMapper());
    }

    public boolean stockUserAssignmentExists(String stockTicker, String userId) {
        return jdbcTemplate.queryForObject(
            "SELECT EXISTS(SELECT 1 FROM user_stock_junction WHERE stock_ticker = :stock_ticker AND user_id = :user_id)",
            Map.of(
                "stock_ticker", stockTicker,
                "user_id", userId
            ),
            Boolean.class
        );
    }

}
