package jan.ondra.newsservice.persistence;

import jan.ondra.newsservice.domain.enums.Sentiment;
import jan.ondra.newsservice.domain.models.NewsArticle;
import jan.ondra.newsservice.dto.CompanyNews;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class NewsArticleRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public NewsArticleRepository(NamedParameterJdbcTemplate jdbcTemplate) {
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
                :stock_ticker
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

    public List<CompanyNews> getCompanyNewsRelevantForUser(String userId) {
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

        return jdbcTemplate.query(sqlStatement, parameters, new CompanyNewsRowMapper());
    }

}

class CompanyNewsRowMapper implements RowMapper<CompanyNews> {

    @Override
    @NonNull
    public CompanyNews mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CompanyNews(
            rs.getString("company_name"),
            rs.getString("ticker"),
            rs.getString("link"),
            rs.getString("summary"),
            Sentiment.valueOf(rs.getString("sentiment"))
        );
    }
}
