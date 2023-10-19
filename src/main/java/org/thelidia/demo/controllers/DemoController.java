package org.thelidia.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.thelidia.demo.services.OpenAiService;

import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
public class DemoController {
    private final OpenAiService openAiService;

    @QueryMapping
    String demo() {
        return openAiService.demoWelcomeMessage();
    }

}
