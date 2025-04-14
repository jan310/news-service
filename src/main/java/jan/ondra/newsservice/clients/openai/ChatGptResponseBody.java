package jan.ondra.newsservice.clients.openai;

import java.util.List;

record ChatGptResponseBody(List<Output> output, Usage usage) {

    record Output(List<Content> content) {
        record Content(String text) {}
    }

    record Usage(int input_tokens, int output_tokens) {}

}
