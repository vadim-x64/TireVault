package course.project.ua.tirevault.Repositories;

import course.project.ua.tirevault.Entities.Models.WorkService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IWorkServiceRepository extends JpaRepository<WorkService, Long> {
    List<WorkService> findAllByOrderByIdAsc();
    List<WorkService> findByCategoryIdOrderByIdAsc(Long categoryId);
}