package course.project.ua.tirevault.Controllers;

import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Services.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    @Autowired
    private AuthService authService;

    // Відображення сторінки
    @GetMapping("/auth")
    public String authPage(Model model, HttpSession session) {
        // Якщо користувач вже авторизований - кидаємо на головну
        if (session.getAttribute("loggedUser") != null) {
            return "redirect:/";
        }
        model.addAttribute("page", "auth");
        return "index";
    }

    // Обробка входу
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        try {
            User user = authService.login(username, password);
            // Зберігаємо юзера в сесію
            session.setAttribute("loggedUser", user);
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loginError", e.getMessage());
            return "redirect:/auth";
        }
    }

    // Обробка реєстрації
    @PostMapping("/register")
    public String register(@RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam(required = false) String middleName,
                           @RequestParam String phone,
                           @RequestParam String username,
                           @RequestParam String password,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        try {
            // Оскільки в HTML ми винесли +38 окремо, додаємо його до збереження
            String fullPhone = "+38 " + phone;
            User newUser = authService.register(firstName, lastName, middleName, fullPhone, username, password);

            // Одразу авторизуємо після успішної реєстрації
            session.setAttribute("loggedUser", newUser);
            return "redirect:/";
        } catch (Exception e) {
            // Передаємо помилку на фронт
            redirectAttributes.addFlashAttribute("registerError", e.getMessage());
            // Перемикаємо вкладку на реєстрацію при поверненні
            redirectAttributes.addFlashAttribute("activeTab", "register");
            return "redirect:/auth";
        }
    }

    // Вихід з акаунту
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Знищуємо сесію
        return "redirect:/";
    }
}