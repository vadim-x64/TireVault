package course.project.ua.tirevault.Controllers.Admin;

import course.project.ua.tirevault.Services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/admin/orders")
    public String ordersPage(Model model) {
        model.addAttribute("activeOrders", orderService.getAllActive());
        model.addAttribute("completedOrders", orderService.getAllCompleted());
        model.addAttribute("page", "admin/orders");
        return "index";
    }
}