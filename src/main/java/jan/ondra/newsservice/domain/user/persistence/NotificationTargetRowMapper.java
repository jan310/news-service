package jan.ondra.newsservice.domain.user.persistence;

import jan.ondra.newsservice.domain.user.model.NotificationTarget;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationTargetRowMapper implements RowMapper<NotificationTarget> {

    @Override
    @NonNull
    public NotificationTarget mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new NotificationTarget(
            rs.getString("id"),
            rs.getString("notification_email")
        );
    }

}
