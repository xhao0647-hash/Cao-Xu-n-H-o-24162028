package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.OrderStatus;
import vn.iotstar.entity.User;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByStatus(OrderStatus status);

    long countByCreatedAtAfter(LocalDateTime after);

    @org.springframework.data.jpa.repository.Query(
            "select coalesce(sum(o.totalAmount), 0) from Order o where o.status <> vn.iotstar.entity.OrderStatus.CANCELLED and o.createdAt >= :from")
    long sumRevenueSince(LocalDateTime from);
}
