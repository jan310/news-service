package jan.ondra.newsservice.scheduled.notification;

import jan.ondra.newsservice.core.user.model.User;
import jan.ondra.newsservice.core.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserNotificationService {

    private final UserService userService;
    private final NewsletterService newsletterService;

    public UserNotificationService(UserService userService, NewsletterService newsletterService) {
        this.userService = userService;
        this.newsletterService = newsletterService;
    }

    public void notifyUsers() {
        var users = userService.getUsers();

        for (User user : users) {
            newsletterService.generateAndSendNewsletter(user.id(), user.email());
        }
    }

}
