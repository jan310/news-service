package jan.ondra.newsservice.domain.user.model;

import jan.ondra.newsservice.domain.user.api.UserDTO;

import java.time.LocalTime;

public record User(
    String id,
    boolean notificationEnabled,
    String notificationEmail,
    LocalTime notificationTime,
    String timeZone
) {

    public UserDTO toUserDTO() {
        return new UserDTO(notificationEnabled, notificationEmail, notificationTime, timeZone);
    }

}
