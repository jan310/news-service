package jan.ondra.newsservice.client.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jan.ondra.newsservice.domain.news.model.NewsArticleAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class OpenAiClient {

    private final Logger logger = LoggerFactory.getLogger(OpenAiClient.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String openAiModel;

    public OpenAiClient(
        @Value("${openai.api-key}") String apiKey,
        @Value("${openai.model}") String openAiModel,
        RestClient.Builder restClientBuilder,
        ObjectMapper objectMapper
    ) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(2_000);
        requestFactory.setReadTimeout(30_000);

        this.restClient = restClientBuilder
            .requestFactory(requestFactory)
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader(AUTHORIZATION, "Bearer " + apiKey)
            .build();
        this.objectMapper = objectMapper;
        this.openAiModel = openAiModel;
    }

    public NewsArticleAnalysis evaluateAndSummarizeCompanyNews(String companyName, String newsArticle) {
        ChatGptResponse response;

        try {
            response = restClient
                .post()
                .uri("/responses")
                .contentType(APPLICATION_JSON)
                .body(new ChatGptRequest(openAiModel, companyName, newsArticle))
                .retrieve()
                .body(ChatGptResponse.class);
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }

        logger.info("OpenAI input tokens used: {}", response.usage().input_tokens());
        logger.info("OpenAI output tokens used: {}", response.usage().output_tokens());

        try {
            return objectMapper.readValue(
                response.output().getFirst().content().getFirst().text(),
                NewsArticleAnalysis.class
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
