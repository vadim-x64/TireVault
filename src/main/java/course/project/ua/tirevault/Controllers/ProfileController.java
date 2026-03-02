package course.project.ua.tirevault.Controllers;

import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Services.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @GetMapping("/profile")
    public String profilePage(Model model, HttpSession session) {
        // Перевіряємо чи авторизований користувач
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/auth";
        }

        // Встановлюємо вкладку за замовчуванням, якщо вона не задана через RedirectAttributes
        if (!model.containsAttribute("activeTab")) {
            model.addAttribute("activeTab", "main");
        }

        model.addAttribute("page", "profile");
        return "index"; // Повертаємо базовий шаблон, який підтягне profile.html
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam(required = false) String middleName,
                                @RequestParam String phone,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("loggedUser");
        if (currentUser == null) {
            return "redirect:/auth";
        }

        try {
            // Форматуємо телефон так само, як при реєстрації
            String cleanDigits = phone.replaceAll("\\D+", "");
            String fullPhone = cleanDigits.startsWith("38") ? "+" + cleanDigits : "+38" + cleanDigits;

            // Оновлюємо профіль
            User updatedUser = profileService.updateProfile(currentUser.getId(), firstName, lastName, middleName, fullPhone);

            // Оновлюємо сесію, щоб нові дані одразу відобразилися на сайті
            session.setAttribute("loggedUser", updatedUser);

            redirectAttributes.addFlashAttribute("profileSuccess", "Дані профілю успішно оновлено!");
            redirectAttributes.addFlashAttribute("activeTab", "main");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("profileError", e.getMessage());
            redirectAttributes.addFlashAttribute("activeTab", "main");
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/security")
    public String updateSecurity(@RequestParam String username,
                                 @RequestParam(required = false) String password,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("loggedUser");
        if (currentUser == null) {
            return "redirect:/auth";
        }

        try {
            profileService.updateSecurity(currentUser.getId(), username, password);

            // Інвалідуємо сесію (робимо логаут), оскільки дані входу змінилися
            session.invalidate();

            // Додаємо повідомлення про необхідність повторної авторизації
            // Використовуємо loginError (або можна створити successAlert в auth.html)
            redirectAttributes.addFlashAttribute("loginError", "Дані безпеки успішно змінено. Будь ласка, авторизуйтесь знову з новими даними.");
            redirectAttributes.addFlashAttribute("enteredUsername", username);

            return "redirect:/auth";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("securityError", e.getMessage());
            redirectAttributes.addFlashAttribute("activeTab", "security");
            return "redirect:/profile";
        }
    }
}
