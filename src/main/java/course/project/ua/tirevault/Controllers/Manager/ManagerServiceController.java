package course.project.ua.tirevault.Controllers.Manager;

import course.project.ua.tirevault.Entities.Enums.PaymentMethod;
import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Services.ServiceRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ManagerServiceController {
    @Autowired
    private ServiceRequestService serviceRequestService;

    @PostMapping("/manager/services/{id}/accept")
    public String acceptRequest(@PathVariable Long id, HttpSession session) {
        if (!isManager(session)) return "redirect:/";
        serviceRequestService.accept(id);
        return "redirect:/manager/services";
    }

    @PostMapping("/manager/services/{id}/delete")
    public String deleteRequest(@PathVariable Long id, HttpSession session) {
        if (!isManager(session)) return "redirect:/";
        serviceRequestService.delete(id);
        return "redirect:/manager/services";
    }

    @PostMapping("/manager/services/{id}/schedule")
    public String scheduleRequest(@PathVariable Long id, @RequestParam String scheduledAt, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isManager(session)) return "redirect:/";

        try {
            LocalDateTime dateTime = LocalDateTime.parse(scheduledAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
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
    public String completeRequest(@PathVariable Long id, @RequestParam String paymentMethod, HttpSession session) {
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

    private boolean isManager(HttpSession session) {
        User u = (User) session.getAttribute("loggedUser");
        return u != null && u.getRole().name().equals("MANAGER");
    }
}