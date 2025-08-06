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

import static org.assertj.core.api.Assertions.assertThat;

@Import(UserRepository.class)
class UserRepositoryTest extends DatabaseIntegrationTest {

    @Autowired private UserRepository userRepository;

    @Nested
    class GetNotificationTargetsByNotificationTime {

        @Test
        @DisplayName("returns correct NotificationTargets")
        void success() {
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