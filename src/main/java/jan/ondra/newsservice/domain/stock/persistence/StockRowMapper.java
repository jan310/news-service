package jan.ondra.newsservice.domain.stock.persistence;

import jan.ondra.newsservice.domain.stock.model.Stock;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StockRowMapper implements RowMapper<Stock> {

    @Override
    @NonNull
    public Stock mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Stock(
            rs.getString("ticker"),
            rs.getString("company_name"),
            rs.getString("latest_news_link")
        );
    }

}
