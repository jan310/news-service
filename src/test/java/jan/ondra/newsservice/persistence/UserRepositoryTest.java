package jan.ondra.newsservice.persistence;

import jan.ondra.newsservice.domain.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(UserRepository.class)
class UserRepositoryTest extends DatabaseTest {

    @Autowired private UserRepository userRepository;

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
        return jdbcTemplate.queryForObject(
            String.format("SELECT * FROM users WHERE id = '%s'", id),
            new UserRowMapper()
        );
    }

}