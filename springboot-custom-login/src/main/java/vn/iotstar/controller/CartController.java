package vn.iotstar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.User;
import vn.iotstar.repository.CartItemRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.security.CustomUserDetails;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private User currentUser(CustomUserDetails principal) {
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy user hiện tại"));
    }

    @GetMapping("/cart")
    public String viewCart(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        User user = currentUser(principal);
        List<CartItem> items = cartItemRepository.findByUserOrderByIdDesc(user);

        long total = items.stream()
                .mapToLong(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        return "cart/cart";
    }

    @PostMapping("/cart/add")
    public String add(@AuthenticationPrincipal CustomUserDetails principal,
                       @RequestParam Long productId,
                       @RequestParam(defaultValue = "1") Integer quantity,
                       @RequestParam(required = false) String size,
                       @RequestParam(required = false) String color,
                       RedirectAttributes redirectAttributes) {

        User user = currentUser(principal);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

        CartItem item = cartItemRepository
                .findByUserAndProductIdAndSizeAndColor(user, productId, size, color)
                .orElse(null);

        if (item == null) {
            item = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(quantity)
                    .size(size)
                    .color(color)
                    .build();
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
        cartItemRepository.save(item);

        redirectAttributes.addFlashAttribute("message", "Đã thêm \"" + product.getName() + "\" vào giỏ hàng.");
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String update(@RequestParam Long cartItemId, @RequestParam Integer quantity) {
        cartItemRepository.findById(cartItemId).ifPresent(item -> {
            if (quantity <= 0) {
                cartItemRepository.delete(item);
            } else {
                item.setQuantity(quantity);
                cartItemRepository.save(item);
            }
        });
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String remove(@RequestParam Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
        return "redirect:/cart";
    }
}
