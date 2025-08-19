package jan.ondra.newsservice.domain.user.api;

import jan.ondra.newsservice.domain.user.model.User;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;

public record UserDTO(
    boolean notificationEnabled,
    String notificationEmail,
    LocalTime notificationTime,
    String timeZone
) {

    private static final Set<String> validTimeZones = ZoneId.getAvailableZoneIds();

    public User toUser(String id) {
        if (!validTimeZones.contains(timeZone)) {
            throw new IllegalArgumentException("Invalid time zone: " + timeZone);
        }

        return new User(id, notificationEnabled, notificationEmail, notificationTime, timeZone);
    }

}
