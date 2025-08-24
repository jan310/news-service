package jan.ondra.newsservice.domain.user.api;

import jan.ondra.newsservice.domain.user.api.validation.ValidEmail;
import jan.ondra.newsservice.domain.user.api.validation.ValidQuarterHour;
import jan.ondra.newsservice.domain.user.api.validation.ValidTimeZone;
import jan.ondra.newsservice.domain.user.model.User;

import java.time.LocalTime;

public record UserDTO(
    boolean notificationEnabled,

    @ValidEmail
    String notificationEmail,

    @ValidQuarterHour
    LocalTime notificationTime,

    @ValidTimeZone
    String timeZone
) {

    public User toUser(String id) {
        return new User(id, notificationEnabled, notificationEmail, notificationTime, timeZone);
    }

}
