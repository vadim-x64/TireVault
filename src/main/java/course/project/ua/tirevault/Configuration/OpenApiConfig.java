package course.project.ua.tirevault.Configuration;

import course.project.ua.tirevault.Entities.Enums.UserRole;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
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
                        .version("1.0"));
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user")
                .displayName("Користувач")
                .pathsToMatch("/api/**")
                .addOpenApiMethodFilter(method -> {
                    ApiRole apiRole = method.getAnnotation(ApiRole.class);
                    if (apiRole == null) return true;
                    return apiRole.value() == UserRole.USER;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi managerApi() {
        return GroupedOpenApi.builder()
                .group("manager")
                .displayName("Менеджер")
                .pathsToMatch("/api/**")
                .addOpenApiMethodFilter(method -> {
                    ApiRole apiRole = method.getAnnotation(ApiRole.class);
                    if (apiRole == null) return true;
                    return apiRole.value() == UserRole.USER
                            || apiRole.value() == UserRole.MANAGER;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("Адміністратор")
                .pathsToMatch("/api/**")
                .build();
    }
}