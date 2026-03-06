package course.project.ua.tirevault.Controllers.Admin;

import course.project.ua.tirevault.Entities.Enums.UserRole;
import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminUserController {

    @Autowired
    private IUserRepository userRepository;

    @GetMapping("/admin/users")
    public String usersPage(Model model) {
        List<User> users = userRepository.findAllByRoleNotOrderByIdAsc(UserRole.ADMIN);
        model.addAttribute("users", users);
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("page", "admin/users");
        return "index";
    }

    @PostMapping("/admin/users/{id}/role")
    public String changeRole(@PathVariable Long id,
                             @RequestParam String role) {
        userRepository.findById(id).ifPresent(user -> {
            user.setRole(UserRole.valueOf(role));
            userRepository.save(user);
        });
        return "redirect:/admin/users";
    }
}