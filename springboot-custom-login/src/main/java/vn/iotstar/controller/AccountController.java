package vn.iotstar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.User;
import vn.iotstar.repository.OrderRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.security.CustomUserDetails;
import vn.iotstar.service.FileStorageService;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final FileStorageService fileStorageService;

    private User currentUser(CustomUserDetails principal) {
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy user hiện tại"));
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        model.addAttribute("user", currentUser(principal));
        return "account/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails principal,
                                 @RequestParam String fullName,
                                 @RequestParam(required = false) String phone,
                                 @RequestParam(required = false) String defaultAddress,
                                 @RequestParam(required = false) MultipartFile avatar,
                                 RedirectAttributes redirectAttributes) {

        User user = currentUser(principal);
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setDefaultAddress(defaultAddress);

        if (avatar != null && !avatar.isEmpty()) {
            String url = fileStorageService.store(avatar, "avatars");
            user.setImages(url);
        }
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("message", "Cập nhật hồ sơ thành công. Đăng nhập lại để thấy avatar mới trên header.");
        return "redirect:/account/profile";
    }

    @GetMapping("/orders")
    public String orders(@AuthenticationPrincipal CustomUserDetails principal,
                          @RequestParam(defaultValue = "0") int page,
                          Model model) {
        User user = currentUser(principal);
        Page<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user, PageRequest.of(page, 5));
        model.addAttribute("orders", orders);
        return "account/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@AuthenticationPrincipal CustomUserDetails principal,
                               @PathVariable Long id, Model model) {
        User user = currentUser(principal);
        Order order = orderRepository.findById(id)
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        model.addAttribute("order", order);
        return "account/order-detail";
    }
}
