package course.project.ua.tirevault.Controllers.Admin;

import course.project.ua.tirevault.Entities.Enums.UserRole;
import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Repositories.ICartRepository;
import course.project.ua.tirevault.Repositories.IOrderRepository;
import course.project.ua.tirevault.Repositories.IServiceRequestRepository;
import course.project.ua.tirevault.Repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminUserController {
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ICartRepository cartRepository;

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private IServiceRequestRepository serviceRequestRepository;

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

    @PostMapping("/admin/users/{id}/block")
    public String toggleBlock(@PathVariable Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setBlocked(!user.isBlocked());
            userRepository.save(user);
        });
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/delete")
    @Transactional
    public String deleteUser(@PathVariable Long id) {
        userRepository.findById(id).ifPresent(user -> {
            cartRepository.findByUserId(id).ifPresent(cartRepository::delete);

            orderRepository.findByUserAndStatusInOrderByCreatedAtDesc(user,
                            List.of(course.project.ua.tirevault.Entities.Enums.OrderStatus.PENDING,
                                    course.project.ua.tirevault.Entities.Enums.OrderStatus.PROCESSING,
                                    course.project.ua.tirevault.Entities.Enums.OrderStatus.COMPLETED,
                                    course.project.ua.tirevault.Entities.Enums.OrderStatus.CANCELLED))
                    .forEach(orderRepository::delete);

            serviceRequestRepository.findByUserAndStatusInOrderByCreatedAtDesc(user,
                            List.of(course.project.ua.tirevault.Entities.Enums.ServiceRequestStatus.PENDING,
                                    course.project.ua.tirevault.Entities.Enums.ServiceRequestStatus.ACCEPTED,
                                    course.project.ua.tirevault.Entities.Enums.ServiceRequestStatus.SCHEDULED,
                                    course.project.ua.tirevault.Entities.Enums.ServiceRequestStatus.COMPLETED,
                                    course.project.ua.tirevault.Entities.Enums.ServiceRequestStatus.CANCELLED))
                    .forEach(serviceRequestRepository::delete);

            userRepository.delete(user);
        });
        return "redirect:/admin/users";
    }
}