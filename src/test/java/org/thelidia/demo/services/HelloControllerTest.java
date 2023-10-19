package org.thelidia.demo.services;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class HelloControllerTest {
    static WireMockServer wireMockServer;
    HttpGraphQlTester tester;

    @Autowired
    ApplicationContext applicationContext;

    @BeforeAll
    static void initWireMock() {
        wireMockServer = new WireMockServer(options()
                .port(8080)
//                .usingFilesUnderClasspath("/wiremock")
        );
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }


    @BeforeEach
    void initTester() {
        WebTestClient client =
                WebTestClient.bindToApplicationContext(applicationContext)
                        .configureClient()
                        .baseUrl("/graphql")
                        .build();
        tester = HttpGraphQlTester.create(client);
    }



    @Test
    void testWireMock() {
        System.out.println("Wiremock base URL : " + wireMockServer.baseUrl());
        System.out.println("Wiremock root dir : " + wireMockServer.getOptions().filesRoot().getPath());
        assertTrue(wireMockServer.isRunning());
        assertTrue(wireMockServer.getStubMappings().size() > 0);
        assertTrue(wireMockServer.isHttpEnabled());
    }
    @Test
    void testHello() {
        String query = """
                query {
                    hello
                }
                """;
        tester.document(query)
                .execute()
                .path("hello")
                .entity(String.class)
                .matches(s -> s.contains("Welcome to our demo"));
    }
}