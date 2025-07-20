package jan.ondra.newsservice.domain.user.model;

public record NotificationTarget(
    String userId,
    String notificationEmail
) {}
