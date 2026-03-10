package course.project.ua.tirevault.Repositories;

import course.project.ua.tirevault.Entities.Models.Review;
import course.project.ua.tirevault.Entities.Models.ReviewLike;
import course.project.ua.tirevault.Entities.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface IReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByUserAndReview(User user, Review review);
    int countByReview(Review review);
    boolean existsByUserAndReview(User user, Review review);
    List<ReviewLike> findByUserAndReviewIn(User user, List<Review> reviews);
}