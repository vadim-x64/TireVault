package course.project.ua.tirevault.Controllers;

import course.project.ua.tirevault.Services.WorkServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WorkServiceController {

    @Autowired
    private WorkServiceManager workServiceManager;

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("categories", workServiceManager.getAllCategories());
        model.addAttribute("workServices", workServiceManager.getAllWorkServices());
        model.addAttribute("selectedCategoryId", null);
        model.addAttribute("page", "autoservices");
        return "index";
    }

    @GetMapping("/services/category/{id}")
    public String servicesByCategory(@PathVariable Long id, Model model) {
        model.addAttribute("categories", workServiceManager.getAllCategories());
        model.addAttribute("workServices", workServiceManager.getWorkServicesByCategory(id));
        model.addAttribute("selectedCategoryId", id);
        model.addAttribute("page", "autoservices");
        return "index";
    }

    @GetMapping("/services/{id}")
    public String serviceDetail(@PathVariable Long id, Model model) {
        var ws = workServiceManager.getWorkServiceById(id);
        if (ws.isEmpty()) return "redirect:/services";
        model.addAttribute("workService", ws.get());
        model.addAttribute("page", "autoservicedetail");
        return "index";
    }
}