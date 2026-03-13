package course.project.ua.tirevault.Controllers.Admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminController {
    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("page", "admin/admin");
        return "index";
    }
}