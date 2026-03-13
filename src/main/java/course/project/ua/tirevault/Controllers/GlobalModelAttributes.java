package course.project.ua.tirevault.Controllers;

import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Services.CartService;
import course.project.ua.tirevault.Services.OrderService;
import course.project.ua.tirevault.Services.ReviewService;
import course.project.ua.tirevault.Services.ServiceRequestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalModelAttributes {
    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReviewService reviewService;

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

    @ModelAttribute("pendingProductOrdersCount")
    public long pendingProductOrdersCount() {
        return orderService.countPending();
    }

    @ModelAttribute("unseenProductOrdersCount")
    public long unseenProductOrdersCount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return 0;
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return 0;
        return orderService.countUnseenByUser(loggedUser);
    }

    @ModelAttribute("cartItemCount")
    public int cartItemCount(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return 0;
        return cartService.getCartItemCount(user);
    }

    @ModelAttribute("reviewNotificationCount")
    public long reviewNotificationCount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return 0;
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return 0;
        return reviewService.countUnseenNotifications(loggedUser);
    }
}