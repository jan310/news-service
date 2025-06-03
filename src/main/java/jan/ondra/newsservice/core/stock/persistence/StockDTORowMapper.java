package jan.ondra.newsservice.core.stock.persistence;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

class StockDTORowMapper implements RowMapper<StockDTO> {

    @Override
    @NonNull
    public StockDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new StockDTO(
            rs.getString("ticker"),
            rs.getString("company_name"),
            rs.getString("latest_news_link")
        );
    }

}
