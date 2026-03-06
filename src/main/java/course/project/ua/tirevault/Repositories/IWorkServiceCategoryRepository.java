package course.project.ua.tirevault.Repositories;

import course.project.ua.tirevault.Entities.Models.WorkServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IWorkServiceCategoryRepository extends JpaRepository<WorkServiceCategory, Long> {
    List<WorkServiceCategory> findAllByOrderByIdAsc();
}