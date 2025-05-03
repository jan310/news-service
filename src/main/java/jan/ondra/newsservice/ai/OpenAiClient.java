package jan.ondra.newsservice.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jan.ondra.newsservice.dto.NewsArticleAnalysis;
import jan.ondra.newsservice.dto.ChatGptRequestBody;
import jan.ondra.newsservice.dto.ChatGptResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
        @Value("${external-api.openai.api-key}") String apiKey,
        @Value("${external-api.openai.model}") String openAiModel,
        RestClient.Builder restClientBuilder,
        ObjectMapper objectMapper
    ) {
        this.restClient = restClientBuilder
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader(AUTHORIZATION, "Bearer " + apiKey)
            .build();
        this.objectMapper = objectMapper;
        this.openAiModel = openAiModel;
    }

    public NewsArticleAnalysis evaluateAndSummarizeCompanyNews(String companyName, String newsArticle) {
        ChatGptResponseBody response;

        try {
            response = restClient
                .post()
                .uri("/responses")
                .contentType(APPLICATION_JSON)
                .body(new ChatGptRequestBody(openAiModel, companyName, newsArticle))
                .retrieve()
                .body(ChatGptResponseBody.class);
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
