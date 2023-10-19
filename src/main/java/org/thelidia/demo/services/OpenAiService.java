package org.thelidia.demo.services;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;
import dev.langchain4j.model.openai.OpenAiChatModel;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thelidia.demo.DemoConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class OpenAiService {
    private final DemoConfig demoConfig;
    private ChatLanguageModel model;

    @PostConstruct
    private void initModel() {
        model =
                OpenAiChatModel.builder()
                        .apiKey("demo_")
                        .baseUrl(demoConfig.getOpenaiProxyUrl())
                        .build();
    }

    @WithSpan
    public String demoWelcomeMessage() {
        return model.generate("write a funny greeting message for a demo. The message must short, no more than 15 words.");
    }

    public String greeting(String name) {
        PromptTemplate promptTemplate = PromptTemplate.from("write a greeting message for {{it}}. The message must short, no more than 10 words");
        Prompt prompt = promptTemplate.apply(name);
        return model.generate(prompt.text());
    }

    @WithSpan
    public CompletableFuture<String> greetingAsync(String name) {
        return CompletableFuture.supplyAsync(() -> greeting(name), Executors.newVirtualThreadPerTaskExecutor());
    }

    @StructuredPrompt({
            "For each of these names: {{names}}, write a short and funny greeting message.",
            "Structure your answer by putting each message on a new line.",
            "Keep the same ordre.",
    })
    static class MultiNamesGreeting {
        private List<String> names;
    }

    @WithSpan
    public Flux<String> greetingsAsync(List<String> names) {
        CompletableFuture<List<String>> futureList = CompletableFuture.supplyAsync(() -> greetings(names), Executors.newVirtualThreadPerTaskExecutor());
        Mono<List<String>> monoList = Mono.fromFuture(() -> futureList);
        return monoList.flatMapMany(Flux::fromIterable);
    }

    public List<String> greetings(List<String> names) {
        MultiNamesGreeting multiNamesGreeting = new MultiNamesGreeting();
        multiNamesGreeting.names = names;

        Prompt prompt = StructuredPromptProcessor.toPrompt(multiNamesGreeting);

        AiMessage aiMessage = model.generate(prompt.toUserMessage()).content();
        List<String> messages = Arrays.stream(aiMessage.text().split("\n"))
                .map(msg -> msg.trim())
                .toList();

        return names.stream()
                .map(name -> findMessageFor(name, messages))
                .toList();
    }

    private String findMessageFor(String name, List<String> messages) {
        return messages.stream()
                .filter(msg -> msg.contains(name))
                .findFirst()
                .orElse("Hello " + name + " !");
    }
}
