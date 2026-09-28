package fr.cours.weather.api.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class MetNorwayConfig {

    @Value("${app.met-norway.base-url}")
    private String baseUrl;

    @Value("${app.met-norway.user-agent}")
    private String userAgent;

    @Bean("metNorwayApiHttpClient")
    public RestClient metNorwayApiHttpClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
