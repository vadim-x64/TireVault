package course.project.ua.tirevault.Configuration;

import course.project.ua.tirevault.Entities.Enums.UserRole;
import course.project.ua.tirevault.Entities.Models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SwaggerAccessInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("loggedUser") : null;

        if (user == null) {
            response.sendRedirect("/auth");
            return false;
        }

        String uri = request.getRequestURI();
        UserRole role = user.getRole();

        if (isAdminDocsRequest(uri)) {
            if (role != UserRole.ADMIN) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Документація адміністратора - доступ заборонено");
                return false;
            }
        }

        if (isManagerDocsRequest(uri)) {
            if (role != UserRole.ADMIN && role != UserRole.MANAGER) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Документація менеджера - доступ заборонено");
                return false;
            }
        }

        return true;
    }

    private boolean isAdminDocsRequest(String uri) {
        return uri.matches(".*/v3/api-docs/admin(/.*)?");
    }

    private boolean isManagerDocsRequest(String uri) {
        return uri.matches(".*/v3/api-docs/manager(/.*)?");
    }
}