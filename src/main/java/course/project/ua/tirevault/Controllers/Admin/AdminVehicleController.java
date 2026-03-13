package course.project.ua.tirevault.Controllers.Admin;

import course.project.ua.tirevault.Entities.Models.Vehicle;
import course.project.ua.tirevault.Repositories.IProductRepository;
import course.project.ua.tirevault.Repositories.IVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminVehicleController {

    @Autowired
    private IVehicleRepository vehicleRepository;

    @Autowired
    private IProductRepository productRepository;

    @GetMapping("/admin/vehicles")
    public String vehiclesPage(Model model) {
        model.addAttribute("vehicles", vehicleRepository.findAllByOrderByBrandAscModelAscYearAsc());
        model.addAttribute("allProducts", productRepository.findAllByOrderByIdAsc());
        model.addAttribute("page", "admin/adminvehicles");
        return "index";
    }

    @PostMapping("/admin/vehicles/add")
    public String addVehicle(@RequestParam String brand,
                             @RequestParam String model,
                             @RequestParam Integer year,
                             @RequestParam String modification) {
        Vehicle v = new Vehicle();
        v.setBrand(brand);
        v.setModel(model);
        v.setYear(year);
        v.setModification(modification);
        vehicleRepository.save(v);
        return "redirect:/admin/vehicles";
    }

    @PutMapping("/admin/vehicles/{id}/edit")
    public String editVehicle(@PathVariable Long id,
                              @RequestParam String brand,
                              @RequestParam String model,
                              @RequestParam Integer year,
                              @RequestParam String modification) {
        vehicleRepository.findById(id).ifPresent(v -> {
            v.setBrand(brand);
            v.setModel(model);
            v.setYear(year);
            v.setModification(modification);
            vehicleRepository.save(v);
        });
        return "redirect:/admin/vehicles";
    }

    @DeleteMapping("/admin/vehicles/{id}/delete")
    public String deleteVehicle(@PathVariable Long id) {
        vehicleRepository.deleteById(id);
        return "redirect:/admin/vehicles";
    }

    // Прив'язати товар до авто
    @PostMapping("/admin/vehicles/{vehicleId}/products/add")
    public String addProduct(@PathVariable Long vehicleId,
                             @RequestParam Long productId) {
        vehicleRepository.findById(vehicleId).ifPresent(vehicle ->
                productRepository.findById(productId).ifPresent(product -> {
                    if (!product.getVehicles().contains(vehicle)) {
                        product.getVehicles().add(vehicle);
                        productRepository.save(product);
                    }
                })
        );
        return "redirect:/admin/vehicles";
    }

    // Відв'язати товар від авто
    @DeleteMapping("/admin/vehicles/{vehicleId}/products/remove")
    public String removeProduct(@PathVariable Long vehicleId,
                                @RequestParam Long productId) {
        vehicleRepository.findById(vehicleId).ifPresent(vehicle ->
                productRepository.findById(productId).ifPresent(product -> {
                    product.getVehicles().remove(vehicle);
                    productRepository.save(product);
                })
        );
        return "redirect:/admin/vehicles";
    }
}