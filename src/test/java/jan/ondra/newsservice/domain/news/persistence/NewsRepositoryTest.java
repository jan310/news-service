package jan.ondra.newsservice.domain.news.persistence;

import jan.ondra.newsservice.domain.news.model.CompanyNews;
import jan.ondra.newsservice.domain.news.model.NewsArticle;
import jan.ondra.newsservice.domain.stock.model.Stock;
import jan.ondra.newsservice.domain.user.model.User;
import jan.ondra.newsservice.helper.DatabaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static jan.ondra.newsservice.enums.Sentiment.NEGATIVE;
import static jan.ondra.newsservice.enums.Sentiment.NEUTRAL;
import static jan.ondra.newsservice.enums.Sentiment.POSITIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(NewsRepository.class)
class NewsRepositoryTest extends DatabaseIntegrationTest {

    @Autowired
    private NewsRepository newsRepository;

    @Nested
    class AddNewsArticle {

        @Test
        @DisplayName("adds the given news article")
        void test1() {
            insertStock(new Stock("MSFT", "Microsoft", "https://msft-news.com"));
            var newsArticle = new NewsArticle("https://asdf.com", "MSFT", "summary", POSITIVE, LocalDateTime.now());

            newsRepository.addNewsArticle(newsArticle);

            assertThat(getAllNewsArticles()).containsExactly(newsArticle);
        }

        @Test
        @DisplayName("throws DataIntegrityViolationException when news article already exists")
        void test2() {
            insertStock(new Stock("MSFT", "Microsoft", "https://msft-news.com"));
            var newsArticle = new NewsArticle("https://asdf.com", "MSFT", "summary", POSITIVE, LocalDateTime.now());
            newsRepository.addNewsArticle(newsArticle);

            assertThatThrownBy(() -> newsRepository.addNewsArticle(newsArticle))
                .isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("throws DataIntegrityViolationException when stock does not exist")
        void test3() {
            var newsArticle = new NewsArticle("https://asdf.com", "MSFT", "summary", POSITIVE, LocalDateTime.now());

            assertThatThrownBy(() -> newsRepository.addNewsArticle(newsArticle))
                .isInstanceOf(DataIntegrityViolationException.class);
        }

    }

    @Nested
    class GetCompanyNewsForUser {

        @Test
        @DisplayName("returns the correct CompanyNews")
        void test() {
            insertUser(new User("user1", true, "user1@email.com", LocalTime.now(), "Europe/Berlin"));
            insertUser(new User("user2", true, "user2@email.com", LocalTime.now(), "Europe/Berlin"));

            insertStock(new Stock("MSFT", "Microsoft", "https://msft-news.com"));
            insertStock(new Stock("AAPL", "Apple", "https://aapl-news.com"));
            insertStock(new Stock("GOOG", "Google", "https://goog-news.com"));

            insertUserStockJunction("user1", "MSFT");
            insertUserStockJunction("user1", "AAPL");
            insertUserStockJunction("user2", "AAPL");
            insertUserStockJunction("user2", "GOOG");

            insertNewsArticle(new NewsArticle("https://msft-news-1.com", "MSFT", "summary1", POSITIVE, LocalDateTime.now()));
            insertNewsArticle(new NewsArticle("https://msft-news-2.com", "MSFT", "summary2", NEUTRAL, LocalDateTime.now()));
            insertNewsArticle(new NewsArticle("https://aapl-news-1.com", "AAPL", "summary3", NEGATIVE, LocalDateTime.now()));
            insertNewsArticle(new NewsArticle("https://goog-news-1.com", "GOOG", "summary4", POSITIVE, LocalDateTime.now()));

            var companyNews = newsRepository.getCompanyNewsForUser("user1");

            assertThat(companyNews).containsExactlyInAnyOrder(
                new CompanyNews("Microsoft", "MSFT", "https://msft-news-1.com", "summary1", POSITIVE),
                new CompanyNews("Microsoft", "MSFT", "https://msft-news-2.com", "summary2", NEUTRAL),
                new CompanyNews("Apple", "AAPL", "https://aapl-news-1.com", "summary3", NEGATIVE)
            );
        }

    }

    @Nested
    class DeleteNewsArticlesCreatedAtOrBefore {

        @Test
        @DisplayName("deletes the correct NewsArticles")
        void test() {
            insertStock(new Stock("MSFT", "Microsoft", "https://msft-news.com"));
            var newsArticle = new NewsArticle("https://news-1.com","MSFT","summary1",POSITIVE,LocalDateTime.of(2025, 8, 6, 10, 0));
            insertNewsArticle(newsArticle);
            insertNewsArticle(new NewsArticle("https://news-2.com","MSFT","summary2",POSITIVE,LocalDateTime.of(2025, 8, 5, 10, 0)));
            insertNewsArticle(new NewsArticle("https://news-3.com","MSFT","summary3",POSITIVE,LocalDateTime.of(2025, 8, 5, 9, 0)));

            newsRepository.deleteNewsArticlesCreatedAtOrBefore(LocalDateTime.of(2025, 8, 5, 10, 0));

            assertThat(getAllNewsArticles()).containsExactly(newsArticle);
        }

    }

}
