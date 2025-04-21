package jan.ondra.newsservice.persistence;

import jan.ondra.newsservice.domain.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@ActiveProfiles("test")
@JdbcTest
@Import(UserRepository.class)
@AutoConfigureTestDatabase(replace = NONE)
class UserRepositoryTest {

    @Autowired private UserRepository userRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    @Nested
    class CreateUser {

        @Test
        @DisplayName("should create user successfully")
        void success() {
            var user = new User("1234", "john@doe.com");

            userRepository.createUser(user);

            assertThat(getSavedUser("1234")).isEqualTo(user);
        }

    }

    private User getSavedUser(String id) {
        return jdbcTemplate.query(
            String.format("SELECT * FROM users WHERE id = '%s'", id),
            new UserRowMapper()
        ).getFirst();
    }

}