package jan.ondra.newsservice.business;

import jan.ondra.newsservice.domain.models.User;
import jan.ondra.newsservice.persistence.UserRepository;
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
