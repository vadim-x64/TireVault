package course.project.ua.tirevault.Controllers;

import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Entities.Models.Cart;
import course.project.ua.tirevault.Entities.Models.CartItem;
import course.project.ua.tirevault.Services.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    private static final List<String> STATIONS = List.of(
            "TireVault — вул. Хрещатик, 1, Київ",
            "TireVault — вул. Незалежності, 5, Харків",
            "TireVault — вул. Шевченка, 10, Львів",
            "TireVault — вул. Соборна, 3, Одеса"
    );

    @GetMapping("/cart")
    public String cartPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            model.addAttribute("page", "cart");
            model.addAttribute("stations", STATIONS);
            return "index";
        }
        Cart cart = cartService.getOrCreateCart(user);
        model.addAttribute("cart", cart);
        model.addAttribute("stations", STATIONS);
        model.addAttribute("page", "cart");
        return "index";
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null)
            return ResponseEntity.ok(Map.of("success", false, "message", "Потрібна авторизація"));

        Cart cart = cartService.addToCart(user, productId, quantity);
        int count = cart.getItems().stream().mapToInt(i -> i.getQuantity()).sum();

        return ResponseEntity.ok(Map.of("success", true, "cartCount", count));
    }

    @PostMapping("/cart/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateItem(
            @RequestParam Long itemId,
            @RequestParam int quantity,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null)
            return ResponseEntity.status(401).body(Map.of("success", false));

        Cart cart = cartService.updateQuantity(user, itemId, quantity);
        CartItem updated = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId)).findFirst().orElse(null);

        int count = cart.getItems().stream().mapToInt(i -> i.getQuantity()).sum();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "subtotal", updated != null ? updated.getSubtotal() : 0,
                "total", cart.getTotal(),
                "cartCount", count,
                "removed", updated == null
        ));
    }

    @PostMapping("/cart/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeItem(
            @RequestParam Long itemId,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null)
            return ResponseEntity.status(401).body(Map.of("success", false));

        Cart cart = cartService.removeItem(user, itemId);
        int count = cart.getItems().stream().mapToInt(i -> i.getQuantity()).sum();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "total", cart.getTotal(),
                "cartCount", count,
                "empty", cart.getItems().isEmpty()
        ));
    }
}