package course.project.ua.tirevault.Controllers;

import course.project.ua.tirevault.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String availability,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String vmodel,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String modification,
            Model model) {

        // Порожній рядок → null, щоб JPQL коректно обробляв IS NULL
        if (brand        != null && brand.isBlank())        brand        = null;
        if (vmodel       != null && vmodel.isBlank())       vmodel       = null;
        if (modification != null && modification.isBlank()) modification = null;

        Boolean availBool = null;
        if ("true".equals(availability))  availBool = true;
        else if ("false".equals(availability)) availBool = false;

        model.addAttribute("categories",       productService.getAllCategories());
        model.addAttribute("vehicles",         productService.getAllVehicles());
        model.addAttribute("products",         productService.getFilteredProducts(
                categoryId, minPrice, maxPrice, availBool, brand, vmodel, year, modification));
        model.addAttribute("selectedCategoryId",  categoryId);
        model.addAttribute("filterMinPrice",      minPrice);
        model.addAttribute("filterMaxPrice",      maxPrice);
        model.addAttribute("filterAvailability",  availability);
        model.addAttribute("filterBrand",         brand);
        model.addAttribute("filterVmodel",        vmodel);
        model.addAttribute("filterYear",          year);
        model.addAttribute("filterModification",  modification);
        model.addAttribute("page", "products");
        return "index";
    }

    // Маршрут збережено для зворотної сумісності (наприклад, якщо є зовнішні посилання)
    @GetMapping("/products/category/{id}")
    public String productsByCategory(@PathVariable Long id, Model model) {
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("vehicles", productService.getAllVehicles());
        model.addAttribute("products", productService.getFilteredProducts(
                id, null, null, null, null, null, null, null));
        model.addAttribute("selectedCategoryId", id);
        model.addAttribute("page", "products");
        return "index";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        var product = productService.getProductById(id);
        if (product.isEmpty()) return "redirect:/products";
        model.addAttribute("product", product.get());
        model.addAttribute("page", "productdetail");
        return "index";
    }
}