package course.project.ua.tirevault.Repositories;

import course.project.ua.tirevault.Entities.Models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IVehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findAllByOrderByBrandAscModelAscYearAsc();
}