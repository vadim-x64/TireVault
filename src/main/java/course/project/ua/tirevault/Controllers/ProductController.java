package course.project.ua.tirevault.Controllers;

import course.project.ua.tirevault.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("selectedCategoryId", null);
        model.addAttribute("page", "products");
        return "index";
    }

    @GetMapping("/products/category/{id}")
    public String productsByCategory(@PathVariable Long id, Model model) {
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("products", productService.getProductsByCategory(id));
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