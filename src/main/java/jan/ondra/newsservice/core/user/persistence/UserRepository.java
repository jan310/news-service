package jan.ondra.newsservice.core.user.persistence;

import jan.ondra.newsservice.core.user.model.User;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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

        var userDTOs = jdbcTemplate.query(sqlStatement, new UserDTORowMapper());

        return userDTOs
            .stream()
            .map(UserDTOMapper::getDomainModel)
            .toList();
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
