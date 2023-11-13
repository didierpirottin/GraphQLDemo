package org.thelidia.demo.controllers;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoaderOptions;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import org.springframework.stereotype.Controller;
import org.thelidia.demo.model.CustomerFilter;
import org.thelidia.demo.model.CustomerModel;
import org.thelidia.demo.services.OpenAiService;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static org.thelidia.demo.mappers.CustomerModelMapper.mapCustomerRecordToModel;

@Controller
@RequiredArgsConstructor
public class CustomerController {
    public static final String GREETING_DATALOADER = "GREETING_DATALOADER";
    private final OpenAiService openAiService;
    private final CustomerJooqService customerJooqService;

    private final BatchLoaderRegistry batchLoaderRegistry;

    @PostConstruct
    void initDataLoader() {
        batchLoaderRegistry.forTypePair(String.class, String.class)
                .withName(GREETING_DATALOADER)
                .withOptions(DataLoaderOptions.newOptions()
                        .setMaxBatchSize(100)
                )
                .registerBatchLoader((List<String> names, BatchLoaderEnvironment env)
                        -> generateGreetings(names));
    }

    private Flux<String> generateGreetings(List<String> names) {
        return openAiService.greetingsAsync(names);
    }


    @QueryMapping
    List<CustomerModel> customers(@Argument CustomerFilter filter, DataFetchingFieldSelectionSet fieldSelectionSet) {
        return customerJooqService.buildCustomerQuery(filter, fieldSelectionSet)
                .stream()
                .map(record -> mapCustomerRecordToModel(record))
                .collect(Collectors.toList());
    }

    @SchemaMapping(typeName = "Customer")
    String greeting(CustomerModel customer, DataFetchingEnvironment env) {
//    CompletableFuture<String> greeting(CustomerModel customer, DataFetchingEnvironment env) {
//        DataLoader<String, String> dataLoader = env.getDataLoader(GREETING_DATALOADER);
//        return dataLoader.load(customer.name());
//        return openAiService.gr   eetingAsync(customer.name());
        return openAiService.greeting(customer.firstName());
    }


}
