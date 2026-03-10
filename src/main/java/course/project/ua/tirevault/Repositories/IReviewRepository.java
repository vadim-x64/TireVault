package course.project.ua.tirevault.Repositories;

import course.project.ua.tirevault.Entities.Enums.ReviewTargetType;
import course.project.ua.tirevault.Entities.Models.Review;
import course.project.ua.tirevault.Entities.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTargetTypeAndTargetIdAndParentIsNullOrderByCreatedAtDesc(ReviewTargetType targetType, Long targetId);
    List<Review> findByUserOrderByCreatedAtDesc(User user);
    List<Review> findAllByOrderByCreatedAtDesc();
}