package jan.ondra.newsservice.domain.news.persistence;

import jan.ondra.newsservice.domain.news.model.NewsArticle;
import jan.ondra.newsservice.enums.Sentiment;
import jan.ondra.newsservice.exception.exceptions.NewsArticleAlreadyExistsException;
import jan.ondra.newsservice.exception.exceptions.StockTickerNotFoundException;
import jan.ondra.newsservice.helper.TestcontainersTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static jan.ondra.newsservice.enums.Sentiment.POSITIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(NewsRepository.class)
class NewsRepositoryTest extends TestcontainersTest {

    @Autowired private NewsRepository newsRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    private final NewsArticleRowMapper newsArticleRowMapper = new NewsArticleRowMapper();

    @Nested
    class AddNewsArticleToStock {

        LocalDateTime localDateTime = LocalDateTime.of(2025,7,20,8,0);

        @Test
        @DisplayName("succeeds")
        void success() {
            insertStock();
            var newsArticle = new NewsArticle("https://asdf.com", "summary", POSITIVE, localDateTime);
            var stockTicker = "MSFT";

            newsRepository.addNewsArticleToStock(newsArticle, stockTicker);

            assertThat(getPersistedNewsArticle(stockTicker)).isEqualTo(newsArticle);
        }

        @Test
        @DisplayName("throws correct exception when news article already exists")
        void error1() {
            insertStock();
            var newsArticle = new NewsArticle("https://asdf.com", "summary", POSITIVE, localDateTime);
            var stockTicker = "MSFT";
            newsRepository.addNewsArticleToStock(newsArticle, stockTicker);

            assertThatThrownBy(() -> newsRepository.addNewsArticleToStock(newsArticle, stockTicker))
                .isInstanceOf(NewsArticleAlreadyExistsException.class)
                .hasCauseInstanceOf(DuplicateKeyException.class);
        }

        @Test
        @DisplayName("throws correct exception when stock sticker does not exist")
        void error2() {
            var newsArticle = new NewsArticle("https://asdf.com", "summary", POSITIVE, localDateTime);
            var stockTicker = "MSFT";

            assertThatThrownBy(() -> newsRepository.addNewsArticleToStock(newsArticle, stockTicker))
                .isInstanceOf(StockTickerNotFoundException.class)
                .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }

    }

    private void insertStock() {
        jdbcTemplate.update(
            """
            INSERT INTO stocks (ticker, company_name, latest_news_link)
            VALUES ('MSFT', 'Microsoft', 'https://asdf.com');
            """
        );
    }

    private NewsArticle getPersistedNewsArticle(String stockTicker) {
        return jdbcTemplate.queryForObject(
            String.format("SELECT * FROM news_articles WHERE stock_ticker = '%s'", stockTicker),
            newsArticleRowMapper
        );
    }

}

class NewsArticleRowMapper implements RowMapper<NewsArticle> {

    @Override
    @NonNull
    public NewsArticle mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new NewsArticle(
            rs.getString("link"),
            rs.getString("summary"),
            Sentiment.valueOf(rs.getString("sentiment")),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

}
