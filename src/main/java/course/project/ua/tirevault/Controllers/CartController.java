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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@ResponseBody
@Controller
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/cart")
    public String cartPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            model.addAttribute("page", "cart");
            return "index";
        }
        Cart cart = cartService.getOrCreateCart(user);
        model.addAttribute("cart", cart);
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

        try {
            Cart cart = cartService.addToCart(user, productId, quantity);
            int count = cart.getItems().stream().mapToInt(i -> i.getQuantity()).sum();
            return ResponseEntity.ok(Map.of("success", true, "cartCount", count));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/cart/checkout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkout(
            @RequestParam(required = false) String station,
            @RequestParam(required = false) String payMethod,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null)
            return ResponseEntity.status(401).body(Map.of("success", false));

        try {
            cartService.checkout(user, station, payMethod);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/cart/update")
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

    @DeleteMapping("/cart/remove")
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