package jan.ondra.newsservice.domain.user.persistence;

import jan.ondra.newsservice.domain.user.model.NotificationTarget;
import jan.ondra.newsservice.domain.user.model.User;
import jan.ondra.newsservice.helper.DatabaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(UserRepository.class)
class UserRepositoryTest extends DatabaseIntegrationTest {

    @Autowired private UserRepository userRepository;

    @Nested
    class AddUser {

        @Test
        @DisplayName("adds user")
        void test1() {
            var user = new User("user1", true, "test@email.com", LocalTime.now().truncatedTo(MINUTES), "Europe/Berlin");
            userRepository.addUser(user);

            assertThat(getAllUsers()).containsExactly(user);
        }

        @Test
        @DisplayName("throws EmailAlreadyInUseException when email is already taken")
        void test2() {
            insertUser(new User("user1", true, "test@email.com", LocalTime.now(), "Europe/Berlin"));

            assertThatThrownBy(
                () -> userRepository.addUser(
                    new User("user2", true, "test@email.com", LocalTime.now(), "Europe/Berlin")
                )
            ).isInstanceOf(EmailAlreadyInUseException.class);
        }

    }

    @Nested
    class GetUser {

        @Test
        @DisplayName("returns the specified user")
        void test() {
            var user = new User("user1", true, "user1@email.com", LocalTime.now().truncatedTo(MINUTES), "Europe/Berlin");
            insertUser(user);
            insertUser(new User("user2", true, "user2@email.com", LocalTime.now(), "Europe/Berlin"));

            assertThat(userRepository.getUser("user1")).isEqualTo(user);
        }

    }

    @Nested
    class UpdateUser {

        @Test
        @DisplayName("updates the specified user")
        void test1() {
            insertUser(new User("user1", true, "user1@email.com", LocalTime.now().truncatedTo(MINUTES), "Europe/Berlin"));
            insertUser(new User("user2", true, "user2@email.com", LocalTime.now().truncatedTo(MINUTES), "Europe/Berlin"));

            userRepository.updateUser(
                new User("user1", false, "user-1@email.com", LocalTime.of(10, 15), "Europe/London")
            );

            assertThat(getAllUsers()).containsExactlyInAnyOrder(
                new User("user1", false, "user-1@email.com", LocalTime.of(10, 15), "Europe/London"),
                new User("user2", true, "user2@email.com", LocalTime.now().truncatedTo(MINUTES), "Europe/Berlin")
            );
        }

        @Test
        @DisplayName("throws EmailAlreadyInUseException when email is already taken")
        void test2() {
            insertUser(new User("user1", true, "user1@email.com", LocalTime.now(), "Europe/Berlin"));
            insertUser(new User("user2", true, "user2@email.com", LocalTime.now(), "Europe/Berlin"));

            assertThatThrownBy(() -> userRepository.updateUser(
                    new User("user1", true, "user2@email.com", LocalTime.now(), "Europe/Berlin")
                )
            ).isInstanceOf(EmailAlreadyInUseException.class);
        }

    }

    @Nested
    class DeleteUser {

        @Test
        @DisplayName("deletes the correct user")
        void test() {
            var user = new User("user1", true, "user1@email.com", LocalTime.now().truncatedTo(MINUTES), "Europe/Berlin");
            insertUser(user);
            insertUser(new User("user2", true, "user2@email.com", LocalTime.now().truncatedTo(MINUTES), "Europe/Berlin"));

            userRepository.deleteUser("user2");

            assertThat(getAllUsers()).containsExactly(user);
        }

    }

    @Nested
    class GetNotificationTargetsByNotificationTime {

        @Test
        @DisplayName("returns correct NotificationTargets")
        void test() {
            //These users should be notification targets
            var user1 = new User("1", true, "user1@test.com", LocalTime.of(4, 0), "Atlantic/Reykjavik");
            var user2 = new User("2", true, "user2@test.com", LocalTime.of(6, 0), "Europe/Berlin");
            var user3 = new User("3", true, "user3@test.com", LocalTime.of(21, 0), "America/Los_Angeles");
            var user4 = new User("4", true, "user4@test.com", LocalTime.of(13, 30), "Australia/Adelaide");
            var user5 = new User("5", true, "user5@test.com", LocalTime.of(9, 45), "Asia/Kathmandu");

            //These users shouldn't be notification targets
            var user6 = new User("6", false, "user6@test.com", LocalTime.of(6, 0), "Europe/Berlin");
            var user7 = new User("7", true, "user7@test.com", LocalTime.of(7, 0), "Europe/Berlin");

            insertUser(user1);
            insertUser(user2);
            insertUser(user3);
            insertUser(user4);
            insertUser(user5);
            insertUser(user6);
            insertUser(user7);

            var targets = userRepository.getNotificationTargetsByNotificationTime(LocalDateTime.of(2025, 7, 20, 4, 0));

            assertThat(targets).containsExactlyInAnyOrder(
                new NotificationTarget("1", "user1@test.com"),
                new NotificationTarget("2", "user2@test.com"),
                new NotificationTarget("3", "user3@test.com"),
                new NotificationTarget("4", "user4@test.com"),
                new NotificationTarget("5", "user5@test.com")
            );
        }

    }

}
