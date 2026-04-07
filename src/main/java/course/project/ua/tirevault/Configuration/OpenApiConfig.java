package course.project.ua.tirevault.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI tirVaultOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TireVault - OpenAPI системи СТО")
                        .description("Документація API до курсової роботи")
                        .version("version is up to date"));
    }
}