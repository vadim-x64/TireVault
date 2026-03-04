package course.project.ua.tirevault.Repositories;

import course.project.ua.tirevault.Entities.Models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ICartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}