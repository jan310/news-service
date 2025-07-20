package jan.ondra.newsservice.domain.user.model;

import java.time.LocalTime;

public record User(
    String id,
    boolean notificationEnabled,
    String notificationEmail,
    LocalTime notificationTime,
    String timeZone
) {}
