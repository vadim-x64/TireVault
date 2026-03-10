package course.project.ua.tirevault.Controllers;

import course.project.ua.tirevault.Entities.Enums.ReviewTargetType;
import course.project.ua.tirevault.Entities.Enums.UserRole;
import course.project.ua.tirevault.Entities.Models.Review;
import course.project.ua.tirevault.Entities.Models.ReviewNotification;
import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Services.ProductService;
import course.project.ua.tirevault.Services.ReviewService;
import course.project.ua.tirevault.Services.WorkServiceManager;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class ReviewController {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WorkServiceManager workServiceManager;

    @PostMapping("/reviews/add")
    public String addReview(@RequestParam String targetType,
                            @RequestParam Long targetId,
                            @RequestParam String content,
                            @RequestParam(required = false) Long parentId,
                            @RequestParam(required = false) Long replyToReviewId,
                            @RequestParam(required = false) Long replyToUserId,
                            HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/auth";

        String stripped = content.trim().replaceFirst("^@\\S+\\s*", "").trim();
        if (stripped.isEmpty()) return buildRedirect(targetType, targetId);

        ReviewTargetType type = ReviewTargetType.valueOf(targetType);
        reviewService.addReview(user, type, targetId, content.trim(),
                parentId, replyToReviewId, replyToUserId);
        return buildRedirect(targetType, targetId) + "#reviews";
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id,
                               @RequestParam String targetType,
                               @RequestParam Long targetId,
                               @RequestParam(required = false) String from,
                               HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/auth";

        reviewService.getReviewById(id).ifPresent(review -> {
            boolean canDelete = review.getUser().getId().equals(user.getId())
                    || user.getRole() == UserRole.ADMIN
                    || user.getRole() == UserRole.MANAGER;
            if (canDelete) reviewService.deleteReview(id);
        });

        if ("myreviews".equals(from)) return "redirect:/myreviews";
        return buildRedirect(targetType, targetId) + "#reviews";
    }

    @PostMapping("/reviews/{id}/like")
    @ResponseBody
    public Map<String, Object> toggleLike(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return Map.of("error", "not_logged_in");
        return reviewService.toggleLike(id, user);
    }

    @GetMapping("/myreviews")
    public String myReviews(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/auth";

        List<Review> reviews = reviewService.getReviewsByUser(user);
        List<ReviewNotification> notifications = reviewService.getNotifications(user);
        reviewService.markAllNotificationsSeen(user);

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

        model.addAttribute("myReviews", reviews);
        model.addAttribute("notifications", notifications);
        model.addAttribute("targetNames", targetNames);
        model.addAttribute("targetLinks", targetLinks);
        model.addAttribute("page", "myreviews");
        return "index";
    }

    private String buildRedirect(String targetType, Long targetId) {
        return "PRODUCT".equals(targetType)
                ? "redirect:/products/" + targetId
                : "redirect:/services/" + targetId;
    }
}