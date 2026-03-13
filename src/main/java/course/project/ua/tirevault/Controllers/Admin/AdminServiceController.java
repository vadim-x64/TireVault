package course.project.ua.tirevault.Controllers.Admin;

import course.project.ua.tirevault.Services.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminServiceController {
    @Autowired
    private ServiceRequestService serviceRequestService;

    @GetMapping("/admin/services")
    public String servicesPage(Model model) {
        model.addAttribute("activeOrders", serviceRequestService.getAllActive());
        model.addAttribute("completedOrders", serviceRequestService.getAllCompleted());
        model.addAttribute("page", "admin/services");
        return "index";
    }
}