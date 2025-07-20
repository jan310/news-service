package jan.ondra.newsservice.domain.user.persistence;

import jan.ondra.newsservice.domain.user.model.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {

    @Override
    @NonNull
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(
            rs.getString("id"),
            rs.getBoolean("notification_enabled"),
            rs.getString("notification_email"),
            rs.getTime("notification_time").toLocalTime(),
            rs.getString("time_zone")
        );
    }

}
