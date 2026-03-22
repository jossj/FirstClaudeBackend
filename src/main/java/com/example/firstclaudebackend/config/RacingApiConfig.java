package com.example.firstclaudebackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RacingApiConfig {

    @Value("${racing-api.username:}")
    private String username;

    @Value("${racing-api.password:}")
    private String password;

    @Bean
    public RestTemplate racingApiRestTemplate(RestTemplateBuilder builder) {
        RestTemplateBuilder configured = builder;
        if (!username.isBlank() && !password.isBlank()) {
            configured = builder.basicAuthentication(username, password);
        }
        return configured.build();
    }
}
