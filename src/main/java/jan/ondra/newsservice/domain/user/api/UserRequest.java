package jan.ondra.newsservice.domain.user.api;

import jan.ondra.newsservice.domain.user.model.User;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;

public record UserRequest(
    boolean notificationEnabled,
    String notificationEmail,
    LocalTime notificationTime,
    String timeZone
) {

    private static final Set<String> VALID_TIME_ZONES = ZoneId.getAvailableZoneIds();

    public User toUser(String id) {
        if (!VALID_TIME_ZONES.contains(timeZone)) {
            throw new IllegalArgumentException("Invalid time zone: " + timeZone);
        }

        return new User(id, notificationEnabled, notificationEmail, notificationTime, timeZone);
    }

}
