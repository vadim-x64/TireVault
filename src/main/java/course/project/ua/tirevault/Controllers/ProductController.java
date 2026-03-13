package course.project.ua.tirevault.Controllers;

import course.project.ua.tirevault.Entities.Models.Review;
import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Services.ProductService;
import course.project.ua.tirevault.Services.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

@ResponseBody
@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

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
    public String productDetail(@PathVariable Long id, Model model, HttpSession session) {
        var product = productService.getProductById(id);
        if (product.isEmpty()) return "redirect:/products";

        User user = (User) session.getAttribute("loggedUser");
        List<Review> reviews =
                reviewService.getTopLevelReviews(
                        course.project.ua.tirevault.Entities.Enums.ReviewTargetType.PRODUCT, id);
        java.util.Set<Long> likedIds = reviewService.getAllLikedReviewIds(user, reviews);

        model.addAttribute("product", product.get());
        model.addAttribute("reviews", reviews);
        model.addAttribute("likedReviewIds", likedIds);
        model.addAttribute("reviewTargetType", "PRODUCT");
        model.addAttribute("reviewTargetId", id);
        model.addAttribute("page", "productdetail");
        return "index";
    }
}