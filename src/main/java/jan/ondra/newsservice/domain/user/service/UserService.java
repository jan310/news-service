package jan.ondra.newsservice.domain.user.service;

import jan.ondra.newsservice.domain.news.service.NewsService;
import jan.ondra.newsservice.domain.user.model.User;
import jan.ondra.newsservice.domain.user.persistence.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final NewsService newsService;

    public UserService(UserRepository userRepository, NewsService newsService) {
        this.userRepository = userRepository;
        this.newsService = newsService;
    }

    public void addUser(User user) {
        userRepository.addUser(user);
    }

    public void updateUser(User user) {
        userRepository.updateUser(user);
    }

    public void deleteUser(String userId) {
        userRepository.deleteUser(userId);
    }

    // runs every day at 5.30 am
    @Scheduled(cron = "0 30 5 * * *")
    public void notifyUsers() {
        for (User user : userRepository.getUsers()) {
            newsService.generateAndSendNewsletter(user.id(), user.email());
        }
    }

}
