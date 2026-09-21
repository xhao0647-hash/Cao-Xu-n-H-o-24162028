package vn.iotstar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.iotstar.entity.*;
import vn.iotstar.repository.CartItemRepository;
import vn.iotstar.repository.OrderRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.security.CustomUserDetails;

import java.security.SecureRandom;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CheckoutController {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ0123456789";
    private final SecureRandom random = new SecureRandom();

    private User currentUser(CustomUserDetails principal) {
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy user hiện tại"));
    }

    @GetMapping("/checkout")
    public String checkoutForm(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        User user = currentUser(principal);
        List<CartItem> items = cartItemRepository.findByUserOrderByIdDesc(user);

        if (items.isEmpty()) {
            return "redirect:/cart";
        }

        long total = items.stream()
                .mapToLong(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("user", user);
        return "checkout/checkout";
    }

    @PostMapping("/checkout/place")
    public String place(@AuthenticationPrincipal CustomUserDetails principal,
                         @RequestParam String receiverName,
                         @RequestParam String phone,
                         @RequestParam String address,
                         @RequestParam PaymentMethod paymentMethod,
                         @RequestParam(required = false) String note,
                         Model model) {

        User user = currentUser(principal);
        List<CartItem> items = cartItemRepository.findByUserOrderByIdDesc(user);
        if (items.isEmpty()) {
            return "redirect:/cart";
        }

        long total = items.stream()
                .mapToLong(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();

        Order order = Order.builder()
                .orderCode(generateOrderCode())
                .user(user)
                .receiverName(receiverName)
                .phone(phone)
                .address(address)
                .paymentMethod(paymentMethod)
                .status(OrderStatus.PENDING)
                .totalAmount(total)
                .note(note)
                .build();

        for (CartItem ci : items) {
            Product product = ci.getProduct();

            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(ci.getQuantity())
                    .size(ci.getSize())
                    .color(ci.getColor())
                    .build();
            order.getItems().add(oi);

            int remain = Math.max(0, product.getStock() - ci.getQuantity());
            product.setStock(remain);
            productRepository.save(product);
        }

        orderRepository.save(order);
        cartItemRepository.deleteByUser(user);

        model.addAttribute("order", order);
        return "checkout/success";
    }

    private String generateOrderCode() {
        StringBuilder sb = new StringBuilder("DH");
        for (int i = 0; i < 8; i++) {
            sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }
}
