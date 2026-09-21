package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.User;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserOrderByIdDesc(User user);

    Optional<CartItem> findByUserAndProductIdAndSizeAndColor(User user, Long productId, String size, String color);

    long countByUser(User user);

    void deleteByUser(User user);
}
