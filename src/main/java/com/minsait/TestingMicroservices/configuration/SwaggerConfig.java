package com.minsait.TestingMicroservices.configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI springOpenAPI(){
        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(new Info().title("Mi primer microservicio")
                        .description("Ya tiene pruebas unitarias")
                        .version("0.0.1.SNAPSHOT"))
                .externalDocs(new ExternalDocumentation()
                        .description("springdoc-openapi")
                        .url("http://springdoc.org"));
    }
}
