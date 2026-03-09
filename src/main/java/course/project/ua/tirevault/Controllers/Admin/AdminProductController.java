package course.project.ua.tirevault.Controllers.Admin;

import course.project.ua.tirevault.Entities.Models.*;
import course.project.ua.tirevault.Repositories.ICartItemRepository;
import course.project.ua.tirevault.Repositories.ICartRepository;
import course.project.ua.tirevault.Repositories.IOrderItemRepository;
import course.project.ua.tirevault.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class AdminProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ICartItemRepository cartItemRepository;

    @Autowired
    private ICartRepository cartRepository;

    @Autowired
    private IOrderItemRepository orderItemRepository;

    @GetMapping("/admin/products")
    public String productsPage(Model model) {
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("page", "admin/adminproducts");
        return "index";
    }

    @PostMapping("/admin/products/categories/add")
    public String addCategory(@RequestParam String name) {
        ProductCategory cat = new ProductCategory();
        cat.setName(name);
        productService.saveCategory(cat);
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/categories/{id}/edit")
    public String editCategory(@PathVariable Long id, @RequestParam String name) {
        productService.getCategoryById(id).ifPresent(cat -> {
            cat.setName(name);
            productService.saveCategory(cat);
        });
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/categories/{id}/delete")
    @Transactional
    public String deleteCategory(@PathVariable Long id) {
        List<Product> products = productService.getProductsByCategory(id);
        for (Product product : products) {
            removeCartItemsForProduct(product.getId());
            productService.deleteProductById(product.getId());
        }
        productService.deleteCategoryById(id);
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/add")
    public String addProduct(@RequestParam Long categoryId,
                             @RequestParam String name,
                             @RequestParam(required = false) String article,
                             @RequestParam(required = false) String description,
                             @RequestParam BigDecimal price,
                             @RequestParam(defaultValue = "0") Integer quantity,
                             @RequestParam(required = false) String imageUrl) {
        productService.getCategoryById(categoryId).ifPresent(cat -> {
            Product p = new Product();
            p.setCategory(cat);
            p.setName(name);
            p.setArticle(article != null ? article : String.valueOf(System.currentTimeMillis()));
            p.setDescription(description);
            p.setPrice(price);
            p.setQuantity(quantity);
            p.setAvailability(quantity > 0);
            p.setImageUrl(imageUrl);
            productService.saveProduct(p);
        });
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/{id}/edit")
    public String editProduct(@PathVariable Long id,
                              @RequestParam Long categoryId,
                              @RequestParam String name,
                              @RequestParam(required = false) String article,
                              @RequestParam(required = false) String description,
                              @RequestParam BigDecimal price,
                              @RequestParam(defaultValue = "0") Integer quantity,
                              @RequestParam(required = false) String imageUrl) {
        productService.getProductById(id).ifPresent(p -> {
            productService.getCategoryById(categoryId).ifPresent(p::setCategory);
            p.setName(name);
            if (article != null && !article.isBlank()) p.setArticle(article);
            p.setDescription(description);
            p.setPrice(price);
            p.setQuantity(quantity);
            p.setAvailability(quantity > 0);
            p.setImageUrl(imageUrl);
            productService.saveProduct(p);
        });
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/{id}/delete")
    @Transactional
    public String deleteProduct(@PathVariable Long id) {
        removeCartItemsForProduct(id);
        productService.deleteProductById(id);
        return "redirect:/admin/products";
    }

    private void removeCartItemsForProduct(Long productId) {
        orderItemRepository.deleteByProductId(productId); // <-- додати це

        List<CartItem> items = cartItemRepository.findByProductId(productId);
        for (CartItem item : items) {
            Cart cart = item.getCart();
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
            BigDecimal total = cart.getItems().stream()
                    .map(CartItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cart.setTotal(total);
            cartRepository.save(cart);
        }
    }
}