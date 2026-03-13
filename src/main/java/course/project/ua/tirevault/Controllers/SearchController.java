package course.project.ua.tirevault.Controllers;

import course.project.ua.tirevault.Entities.Models.Product;
import course.project.ua.tirevault.Entities.Models.WorkService;
import course.project.ua.tirevault.Repositories.IProductRepository;
import course.project.ua.tirevault.Repositories.IWorkServiceRepository;
import course.project.ua.tirevault.Services.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class SearchController {
    private final SearchService searchService;
    private final IProductRepository productRepository;
    private final IWorkServiceRepository serviceRepository;

    public SearchController(SearchService searchService, IProductRepository productRepository, IWorkServiceRepository serviceRepository) {
        this.searchService = searchService;
        this.productRepository = productRepository;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "q", defaultValue = "") String query, Model model) {
        String q = query.trim();
        List<Product> products = q.isEmpty() ? List.of() : productRepository.searchByKeyword(q);
        List<WorkService> services = q.isEmpty() ? List.of() : serviceRepository.searchByKeyword(q);
        int totalCount = products.size() + services.size();
        model.addAttribute("query", q);
        model.addAttribute("products", products);
        model.addAttribute("services", services);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("page", "search");
        return "index";
    }
}