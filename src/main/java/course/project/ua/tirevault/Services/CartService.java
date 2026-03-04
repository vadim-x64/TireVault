package course.project.ua.tirevault.Services;

import course.project.ua.tirevault.Entities.Models.User;
import course.project.ua.tirevault.Entities.Models.Cart;
import course.project.ua.tirevault.Entities.Models.CartItem;
import course.project.ua.tirevault.Entities.Models.Product;
import course.project.ua.tirevault.Repositories.ICartItemRepository;
import course.project.ua.tirevault.Repositories.ICartRepository;
import course.project.ua.tirevault.Repositories.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private ICartRepository cartRepository;

    @Autowired
    private ICartItemRepository cartItemRepository;

    @Autowired
    private IProductRepository productRepository;

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setTotal(BigDecimal.ZERO);
            return cartRepository.save(cart);
        });
    }

    @Transactional
    public Cart addToCart(User user, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не знайдено"));

        Cart cart = getOrCreateCart(user);

        Optional<CartItem> existing =
                cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return recalculate(cart);
    }

    @Transactional
    public Cart updateQuantity(User user, Long itemId, int quantity) {
        Cart cart = getOrCreateCart(user);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Позицію не знайдено"));

        if (!item.getCart().getId().equals(cart.getId()))
            throw new RuntimeException("Доступ заборонено");

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return recalculate(cart);
    }

    @Transactional
    public Cart removeItem(User user, Long itemId) {
        Cart cart = getOrCreateCart(user);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Позицію не знайдено"));

        if (!item.getCart().getId().equals(cart.getId()))
            throw new RuntimeException("Доступ заборонено");

        cartItemRepository.delete(item);
        return recalculate(cart);
    }

    public int getCartItemCount(User user) {
        return cartRepository.findByUserId(user.getId())
                .map(cart -> cart.getItems().stream()
                        .mapToInt(CartItem::getQuantity).sum())
                .orElse(0);
    }

    private Cart recalculate(Cart cart) {
        // Перезавантажуємо щоб отримати актуальні items
        cart = cartRepository.findById(cart.getId()).orElse(cart);
        BigDecimal total = cart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotal(total);
        return cartRepository.save(cart);
    }
}