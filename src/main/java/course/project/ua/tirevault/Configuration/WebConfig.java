package course.project.ua.tirevault.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SecurityInterceptor securityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Застосовуємо наш фільтр безпеки ТІЛЬКИ для шляхів, що починаються з /manager
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/manager/**");
    }
}