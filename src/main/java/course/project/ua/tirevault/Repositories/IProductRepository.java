package course.project.ua.tirevault.Repositories;

import course.project.ua.tirevault.Entities.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByOrderByIdAsc();
    List<Product> findByCategoryIdOrderByIdAsc(Long categoryId);
}