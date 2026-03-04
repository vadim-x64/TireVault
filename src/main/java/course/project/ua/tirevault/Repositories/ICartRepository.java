package course.project.ua.tirevault.Repositories;

import course.project.ua.tirevault.Entities.Models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ICartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
}