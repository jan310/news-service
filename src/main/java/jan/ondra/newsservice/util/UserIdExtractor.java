package jan.ondra.newsservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class UserIdExtractor {

    private final ObjectMapper objectMapper;
    private final Base64.Decoder base64Decoder = Base64.getUrlDecoder();

    public UserIdExtractor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String extractFromBearerToken(String token) {
        try {
            var jwtPayload = token.split("\\.")[1];
            var decodedJwtPayload = new String(base64Decoder.decode(jwtPayload));
            return objectMapper.readTree(decodedJwtPayload).get("sub").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
