package config;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestRestTemplateConfig {

    @Bean
    public TestRestTemplate restTemplate() {
        return new TestRestTemplate();
    }
}
