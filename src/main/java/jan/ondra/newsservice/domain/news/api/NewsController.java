package jan.ondra.newsservice.domain.news.api;

import jan.ondra.newsservice.domain.news.model.CompanyNews;
import jan.ondra.newsservice.domain.news.service.NewsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public Map<String, List<CompanyNews>> getCompanyNewsForUserGroupedByTicker(@AuthenticationPrincipal Jwt jwt) {
        return newsService.getCompanyNewsForUserGroupedByTicker(jwt.getSubject());
    }

}
