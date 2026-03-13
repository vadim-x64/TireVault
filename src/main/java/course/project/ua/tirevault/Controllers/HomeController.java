package course.project.ua.tirevault.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("page", "home");
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("page", "about");
        return "index";
    }

    @GetMapping("/contacts")
    public String contacts(Model model) {
        model.addAttribute("page", "contacts");
        return "index";
    }

    @GetMapping("/delivery")
    public String delivery(Model model) {
        model.addAttribute("page", "delivery");
        return "index";
    }

    @GetMapping("/gallery")
    public String gallery(Model model) {
        model.addAttribute("page", "gallery");
        return "index";
    }

    @GetMapping("/blog")
    public String blog(Model model) {
        model.addAttribute("page", "blog");
        return "index";
    }
}