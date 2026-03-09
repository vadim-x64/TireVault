package course.project.ua.tirevault.Controllers;

import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Services.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/myorders")
    public String myOrders(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/auth";

        orderService.markAllSeenByUser(loggedUser);
        model.addAttribute("activeOrders", orderService.getActiveByUser(loggedUser));
        model.addAttribute("completedOrders", orderService.getCompletedByUser(loggedUser));
        model.addAttribute("page", "myorders");
        return "index";
    }

    @PostMapping("/myorders/{id}/cancel")
    public String cancelByClient(@PathVariable Long id, HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/auth";
        try {
            orderService.cancel(id, loggedUser);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("cancelError", e.getMessage());
        }
        return "redirect:/myorders";
    }

    @PostMapping("/myorders/{id}/delete")
    public String deleteByClient(@PathVariable Long id, HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/auth";
        try {
            orderService.deleteByUser(id, loggedUser);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("cancelError", e.getMessage());
        }
        return "redirect:/myorders";
    }
}