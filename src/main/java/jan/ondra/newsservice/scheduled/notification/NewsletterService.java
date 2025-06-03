package jan.ondra.newsservice.scheduled.notification;

import jan.ondra.newsservice.core.newsarticle.model.CompanyNews;
import jan.ondra.newsservice.core.newsarticle.persistence.NewsArticleRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class NewsletterService {

    private final NewsArticleRepository newsArticleRepository;

    public NewsletterService(NewsArticleRepository newsArticleRepository) {
        this.newsArticleRepository = newsArticleRepository;
    }

    @Async
    public void generateAndSendNewsletter(String userId, String email) {
        var companyNewsRelevantForUser = newsArticleRepository.getCompanyNewsRelevantForUser(userId);

        var companyNewsGroupedByTicker = new HashMap<String, ArrayList<CompanyNews>>();
        for (CompanyNews companyNews : companyNewsRelevantForUser) {
            companyNewsGroupedByTicker
                .computeIfAbsent(companyNews.stockTicker(), k -> new ArrayList<>())
                .add(companyNews);
        }

        var newsletter = new StringBuilder();

        for (ArrayList<CompanyNews> companyNewsList : companyNewsGroupedByTicker.values()) {
            newsletter
                .append("News for ")
                .append(companyNewsList.getFirst().companyName())
                .append(" (")
                .append(companyNewsList.getFirst().stockTicker())
                .append(")\n");

            for (CompanyNews companyNews : companyNewsList) {
                newsletter
                    .append("Sentiment: ")
                    .append(companyNews.newsSentiment())
                    .append("\n")
                    .append(companyNews.newsSummary())
                    .append("\nSource: ")
                    .append(companyNews.newsLink())
                    .append("\n\n");
            }

            newsletter.append("\n");
        }

        System.out.println(newsletter);
    }

}
