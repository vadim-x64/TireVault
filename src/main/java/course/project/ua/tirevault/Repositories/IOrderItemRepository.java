package course.project.ua.tirevault.Repositories;

import course.project.ua.tirevault.Entities.Models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IOrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Modifying
    @Query("delete from OrderItem oi where oi.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}