package course.project.ua.tirevault.Repositories;

import course.project.ua.tirevault.Entities.Models.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductCategoryRepository extends JpaRepository<ProductCategory, Long> {}