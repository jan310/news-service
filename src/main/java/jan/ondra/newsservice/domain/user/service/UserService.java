package jan.ondra.newsservice.domain.user.service;

import jan.ondra.newsservice.domain.user.model.NotificationTarget;
import jan.ondra.newsservice.domain.user.model.User;
import jan.ondra.newsservice.domain.user.persistence.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(User user) {
        userRepository.addUser(user);
    }

    public User getUser(String id) {
        return userRepository.getUser(id);
    }

    public void updateUser(User user) {
        userRepository.updateUser(user);
    }

    public void deleteUser(String userId) {
        userRepository.deleteUser(userId);
    }

    public List<NotificationTarget> getNotificationTargetsByNotificationTime(LocalDateTime utcDateTime) {
        return userRepository.getNotificationTargetsByNotificationTime(utcDateTime);
    }

}
