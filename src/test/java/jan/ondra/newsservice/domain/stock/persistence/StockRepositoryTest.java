package jan.ondra.newsservice.domain.stock.persistence;

import jan.ondra.newsservice.domain.stock.model.Stock;
import jan.ondra.newsservice.domain.user.model.User;
import jan.ondra.newsservice.helper.DatabaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(StockRepository.class)
class StockRepositoryTest extends DatabaseIntegrationTest {

    @Autowired
    private StockRepository stockRepository;

    @Nested
    class AddStock {

        @Test
        @DisplayName("adds the specified stock")
        void test1() {
            var stock = new Stock("MSFT", "Microsoft", "https://msft-news.com");
            stockRepository.addStock(stock);

            assertThat(getAllStocks()).containsExactly(stock);
        }

        @Test
        @DisplayName("does not fail and inserts only once when stock is added twice (race condition)")
        void test2() {
            var stock = new Stock("MSFT", "Microsoft", "https://msft-news.com");
            stockRepository.addStock(stock);
            stockRepository.addStock(stock);

            assertThat(getAllStocks()).containsExactly(stock);
        }

    }

    @Nested
    class AssignStockToUser {

        @Test
        @DisplayName("assigns given stock to given user")
        void test1() {
            insertStock(new Stock("MSFT", "Microsoft", "https://msft-news.com"));
            insertUser(new User("user1", true, "user1@email.com", LocalTime.now(), "Europe/Berlin"));

            stockRepository.assignStockToUser("MSFT", "user1");

            assertThat(stockUserAssignmentExists("MSFT", "user1")).isTrue();
        }

        @Test
        @DisplayName("throws StockNotExistsException when stock does not exist")
        void test2() {
            insertUser(new User("user1", true, "user1@email.com", LocalTime.now(), "Europe/Berlin"));

            assertThatThrownBy(() -> stockRepository.assignStockToUser("MSFT", "user1"))
                .isInstanceOf(StockNotExistsException.class);
        }

        @Test
        @DisplayName("throws DataIntegrityViolationException when user does not exist")
        void test3() {
            insertStock(new Stock("MSFT", "Microsoft", "https://msft-news.com"));

            assertThatThrownBy(() -> stockRepository.assignStockToUser("MSFT", "user1"))
                .isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("throws DataIntegrityViolationException when assignment already exists")
        void test4() {
            insertStock(new Stock("MSFT", "Microsoft", "https://msft-news.com"));
            insertUser(new User("user1", true, "user1@email.com", LocalTime.now(), "Europe/Berlin"));
            stockRepository.assignStockToUser("MSFT", "user1");

            assertThatThrownBy(() -> stockRepository.assignStockToUser("MSFT", "user1"))
                .isInstanceOf(DataIntegrityViolationException.class);
        }

    }

    @Nested
    class RemoveStockFromUser {

        @Test
        @DisplayName("removes correct stock-user assignment")
        void test() {
            insertStock(new Stock("MSFT", "Microsoft", "https://msft-news.com"));
            insertStock(new Stock("AAPL", "Apple", "https://aapl-news.com"));
            insertUser(new User("user1", true, "user1@email.com", LocalTime.now(), "Europe/Berlin"));
            insertUser(new User("user2", true, "user2@email.com", LocalTime.now(), "Europe/Berlin"));
            insertUserStockJunction("user1", "MSFT");
            insertUserStockJunction("user1", "AAPL");
            insertUserStockJunction("user2", "MSFT");

            stockRepository.removeStockFromUser("MSFT", "user1");

            assertThat(stockUserAssignmentExists("MSFT", "user1")).isFalse();
            assertThat(stockUserAssignmentExists("AAPL", "user1")).isTrue();
            assertThat(stockUserAssignmentExists("MSFT", "user2")).isTrue();
        }

    }

    @Nested
    class RemoveStockIfUnassigned {

        @Test
        @DisplayName("removes correct unassigned stock")
        void test1() {
            var msftStock = new Stock("MSFT", "Microsoft", "https://msft-news.com");
            insertStock(msftStock);
            insertUser(new User("user1", true, "user1@email.com", LocalTime.now(), "Europe/Berlin"));
            insertUserStockJunction("user1", "MSFT");
            insertStock(new Stock("AAPL", "Apple", "https://aapl-news.com"));

            stockRepository.removeStockIfUnassigned("AAPL");

            assertThat(getAllStocks()).containsExactly(msftStock);
        }

        @Test
        @DisplayName("does not remove assigned stock")
        void test2() {
            var msftStock = new Stock("MSFT", "Microsoft", "https://msft-news.com");
            insertStock(msftStock);
            insertUser(new User("user1", true, "user1@email.com", LocalTime.now(), "Europe/Berlin"));
            insertUserStockJunction("user1", "MSFT");

            stockRepository.removeStockIfUnassigned("MSFT");

            assertThat(getAllStocks()).containsExactly(msftStock);
        }

    }

    @Nested
    class UpdateLatestNewsLink {

        @Test
        @DisplayName("removes correct unassigned stock")
        void test() {
            insertStock(new Stock("MSFT", "Microsoft", "https://msft-news-1.com"));

            stockRepository.updateLatestNewsLink("MSFT", "https://msft-news-2.com");

            assertThat(getAllStocks()).containsExactly(new Stock("MSFT", "Microsoft", "https://msft-news-2.com"));
        }

    }

    @Nested
    class GetAllStocks {

        @Test
        @DisplayName("returns all stocks")
        void test() {
            var stock1 = new Stock("MSFT", "Microsoft", "https://msft-news.com");
            var stock2 = new Stock("AAPL", "Apple", "https://aapl-news.com");
            var stock3 = new Stock("AMZN", "Amazon", "https://amzn-news.com");
            insertStock(stock1);
            insertStock(stock2);
            insertStock(stock3);

            assertThat(stockRepository.getAllStocks()).containsExactlyInAnyOrder(stock1, stock2, stock3);
        }

    }

    @Nested
    class GetStocksForUser  {

        @Test
        @DisplayName("returns correct stocks")
        void test() {
            var stock1 = new Stock("MSFT", "Microsoft", "https://msft-news.com");
            var stock2 = new Stock("AAPL", "Apple", "https://aapl-news.com");
            var stock3 = new Stock("AMZN", "Amazon", "https://amzn-news.com");
            insertStock(stock1);
            insertStock(stock2);
            insertStock(stock3);
            insertUser(new User("user1", true, "user1@email.com", LocalTime.now(), "Europe/Berlin"));
            insertUser(new User("user2", true, "user2@email.com", LocalTime.now(), "Europe/Berlin"));
            insertUserStockJunction("user1", "MSFT");
            insertUserStockJunction("user1", "AAPL");
            insertUserStockJunction("user2", "AMZN");

            assertThat(stockRepository.getStocksForUser("user1")).containsExactlyInAnyOrder(stock1, stock2);
        }

    }

}
