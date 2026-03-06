package course.project.ua.tirevault.Repositories;

import course.project.ua.tirevault.Entities.Models.ProductCategory;
import course.project.ua.tirevault.Entities.Models.WorkServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findAllByOrderByIdAsc();
}