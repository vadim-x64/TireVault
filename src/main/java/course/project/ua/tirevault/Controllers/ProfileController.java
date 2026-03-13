package course.project.ua.tirevault.Controllers;

import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Services.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @GetMapping("/profile")
    public String profilePage(Model model, HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/auth";
        }

        if (!model.containsAttribute("activeTab")) {
            model.addAttribute("activeTab", "main");
        }

        model.addAttribute("page", "profile");
        return "index";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String firstName, @RequestParam String lastName, @RequestParam(required = false) String middleName, @RequestParam String phone, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("loggedUser");

        if (currentUser == null) {
            return "redirect:/auth";
        }

        try {
            String cleanDigits = phone.replaceAll("\\D+", "");

            if (cleanDigits.startsWith("38")) {
                cleanDigits = cleanDigits.substring(2);
            }

            if (cleanDigits.length() != 10) {
                throw new IllegalArgumentException("Невірний формат телефону. Має бути рівно 10 цифр (наприклад, 050-123-45-67).");
            }

            String fullPhone = "+38" + cleanDigits;
            User updatedUser = profileService.updateProfile(currentUser.getId(), firstName, lastName, middleName, fullPhone);
            session.setAttribute("loggedUser", updatedUser);
            redirectAttributes.addFlashAttribute("profileSuccess", "Дані профілю успішно оновлено.");
            redirectAttributes.addFlashAttribute("activeTab", "main");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("profileError", e.getMessage());
            redirectAttributes.addFlashAttribute("activeTab", "main");
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/security")
    public String updateSecurity(@RequestParam String username, @RequestParam(required = false) String password, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("loggedUser");

        if (currentUser == null) {
            return "redirect:/auth";
        }

        try {
            profileService.updateSecurity(currentUser.getId(), username, password);
            session.invalidate();
            redirectAttributes.addFlashAttribute("loginError", "Дані безпеки успішно змінено. Будь ласка, авторизуйтесь знову з новими даними.");
            redirectAttributes.addFlashAttribute("enteredUsername", username);
            return "redirect:/auth";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("securityError", e.getMessage());
            redirectAttributes.addFlashAttribute("activeTab", "security");
            return "redirect:/profile";
        }
    }

    @PostMapping("/profile/delete")
    public String deleteAccount(@RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("loggedUser");

        if (currentUser == null) {
            return "redirect:/auth";
        }

        try {
            profileService.deleteAccount(currentUser.getId(), password);
            session.invalidate();
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("deleteAccountError", e.getMessage());
            redirectAttributes.addFlashAttribute("activeTab", "security");
            return "redirect:/profile";
        }
    }
}