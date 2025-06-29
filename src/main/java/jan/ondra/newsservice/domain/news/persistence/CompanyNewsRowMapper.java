package jan.ondra.newsservice.domain.news.persistence;

import jan.ondra.newsservice.enums.Sentiment;
import jan.ondra.newsservice.domain.news.model.CompanyNews;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyNewsRowMapper implements RowMapper<CompanyNews> {

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
