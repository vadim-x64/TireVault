package course.project.ua.tirevault.Controllers;

import course.project.ua.tirevault.Entities.Models.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {
    @GetMapping("/docs")
    public String swaggerRedirect(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");

        if (user == null) {
            return "redirect:/auth";
        }

        String group = switch (user.getRole()) {
            case ADMIN -> "admin";
            case MANAGER -> "manager";
            default -> "user";
        };

        return "redirect:/swagger-ui/index.html?urls.primaryName=" + group;
    }
}