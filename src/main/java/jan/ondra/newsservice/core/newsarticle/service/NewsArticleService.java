package jan.ondra.newsservice.core.newsarticle.service;

import jan.ondra.newsservice.core.newsarticle.model.CompanyNews;
import jan.ondra.newsservice.core.newsarticle.model.NewsArticle;
import jan.ondra.newsservice.core.newsarticle.persistence.NewsArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsArticleService {

    private final NewsArticleRepository newsArticleRepository;

    public NewsArticleService(NewsArticleRepository newsArticleRepository) {
        this.newsArticleRepository = newsArticleRepository;
    }

    public void addNewsArticleToStock(NewsArticle newsArticle, String stockTicker) {
        newsArticleRepository.addNewsArticleToStock(newsArticle, stockTicker);
    }

    public void deleteNewsArticles() {
        newsArticleRepository.deleteNewsArticles();
    }

    public List<CompanyNews> getCompanyNewsRelevantForUser(String userId) {
        return newsArticleRepository.getCompanyNewsRelevantForUser(userId);
    }

}
