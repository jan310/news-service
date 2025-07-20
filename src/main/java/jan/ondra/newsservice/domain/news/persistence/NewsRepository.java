package jan.ondra.newsservice.domain.news.persistence;

import jan.ondra.newsservice.domain.news.model.CompanyNews;
import jan.ondra.newsservice.domain.news.model.NewsArticle;
import jan.ondra.newsservice.exception.exceptions.NewsArticleAlreadyExistsException;
import jan.ondra.newsservice.exception.exceptions.StockTickerNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class NewsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final CompanyNewsRowMapper companyNewsRowMapper = new CompanyNewsRowMapper();

    public NewsRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addNewsArticleToStock(NewsArticle newsArticle, String stockTicker) {
        var sqlStatement = """
            INSERT INTO news_articles (link, stock_ticker, summary, sentiment, created_at)
            VALUES (:link, :stock_ticker, :summary, :sentiment, :created_at);
            """;

        var parameters = Map.of(
            "link", newsArticle.link(),
            "stock_ticker", stockTicker,
            "summary", newsArticle.summary(),
            "sentiment", newsArticle.sentiment().name(),
            "created_at", newsArticle.createdAt()
        );

        try {
            jdbcTemplate.update(sqlStatement, parameters);
        } catch (DuplicateKeyException e) {
            throw new NewsArticleAlreadyExistsException(e);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("violates foreign key constraint \"news_articles_stock_ticker_fkey\"")) {
                throw new StockTickerNotFoundException(e);
            } else {
                throw e;
            }
        }
    }

    public List<CompanyNews> getCompanyNewsForUser(String userId) {
        var sqlStatement = """
            SELECT
                stocks.company_name,
                stocks.ticker,
                news_articles.link,
                news_articles.summary,
                news_articles.sentiment
            FROM
                user_stock_junction
                INNER JOIN stocks ON stocks.ticker = user_stock_junction.stock_ticker
                INNER JOIN news_articles ON news_articles.stock_ticker = stocks.ticker
            WHERE user_stock_junction.user_id = :user_id;
            """;

        var parameters = Map.of("user_id", userId);

        return jdbcTemplate.query(sqlStatement, parameters, companyNewsRowMapper);
    }

    public void deleteNewsArticlesCreatedAtOrBefore(LocalDateTime creationTime) {
        var sqlStatement = "DELETE FROM news_articles WHERE created_at >= :creation_time";

        var parameters = Map.of("creation_time", creationTime);

        jdbcTemplate.update(sqlStatement, parameters);
    }

}
