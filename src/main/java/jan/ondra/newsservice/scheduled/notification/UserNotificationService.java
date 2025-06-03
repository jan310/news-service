package jan.ondra.newsservice.scheduled.notification;

import jan.ondra.newsservice.core.user.model.User;
import jan.ondra.newsservice.core.user.persistence.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserNotificationService {

    private final UserRepository userRepository;
    private final NewsletterService newsletterService;

    public UserNotificationService(UserRepository userRepository, NewsletterService newsletterService) {
        this.userRepository = userRepository;
        this.newsletterService = newsletterService;
    }

    public void notifyUsers() {
        var users = userRepository.getUsers();

        for (User user : users) {
            newsletterService.generateAndSendNewsletter(user.id(), user.email());
        }
    }

}
