package course.project.ua.tirevault.Controllers.RESTful_API;

import course.project.ua.tirevault.Entities.Enums.OrderStatus;
import course.project.ua.tirevault.Services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Замовлення", description = "Управління замовленнями магазину")
public class OrderApiController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/active")
    @Operation(summary = "Отримати активні замовлення")
    public ResponseEntity<?> getActive() {
        return ResponseEntity.ok(orderService.getAllActive());
    }

    @GetMapping("/completed")
    @Operation(summary = "Отримати завершені замовлення")
    public ResponseEntity<?> getCompleted() {
        return ResponseEntity.ok(orderService.getAllCompleted());
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Змінити статус замовлення")
    public ResponseEntity<?> setStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.setStatus(id, OrderStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Видалити замовлення")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.ok().build();
    }
}