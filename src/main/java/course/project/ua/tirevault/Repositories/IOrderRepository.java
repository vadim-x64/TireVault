package course.project.ua.tirevault.Repositories;

import course.project.ua.tirevault.Entities.Enums.OrderStatus;
import course.project.ua.tirevault.Entities.Models.Order;
import course.project.ua.tirevault.Entities.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IOrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserAndStatusInOrderByCreatedAtDesc(User user, List<OrderStatus> statuses);
    List<Order> findByStatusInOrderByCreatedAtDesc(List<OrderStatus> statuses);
    long countByStatus(OrderStatus status);
    long countByUserAndSeenFalse(User user);
    List<Order> findByUserAndSeenFalse(User user);
}