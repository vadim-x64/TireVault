package course.project.ua.tirevault.Controllers;

import course.project.ua.tirevault.Entities.Models.Client.User;
import course.project.ua.tirevault.Services.ServiceRequestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {
    @Autowired
    private ServiceRequestService serviceRequestService;

    @ModelAttribute("pendingOrdersCount")
    public long pendingOrdersCount() {
        return serviceRequestService.countPending();
    }

    @ModelAttribute("unseenOrdersCount")
    public long unseenOrdersCount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return 0;
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return 0;
        return serviceRequestService.countUnseenByUser(loggedUser);
    }
}