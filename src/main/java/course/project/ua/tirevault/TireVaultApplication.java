package course.project.ua.tirevault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@SpringBootApplication
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 604800)
public class TireVaultApplication {
    public static void main(String[] args) {
        SpringApplication.run(TireVaultApplication.class, args);
    }
}