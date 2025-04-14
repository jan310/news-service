package jan.ondra.newsservice.ai.dto;

import java.util.List;

public record ChatGptRequestBody(
    String model,
    int max_output_tokens,
    double temperature,
    double top_p,
    Text text,
    List<Input> input
) {

    private static final String systemMessage = "Read the provided news article and evaluate its relevancy for %s " +
        "investors. If the article contains relevant information, set relevant to true, evaluate the sentiment and " +
        "set it to either POSITIVE, NEUTRAL or NEGATIVE and provide a summary of the key points in at most two " +
        "sentences. Otherwise, if the article doesn't contain relevant information, set relevant to false and " +
        "sentiment and summary to an empty string.";

    public ChatGptRequestBody(String companyName, String newsArticle) {
        this(
            "gpt-4o-mini",
            200,
            0,
            0,
            new Text(),
            List.of(
                new Input("system", String.format(systemMessage, companyName)),
                new Input("user", newsArticle)
            )
        );
    }

    record Text(Format format) {
        private Text() {
            this(new Format());
        }
        record Format(String type, String name, boolean strict, Schema schema) {
            private Format() {
                this("json_schema", "queryResponse", true, new Schema());
            }
            record Schema(String type, Properties properties, List<String> required, boolean additionalProperties) {
                private Schema() {
                    this("object", new Properties(), List.of("relevant", "sentiment", "summary"), false);
                }
                record Properties(Relevant relevant, Sentiment sentiment, Summary summary) {
                    private Properties() {
                        this(new Relevant(), new Sentiment(), new Summary());
                    }
                    record Relevant(String type) {
                        private Relevant() {
                            this("boolean");
                        }
                    }
                    record Sentiment(String type) {
                        private Sentiment() {
                            this("string");
                        }
                    }
                    record Summary(String type) {
                        private Summary() {
                            this("string");
                        }
                    }
                }
            }
        }
    }

    record Input(String role, String content) {}

}
