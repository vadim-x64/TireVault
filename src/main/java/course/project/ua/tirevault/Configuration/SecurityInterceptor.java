package course.project.ua.tirevault.Configuration;

import course.project.ua.tirevault.Entities.Enums.UserRole;
import course.project.ua.tirevault.Entities.Models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SecurityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedUser");

        // Перевіряємо, чи користувач взагалі авторизований
        if (user == null) {
            // Якщо не авторизований, кидаємо на сторінку входу
            response.sendRedirect("/auth");
            return false;
        }

        // Перевіряємо роль (пускаємо Менеджера)
        if (user.getRole() != UserRole.MANAGER) {
            // Якщо роль звичайна (USER) - кидаємо помилку 403 (Forbidden)
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }
}