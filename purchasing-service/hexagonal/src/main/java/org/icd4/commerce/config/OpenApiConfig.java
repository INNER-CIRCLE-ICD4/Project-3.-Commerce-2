package org.icd4.commerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger 설정.
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${spring.application.name:purchasing-service}")
    private String applicationName;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("구매 서비스 API")
                .version("1.0.0")
                .description("장바구니 및 주문 관리를 위한 REST API")
                .contact(new Contact()
                    .name("ICD4 Commerce Team")
                    .email("support@icd4commerce.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Local Development Server"),
                new Server()
                    .url("https://api.icd4commerce.com")
                    .description("Production Server")
            ));
    }
}