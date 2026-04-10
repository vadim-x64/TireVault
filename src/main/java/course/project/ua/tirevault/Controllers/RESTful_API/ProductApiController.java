package course.project.ua.tirevault.Controllers.RESTful_API;

import course.project.ua.tirevault.Configuration.ApiRole;
import course.project.ua.tirevault.Entities.Enums.UserRole;
import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Автотовари", description = "Каталог товарів та категорій")
public class ProductApiController {
    @Autowired
    private ProductService productService;

    @ApiRole(UserRole.USER)
    @GetMapping
    @Operation(summary = "Отримати всі товари")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @ApiRole(UserRole.USER)
    @GetMapping("/{id}")
    @Operation(summary = "Отримати товар за ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return productService.getProductById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @ApiRole(UserRole.USER)
    @GetMapping("/categories")
    @Operation(summary = "Отримати всі категорії товарів")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(productService.getAllCategories());
    }

    @ApiRole(UserRole.ADMIN)
    @PutMapping("/{id}")
    @Operation(summary = "Редагувати товар")
    public ResponseEntity<?> editProduct(@PathVariable Long id, @RequestParam Long categoryId, @RequestParam String name, @RequestParam(required = false) String description, @RequestParam BigDecimal price, @RequestParam(defaultValue = "0") Integer quantity, @RequestParam(required = false) String imageUrl, HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).build();
        return productService.getProductById(id).map(p -> {
            productService.getCategoryById(categoryId).ifPresent(p::setCategory);
            p.setName(name);
            p.setDescription(description);
            p.setPrice(price);
            p.setQuantity(quantity);
            p.setAvailability(quantity > 0);
            p.setImageUrl(imageUrl);
            productService.saveProduct(p);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @ApiRole(UserRole.ADMIN)
    @DeleteMapping("/{id}")
    @Operation(summary = "Видалити товар")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).build();
        productService.deleteProductById(id);
        return ResponseEntity.ok().build();
    }

    @ApiRole(UserRole.ADMIN)
    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Видалити категорію")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).build();
        productService.deleteCategoryById(id);
        return ResponseEntity.ok().build();
    }

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        return user != null && user.getRole() == UserRole.ADMIN;
    }
}