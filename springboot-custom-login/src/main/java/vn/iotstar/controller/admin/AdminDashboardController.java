package vn.iotstar.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.iotstar.entity.OrderStatus;
import vn.iotstar.repository.OrderRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        model.addAttribute("pendingCount", orderRepository.countByStatus(OrderStatus.PENDING));
        model.addAttribute("ordersToday", orderRepository.countByCreatedAtAfter(startOfToday));
        model.addAttribute("revenueToday", orderRepository.sumRevenueSince(startOfToday));
        model.addAttribute("revenueMonth", orderRepository.sumRevenueSince(startOfMonth));
        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalOrders", orderRepository.count());
        return "admin/dashboard";
    }
}
