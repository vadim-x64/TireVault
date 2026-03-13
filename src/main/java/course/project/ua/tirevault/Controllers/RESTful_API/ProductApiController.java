package course.project.ua.tirevault.Controllers.RESTful_API;

import course.project.ua.tirevault.Services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Автотовари", description = "Каталог товарів та категорій")
public class ProductApiController {

    @Autowired private ProductService productService;

    @GetMapping
    @Operation(summary = "Отримати всі товари")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отримати товар за ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categories")
    @Operation(summary = "Отримати всі категорії товарів")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(productService.getAllCategories());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Редагувати товар")
    public ResponseEntity<?> editProduct(@PathVariable Long id,
                                         @RequestParam Long categoryId,
                                         @RequestParam String name,
                                         @RequestParam(required = false) String description,
                                         @RequestParam BigDecimal price,
                                         @RequestParam(defaultValue = "0") Integer quantity,
                                         @RequestParam(required = false) String imageUrl) {
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

    @DeleteMapping("/{id}")
    @Operation(summary = "Видалити товар")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Видалити категорію")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        productService.deleteCategoryById(id);
        return ResponseEntity.ok().build();
    }
}