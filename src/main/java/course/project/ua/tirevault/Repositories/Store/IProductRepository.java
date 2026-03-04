package course.project.ua.tirevault.Repositories.Store;

import course.project.ua.tirevault.Entities.Models.Store.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
}