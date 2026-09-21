package vn.iotstar.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.OrderStatus;
import vn.iotstar.repository.OrderRepository;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepository;

    @GetMapping
    public String list(@RequestParam(required = false) String status,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        OrderStatus statusEnum = (status == null || status.isBlank()) ? null : OrderStatus.valueOf(status);

        Page<Order> orders = statusEnum != null
                ? orderRepository.findByStatusOrderByCreatedAtDesc(statusEnum, PageRequest.of(page, 10))
                : orderRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, 10));

        model.addAttribute("orders", orders);
        model.addAttribute("status", statusEnum);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        model.addAttribute("order", order);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/orders/detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                                @RequestParam OrderStatus status,
                                RedirectAttributes redirectAttributes) {
        orderRepository.findById(id).ifPresent(o -> {
            o.setStatus(status);
            orderRepository.save(o);
        });
        redirectAttributes.addFlashAttribute("message", "Đã cập nhật trạng thái đơn hàng.");
        return "redirect:/admin/orders/" + id;
    }
}
