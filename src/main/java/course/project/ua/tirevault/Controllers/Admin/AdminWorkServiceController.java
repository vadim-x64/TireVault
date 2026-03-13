package course.project.ua.tirevault.Controllers.Admin;

import course.project.ua.tirevault.Entities.Models.WorkService;
import course.project.ua.tirevault.Entities.Models.WorkServiceCategory;
import course.project.ua.tirevault.Services.WorkServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@Controller
public class AdminWorkServiceController {
    @Autowired
    private WorkServiceManager workServiceManager;

    @GetMapping("/admin/workservices")
    public String workServicesPage(Model model) {
        model.addAttribute("categories", workServiceManager.getAllCategories());
        model.addAttribute("workServices", workServiceManager.getAllWorkServices());
        model.addAttribute("page", "admin/workservices");
        return "index";
    }

    @PostMapping("/admin/workservices/categories/add")
    public String addCategory(@RequestParam String name) {
        WorkServiceCategory cat = new WorkServiceCategory();
        cat.setName(name);
        workServiceManager.saveCategory(cat);
        return "redirect:/admin/workservices";
    }

    @PostMapping("/admin/workservices/categories/{id}/edit")
    public String editCategory(@PathVariable Long id, @RequestParam String name) {
        workServiceManager.getCategoryById(id).ifPresent(cat -> {
            cat.setName(name);
            workServiceManager.saveCategory(cat);
        });

        return "redirect:/admin/workservices";
    }

    @PostMapping("/admin/workservices/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id) {
        workServiceManager.deleteCategoryById(id);
        return "redirect:/admin/workservices";
    }

    @PostMapping("/admin/workservices/add")
    public String addWorkService(@RequestParam Long categoryId, @RequestParam String name, @RequestParam(required = false) String description, @RequestParam BigDecimal price, @RequestParam(required = false) String workingHours) {
        workServiceManager.getCategoryById(categoryId).ifPresent(cat -> {
            WorkService ws = new WorkService();
            ws.setCategory(cat);
            ws.setName(name);
            ws.setDescription(description);
            ws.setPrice(price);
            ws.setWorkingHours(workingHours);
            workServiceManager.saveWorkService(ws);
        });

        return "redirect:/admin/workservices";
    }

    @PostMapping("/admin/workservices/{id}/edit")
    public String editWorkService(@PathVariable Long id, @RequestParam Long categoryId, @RequestParam String name, @RequestParam(required = false) String description, @RequestParam BigDecimal price, @RequestParam(required = false) String workingHours) {
        workServiceManager.getWorkServiceById(id).ifPresent(ws -> {
            workServiceManager.getCategoryById(categoryId).ifPresent(ws::setCategory);
            ws.setName(name);
            ws.setDescription(description);
            ws.setPrice(price);
            ws.setWorkingHours(workingHours);
            workServiceManager.saveWorkService(ws);
        });

        return "redirect:/admin/workservices";
    }

    @PostMapping("/admin/workservices/{id}/delete")
    public String deleteWorkService(@PathVariable Long id) {
        workServiceManager.deleteWorkServiceById(id);
        return "redirect:/admin/workservices";
    }
}