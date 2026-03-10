package course.project.ua.tirevault.Controllers.Admin;

import course.project.ua.tirevault.Entities.Enums.ReviewTargetType;
import course.project.ua.tirevault.Entities.Models.Review;
import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Services.ProductService;
import course.project.ua.tirevault.Services.ReviewService;
import course.project.ua.tirevault.Services.WorkServiceManager;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminReviewController {
    @Autowired private ReviewService reviewService;
    @Autowired private ProductService productService;
    @Autowired private WorkServiceManager workServiceManager;

    @GetMapping({"/admin/reviews", "/manager/reviews"})
    public String reviewsPage(Model model, HttpSession session) {
        if (!hasAccess(session)) return "redirect:/";

        List<Review> reviews = reviewService.getAllReviews();
        Map<String, String> targetNames = new HashMap<>();
        Map<String, String> targetLinks = new HashMap<>();

        for (Review r : reviews) {
            String key = r.getTargetType() + "_" + r.getTargetId();
            if (!targetNames.containsKey(key)) {
                if (r.getTargetType() == ReviewTargetType.PRODUCT) {
                    productService.getProductById(r.getTargetId()).ifPresent(p -> {
                        targetNames.put(key, p.getName());
                        targetLinks.put(key, "/products/" + p.getId());
                    });
                } else {
                    workServiceManager.getWorkServiceById(r.getTargetId()).ifPresent(ws -> {
                        targetNames.put(key, ws.getName());
                        targetLinks.put(key, "/services/" + ws.getId());
                    });
                }
            }
        }

        model.addAttribute("reviews", reviews);
        model.addAttribute("targetNames", targetNames);
        model.addAttribute("targetLinks", targetLinks);
        model.addAttribute("page", "admin/adminreviews");
        return "index";
    }

    @PostMapping({"/admin/reviews/{id}/delete", "/manager/reviews/{id}/delete"})
    public String deleteReview(@PathVariable Long id, HttpSession session) {
        if (!hasAccess(session)) return "redirect:/";
        reviewService.deleteReview(id);
        User user = (User) session.getAttribute("loggedUser");
        return user.getRole().name().equals("MANAGER")
                ? "redirect:/manager/reviews"
                : "redirect:/admin/reviews";
    }

    private boolean hasAccess(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return false;
        String role = user.getRole().name();
        return "ADMIN".equals(role) || "MANAGER".equals(role);
    }
}