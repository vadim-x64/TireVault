package course.project.ua.tirevault.Controllers.Manager;

import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Services.ServiceRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManagerController {
    @Autowired
    private ServiceRequestService serviceRequestService;

    @GetMapping("/manager/services")
    public String managerPage(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !loggedUser.getRole().name().equals("MANAGER")) {
            return "redirect:/";
        }

        model.addAttribute("activeOrders", serviceRequestService.getAllActive());
        model.addAttribute("completedOrders", serviceRequestService.getAllCompleted());
        model.addAttribute("page", "manager/services");
        return "index";
    }
}