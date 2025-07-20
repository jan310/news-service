package jan.ondra.newsservice.schedule;

import jan.ondra.newsservice.domain.news.service.NewsService;
import jan.ondra.newsservice.domain.user.model.NotificationTarget;
import jan.ondra.newsservice.domain.user.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;

@Component
public class ScheduledTaskManager {

    private final NewsService newsService;
    private final UserService userService;

    public ScheduledTaskManager(NewsService newsService, UserService userService) {
        this.newsService = newsService;
        this.userService = userService;
    }

    /**
     * Runs every quarter-hour at minutes 0, 15, 30 and 45.
     * 1. notifies users that want to be notified at this time
     * 2. deletes news articles that are at least 24 hours old
     * 3. gathers new news articles
     */
    @Scheduled(cron = "5 0,15,30,45 * * * *")
    public void notifyUsers() {
        var currentUtcDateTime = LocalDateTime.now(UTC).withSecond(0).withNano(0);

        for (NotificationTarget target : userService.getNotificationTargetsByNotificationTime(currentUtcDateTime)) {
            newsService.generateAndSendNewsletter(target.userId(), target.notificationEmail());
        }

        newsService.deleteNewsArticlesCreatedAtOrBefore(currentUtcDateTime.minusDays(1));

        newsService.gatherNews(currentUtcDateTime);
    }

}
