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

    @GetMapping("/auth")
    public String authPage(Model model, HttpSession session) {
        if (session.getAttribute("loggedUser") != null) {
            return "redirect:/";
        }
        model.addAttribute("page", "auth");
        return "index";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            User user = authService.login(username, password);
            session.setAttribute("loggedUser", user);
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loginError", e.getMessage());
            redirectAttributes.addFlashAttribute("enteredUsername", username);
            return "redirect:/auth";
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam String firstName, @RequestParam String lastName,
                           @RequestParam(required = false) String middleName, @RequestParam String phone,
                           @RequestParam String username, @RequestParam String password, HttpSession session,
                           RedirectAttributes redirectAttributes) {
        try {
            // Залишаємо тільки цифри
            String cleanDigits = phone.replaceAll("\\D+", "");

            // Якщо номер починається з 38 (наприклад, якщо хтось ввів з кодом або автозаповнення спрацювало), відкидаємо їх
            if (cleanDigits.startsWith("38")) {
                cleanDigits = cleanDigits.substring(2);
            }

            // Перевіряємо, чи залишилося рівно 10 цифр (наприклад: 0501234567)
            if (cleanDigits.length() != 10) {
                throw new IllegalArgumentException("Невірний формат телефону. Має бути рівно 10 цифр (наприклад, 050-123-45-67).");
            }

            String fullPhone = "+38" + cleanDigits;

            User newUser = authService.register(firstName, lastName, middleName, fullPhone, username, password);
            session.setAttribute("loggedUser", newUser);
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("registerError", e.getMessage());
            redirectAttributes.addFlashAttribute("activeTab", "register");
            redirectAttributes.addFlashAttribute("regFirstName", firstName);
            redirectAttributes.addFlashAttribute("regLastName", lastName);
            redirectAttributes.addFlashAttribute("regMiddleName", middleName);
            redirectAttributes.addFlashAttribute("regPhone", phone);
            redirectAttributes.addFlashAttribute("regUsername", username);
            return "redirect:/auth";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}