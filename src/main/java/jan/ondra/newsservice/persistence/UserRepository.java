package jan.ondra.newsservice.persistence;

import jan.ondra.newsservice.domain.models.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createUser(User user) {
        var sqlStatement = """
            INSERT INTO users (
                id,
                email
            ) VALUES (
                :id,
                :email
            );
            """;

        var parameters = Map.of(
            "id", user.id(),
            "email", user.email()
        );

        jdbcTemplate.update(sqlStatement, parameters);
    }

    public List<User> getUsers() {
        var sqlStatement = "SELECT * FROM users;";

        return jdbcTemplate.query(sqlStatement, new UserRowMapper());
    }

    public void updateUser(User user) {
        var sqlStatement = """
            UPDATE users
            SET
                email = :email
            WHERE
                id = :id;
            """;

        var parameters = Map.of(
            "id", user.id(),
            "email", user.email()
        );

        jdbcTemplate.update(sqlStatement, parameters);
    }

    public void deleteUser(String userId) {
        var sqlStatement = "DELETE FROM users WHERE id = :id;";

        var parameters = Map.of("id", userId);

        jdbcTemplate.update(sqlStatement, parameters);
    }

}

class UserRowMapper implements RowMapper<User> {

    @Override
    @NonNull
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(rs.getString("id"), rs.getString("email"));
    }

}
