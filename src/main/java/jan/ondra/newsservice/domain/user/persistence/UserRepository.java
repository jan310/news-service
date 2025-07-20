package jan.ondra.newsservice.domain.user.persistence;

import jan.ondra.newsservice.domain.user.model.NotificationTarget;
import jan.ondra.newsservice.domain.user.model.User;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper = new UserRowMapper();
    private final NotificationTargetRowMapper notificationTargetRowMapper = new NotificationTargetRowMapper();

    public UserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addUser(User user) {
        var sqlStatement = """
            INSERT INTO users (id, notification_enabled, notification_email, notification_time, time_zone)
            VALUES (:id, :notification_enabled, :notification_email, :notification_time, :time_zone);
            """;

        var parameters = Map.of(
            "id", user.id(),
            "notification_enabled", user.notificationEnabled(),
            "notification_email", user.notificationEmail(),
            "notification_time", user.notificationTime(),
            "time_zone", user.timeZone()
        );

        jdbcTemplate.update(sqlStatement, parameters);
    }

    public User getUser(String id) {
        var sqlStatement = "SELECT * FROM users WHERE id = :id;";

        var parameters = Map.of("id", id);

        return jdbcTemplate.queryForObject(sqlStatement, parameters, userRowMapper);
    }

    public void updateUser(User user) {
        var sqlStatement = """
            UPDATE users
            SET
                notification_enabled = :notification_enabled,
                notification_email = :notification_email,
                notification_time = :notification_time,
                time_zone = :time_zone
            WHERE id = :id;
            """;

        var parameters = Map.of(
            "id", user.id(),
            "notification_enabled", user.notificationEnabled(),
            "notification_email", user.notificationEmail(),
            "notification_time", user.notificationTime(),
            "time_zone", user.timeZone()
        );

        jdbcTemplate.update(sqlStatement, parameters);
    }

    public void deleteUser(String userId) {
        var sqlStatement = "DELETE FROM users WHERE id = :id;";

        var parameters = Map.of("id", userId);

        jdbcTemplate.update(sqlStatement, parameters);
    }

    public List<NotificationTarget> getNotificationTargetsByNotificationTime(LocalDateTime utcDateTime) {
        var sqlStatement = """
            SELECT id, notification_email
            FROM users
            WHERE
                notification_enabled = true AND
                notification_time = ((:utcDateTime AT TIME ZONE 'UTC') AT TIME ZONE time_zone)::time
            """;

        var parameters = Map.of("utcDateTime", utcDateTime);

        return jdbcTemplate.query(sqlStatement, parameters, notificationTargetRowMapper);
    }

}
