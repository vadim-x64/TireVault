package course.project.ua.tirevault.Controllers.RESTful_API;

import course.project.ua.tirevault.Services.WorkServiceManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/work-services")
@Tag(name = "Послуги СТО", description = "Каталог послуг та категорій")
public class WorkServiceApiController {
    @Autowired
    private WorkServiceManager workServiceManager;

    @GetMapping
    @Operation(summary = "Отримати всі послуги")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(workServiceManager.getAllWorkServices());
    }

    @GetMapping("/categories")
    @Operation(summary = "Отримати всі категорії послуг")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(workServiceManager.getAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отримати послугу за ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return workServiceManager.getWorkServiceById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Редагувати послугу")
    public ResponseEntity<?> edit(@PathVariable Long id, @RequestParam Long categoryId, @RequestParam String name, @RequestParam(required = false) String description, @RequestParam BigDecimal price, @RequestParam(required = false) String workingHours) {
        return workServiceManager.getWorkServiceById(id).map(ws -> {
            workServiceManager.getCategoryById(categoryId).ifPresent(ws::setCategory);
            ws.setName(name);
            ws.setDescription(description);
            ws.setPrice(price);
            ws.setWorkingHours(workingHours);
            workServiceManager.saveWorkService(ws);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Видалити послугу")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        workServiceManager.deleteWorkServiceById(id);
        return ResponseEntity.ok().build();
    }
}