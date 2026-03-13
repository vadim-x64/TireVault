package course.project.ua.tirevault.Controllers.RESTful_API;

import course.project.ua.tirevault.Services.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Відгуки", description = "Модерація відгуків")
public class ReviewApiController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Отримати всі відгуки")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Видалити відгук")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok().build();
    }
}