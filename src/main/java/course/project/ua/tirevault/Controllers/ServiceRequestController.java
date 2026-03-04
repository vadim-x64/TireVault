package course.project.ua.tirevault.Controllers;

import course.project.ua.tirevault.Entities.Enums.PaymentMethod;
import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Services.ServiceRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Controller
public class ServiceRequestController {
    @Autowired
    private ServiceRequestService serviceRequestService;

    @PostMapping("/service/request")
    public String submitRequest(
            @RequestParam String customerName, @RequestParam String phone,
            @RequestParam String city, @RequestParam(required = false) String description,
            HttpSession session, RedirectAttributes redirectAttributes) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            redirectAttributes.addFlashAttribute("authRequired", true);
            return "redirect:/auth";
        }

        try {
            String cleanDigits = phone.replaceAll("\\D+", "");
            if (cleanDigits.startsWith("38") && cleanDigits.length() > 10) {
                cleanDigits = cleanDigits.substring(2);
            }
            if (cleanDigits.length() != 10) {
                throw new IllegalArgumentException("Невірний формат телефону. Має бути рівно 10 цифр (наприклад, 050-123-45-67).");
            }
            String fullPhone = "+38" + cleanDigits;
            serviceRequestService.create(customerName, fullPhone, city, description, loggedUser);
            redirectAttributes.addFlashAttribute("requestSuccess", true);
            return "redirect:/myservices";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("requestError", e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/myservices")
    public String myOrders(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/auth";

        serviceRequestService.markAllSeenByUser(loggedUser);
        model.addAttribute("activeOrders", serviceRequestService.getActiveByUser(loggedUser));
        model.addAttribute("completedOrders", serviceRequestService.getCompletedByUser(loggedUser));
        model.addAttribute("page", "myservices");
        return "index";
    }

    // ---------- Manager endpoints ----------

    @PostMapping("/manager/services/{id}/accept")
    public String acceptRequest(@PathVariable Long id, HttpSession session) {
        if (!isManager(session)) return "redirect:/";
        serviceRequestService.accept(id);
        return "redirect:/manager/services";
    }

    @PostMapping("/manager/services/{id}/schedule")
    public String scheduleRequest(@PathVariable Long id,
                                  @RequestParam String scheduledAt,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!isManager(session)) return "redirect:/";
        try {
            LocalDateTime dateTime = LocalDateTime.parse(scheduledAt,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            serviceRequestService.schedule(id, dateTime);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("scheduleError", e.getMessage());
        }
        return "redirect:/manager/services";
    }

    @PostMapping("/manager/services/{id}/unschedule")
    public String unscheduleRequest(@PathVariable Long id, HttpSession session) {
        if (!isManager(session)) return "redirect:/";
        serviceRequestService.unschedule(id);
        return "redirect:/manager/services";
    }

    @PostMapping("/manager/services/{id}/complete")
    public String completeRequest(@PathVariable Long id,
                                  @RequestParam String paymentMethod,
                                  HttpSession session) {
        if (!isManager(session)) return "redirect:/";
        serviceRequestService.complete(id, PaymentMethod.valueOf(paymentMethod));
        return "redirect:/manager/services";
    }

    @PostMapping("/manager/services/{id}/cancel")
    public String cancelByManager(@PathVariable Long id, HttpSession session) {
        if (!isManager(session)) return "redirect:/";
        User loggedUser = (User) session.getAttribute("loggedUser");
        serviceRequestService.cancel(id, loggedUser);
        return "redirect:/manager/services";
    }

    // ---------- Client endpoint ----------

    @PostMapping("/myservices/{id}/cancel")
    public String cancelByClient(@PathVariable Long id, HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/auth";
        try {
            serviceRequestService.cancel(id, loggedUser);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("cancelError", e.getMessage());
        }
        return "redirect:/myservices";
    }

    private boolean isManager(HttpSession session) {
        User u = (User) session.getAttribute("loggedUser");
        return u != null && u.getRole().name().equals("MANAGER");
    }
}