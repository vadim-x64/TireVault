package course.project.ua.tirevault.Controllers.RESTful_API;

import course.project.ua.tirevault.Repositories.IVehicleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Автомобілі", description = "Довідник автомобілів")
public class VehicleApiController {

    @Autowired private IVehicleRepository vehicleRepository;

    @GetMapping
    @Operation(summary = "Отримати всі автомобілі")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(
                vehicleRepository.findAllByOrderByBrandAscModelAscYearAsc()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отримати автомобіль за ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return vehicleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Видалити автомобіль")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        vehicleRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}