package jan.ondra.newsservice.domain.news.api;

import jan.ondra.newsservice.domain.news.model.CompanyNews;
import jan.ondra.newsservice.domain.news.service.NewsService;
import jan.ondra.newsservice.util.UserIdExtractor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;
    private final UserIdExtractor userIdExtractor;

    public NewsController(NewsService newsService, UserIdExtractor userIdExtractor) {
        this.newsService = newsService;
        this.userIdExtractor = userIdExtractor;
    }

    @GetMapping
    @ResponseStatus(OK)
    public Map<String, List<CompanyNews>> getCompanyNewsForUserGroupedByTicker(
        @RequestHeader(AUTHORIZATION) String bearerToken
    ) {
        return newsService.getCompanyNewsForUserGroupedByTicker(userIdExtractor.extractFromBearerToken(bearerToken));
    }

}
