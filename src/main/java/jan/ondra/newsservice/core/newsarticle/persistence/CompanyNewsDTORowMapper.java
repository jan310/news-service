package jan.ondra.newsservice.core.newsarticle.persistence;

import jan.ondra.newsservice.common.enums.Sentiment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

class CompanyNewsDTORowMapper implements RowMapper<CompanyNewsDTO> {

    @Override
    @NonNull
    public CompanyNewsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CompanyNewsDTO(
            rs.getString("company_name"),
            rs.getString("ticker"),
            rs.getString("link"),
            rs.getString("summary"),
            Sentiment.valueOf(rs.getString("sentiment"))
        );
    }

}
