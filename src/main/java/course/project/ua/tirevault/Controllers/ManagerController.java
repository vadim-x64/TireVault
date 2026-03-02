package course.project.ua.tirevault.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManagerController {

    @GetMapping("/manager")
    public String managerPage(Model model) {
        model.addAttribute("page", "manager");
        return "index";
    }
}