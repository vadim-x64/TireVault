package course.project.ua.tirevault.Controllers.RESTful_API;

import course.project.ua.tirevault.Entities.Enums.PaymentMethod;
import course.project.ua.tirevault.Services.ServiceRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/service-requests")
@Tag(name = "Заявки на СТО", description = "Управління заявками на обслуговування")
public class ServiceRequestApiController {

    @Autowired private ServiceRequestService serviceRequestService;

    @GetMapping("/active")
    @Operation(summary = "Активні заявки")
    public ResponseEntity<?> getActive() {
        return ResponseEntity.ok(serviceRequestService.getAllActive());
    }

    @GetMapping("/completed")
    @Operation(summary = "Завершені заявки")
    public ResponseEntity<?> getCompleted() {
        return ResponseEntity.ok(serviceRequestService.getAllCompleted());
    }

    @PutMapping("/{id}/accept")
    @Operation(summary = "Прийняти заявку")
    public ResponseEntity<?> accept(@PathVariable Long id) {
        serviceRequestService.accept(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/schedule")
    @Operation(summary = "Запланувати заявку (формат: yyyy-MM-dd'T'HH:mm)")
    public ResponseEntity<?> schedule(@PathVariable Long id, @RequestParam String scheduledAt) {
        LocalDateTime dt = LocalDateTime.parse(scheduledAt,
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        serviceRequestService.schedule(id, dt);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Завершити заявку")
    public ResponseEntity<?> complete(@PathVariable Long id, @RequestParam String paymentMethod) {
        serviceRequestService.complete(id, PaymentMethod.valueOf(paymentMethod));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Видалити заявку")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        serviceRequestService.delete(id);
        return ResponseEntity.ok().build();
    }
}