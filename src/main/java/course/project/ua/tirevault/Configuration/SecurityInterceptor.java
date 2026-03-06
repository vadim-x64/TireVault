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
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedUser");
        String requestURI = request.getRequestURI();

        if (user == null) {
            response.sendRedirect("/auth");
            return false;
        }

        UserRole userRole = user.getRole();

        if (requestURI.startsWith("/admin")) {
            if (userRole == UserRole.ADMIN) {
                return true; // Allow admin access to admin pages
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false; // Forbid non-admin access to admin pages
            }
        }

        if (userRole == UserRole.ADMIN || userRole == UserRole.MANAGER) {
            return true; // Allow admin and manager access to non-admin pages
        }

        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return false; // Forbid all other users
    }
}
