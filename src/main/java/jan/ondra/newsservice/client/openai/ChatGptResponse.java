package jan.ondra.newsservice.client.openai;

import java.util.List;

public record ChatGptResponse(List<Output> output, Usage usage) {

    public record Output(List<Content> content) {
        public record Content(String text) {}
    }

    public record Usage(int input_tokens, int output_tokens) {}

}
