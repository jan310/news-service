package jan.ondra.newsservice.domain.news.persistence;

import jan.ondra.newsservice.domain.news.model.CompanyNews;
import jan.ondra.newsservice.domain.news.model.NewsArticle;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
            INSERT INTO news_articles (
                link,
                stock_ticker,
                summary,
                sentiment
            ) VALUES (
                :link,
                :stock_ticker,
                :summary,
                :sentiment
            );
            """;

        var parameters = Map.of(
            "link", newsArticle.link(),
            "stock_ticker", stockTicker,
            "summary", newsArticle.summary(),
            "sentiment", newsArticle.sentiment().name()
        );

        jdbcTemplate.update(sqlStatement, parameters);
    }

    public void deleteNewsArticles() {
        jdbcTemplate.update("DELETE FROM news_articles", Map.of());
    }

    public List<CompanyNews> getCompanyNewsForUser(String userId) {
        var sqlStatement = """
            SELECT
                stocks.company_name,
                stocks.ticker,
                news_articles.link,
                news_articles.summary,
                news_articles.sentiment
            FROM user_stock_junction
                INNER JOIN stocks ON stocks.ticker = user_stock_junction.stock_ticker
                INNER JOIN news_articles ON news_articles.stock_ticker = stocks.ticker
            WHERE user_stock_junction.user_id = :user_id;
            """;

        var parameters = Map.of("user_id", userId);

        return jdbcTemplate.query(sqlStatement, parameters, companyNewsRowMapper);
    }

}
