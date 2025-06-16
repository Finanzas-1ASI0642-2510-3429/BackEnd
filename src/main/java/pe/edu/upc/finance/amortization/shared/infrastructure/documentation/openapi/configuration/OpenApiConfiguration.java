package pe.edu.upc.finance.amortization.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI financePlatformOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("French Amortization API")
                        .description("REST API for calculating and managing loan amortization using the French method (equal installment payments). " +
                                "Provides endpoints for amortization schedules, payment calculations, and loan simulations.")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0")
                                .url("https://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("French Amortization Documentation")
                        .url("https://github.com/your-repo/french-amortization-api"));
    }
}