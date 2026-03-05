package course.project.ua.tirevault.Services;

import course.project.ua.tirevault.Entities.Models.WorkService;
import course.project.ua.tirevault.Entities.Models.WorkServiceCategory;
import course.project.ua.tirevault.Repositories.IWorkServiceCategoryRepository;
import course.project.ua.tirevault.Repositories.IWorkServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkServiceManager {

    @Autowired
    private IWorkServiceRepository workServiceRepository;

    @Autowired
    private IWorkServiceCategoryRepository categoryRepository;

    public List<WorkServiceCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<WorkService> getAllWorkServices() {
        return workServiceRepository.findAllByOrderByIdAsc();
    }

    public List<WorkService> getWorkServicesByCategory(Long categoryId) {
        return workServiceRepository.findByCategoryIdOrderByIdAsc(categoryId);
    }

    public Optional<WorkService> getWorkServiceById(Long id) {
        return workServiceRepository.findById(id);
    }
}