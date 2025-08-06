package jan.ondra.newsservice.helper;

import jan.ondra.newsservice.domain.news.model.NewsArticle;
import jan.ondra.newsservice.enums.Sentiment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NewsArticleRowMapper implements RowMapper<NewsArticle> {

    @Override
    @NonNull
    public NewsArticle mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new NewsArticle(
            rs.getString("link"),
            rs.getString("stock_ticker"),
            rs.getString("summary"),
            Sentiment.valueOf(rs.getString("sentiment")),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

}
