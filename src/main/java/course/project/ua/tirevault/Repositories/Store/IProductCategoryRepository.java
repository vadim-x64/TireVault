package course.project.ua.tirevault.Repositories.Store;

import course.project.ua.tirevault.Entities.Models.Store.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductCategoryRepository extends JpaRepository<ProductCategory, Long> {}