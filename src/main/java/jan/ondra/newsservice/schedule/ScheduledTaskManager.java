package jan.ondra.newsservice.schedule;

import jan.ondra.newsservice.domain.news.service.NewsService;
import jan.ondra.newsservice.domain.user.model.NotificationTarget;
import jan.ondra.newsservice.domain.user.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.MINUTES;

@Component
public class ScheduledTaskManager {

    private final NewsService newsService;
    private final UserService userService;

    public ScheduledTaskManager(NewsService newsService, UserService userService) {
        this.newsService = newsService;
        this.userService = userService;
    }

    @Scheduled(cron = "5 0,15,30,45 * * * *")
    public void notifyUsersThenDeleteOldNewsThenGatherNewNews() {
        var currentUtcDateTime = LocalDateTime.now(UTC).truncatedTo(MINUTES);

        var futures = new ArrayList<CompletableFuture<Void>>();
        for (NotificationTarget target : userService.getNotificationTargetsByNotificationTime(currentUtcDateTime)) {
            futures.add(newsService.generateAndSendNewsletter(target.userId(), target.notificationEmail()));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        newsService.deleteNewsArticlesCreatedAtOrBefore(currentUtcDateTime.minusDays(1));

        newsService.gatherNews(currentUtcDateTime);
    }

}
