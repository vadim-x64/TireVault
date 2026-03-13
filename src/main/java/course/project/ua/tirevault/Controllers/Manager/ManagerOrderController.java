package course.project.ua.tirevault.Controllers.Manager;

import course.project.ua.tirevault.Entities.Enums.OrderStatus;
import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Services.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ManagerOrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/manager/orders")
    public String managerOrders(Model model, HttpSession session) {
        if (!isManager(session)) return "redirect:/";
        model.addAttribute("activeOrders", orderService.getAllActive());
        model.addAttribute("completedOrders", orderService.getAllCompleted());
        model.addAttribute("page", "manager/orders");
        return "index";
    }

    @PostMapping("/manager/orders/{id}/status")
    public String setStatus(@PathVariable Long id,
                            @RequestParam String status,
                            HttpSession session) {
        if (!isManager(session)) return "redirect:/";
        orderService.setStatus(id, OrderStatus.valueOf(status));
        return "redirect:/manager/orders";
    }

    @PostMapping("/manager/orders/{id}/cancel")
    public String cancel(@PathVariable Long id, HttpSession session) {
        if (!isManager(session)) return "redirect:/";
        User user = (User) session.getAttribute("loggedUser");
        orderService.cancel(id, user);
        return "redirect:/manager/orders";
    }

    @PostMapping("/manager/orders/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!isManager(session)) return "redirect:/";
        orderService.delete(id);
        return "redirect:/manager/orders";
    }

    private boolean isManager(HttpSession session) {
        User u = (User) session.getAttribute("loggedUser");
        return u != null && u.getRole().name().equals("MANAGER");
    }
}