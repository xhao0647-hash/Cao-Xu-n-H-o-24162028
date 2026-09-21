package vn.iotstar.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(RoleRepository roleRepository,
                            UserRepository userRepository,
                            CategoryRepository categoryRepository,
                            ProductRepository productRepository,
                            PasswordEncoder passwordEncoder) {
        return args -> {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").build()));
            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("USER").build()));

            if (userRepository.findByUsername("admin01").isEmpty()) {
                userRepository.save(User.builder()
                        .username("admin01")
                        .email("admin01@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("Quản trị viên")
                        .images("/images/avatar-admin.png")
                        .role(adminRole)
                        .enabled(true)
                        .build());
            }

            if (userRepository.findByUsername("user01").isEmpty()) {
                userRepository.save(User.builder()
                        .username("user01")
                        .email("user01@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("Nguyễn Hữu Trung")
                        .phone("0909123456")
                        .defaultAddress("123 Vo Van Ngan, Thu Duc, TP.HCM")
                        .images("/images/avatar-default.png")
                        .role(userRole)
                        .enabled(true)
                        .build());
            }

            if (categoryRepository.count() == 0) {
                Category ao = categoryRepository.save(Category.builder().name("Áo").build());
                Category quan = categoryRepository.save(Category.builder().name("Quần").build());
                Category giay = categoryRepository.save(Category.builder().name("Giày dép").build());
                Category phuKien = categoryRepository.save(Category.builder().name("Phụ kiện").build());

                productRepository.save(Product.builder()
                        .name("Áo thun basic cotton")
                        .description("Áo thun cotton 100%, form regular, thoáng mát, nhiều màu.")
                        .price(150000L)
                        .stock(50)
                        .imageUrl("/images/avatar-default.png")
                        .sizeOptions("S,M,L,XL")
                        .colorOptions("Đen,Trắng,Xám")
                        .category(ao)
                        .build());

                productRepository.save(Product.builder()
                        .name("Áo sơ mi công sở")
                        .description("Áo sơ mi vải kate không nhăn, phù hợp đi làm/đi học.")
                        .price(280000L)
                        .stock(30)
                        .imageUrl("/images/avatar-default.png")
                        .sizeOptions("S,M,L,XL")
                        .colorOptions("Trắng,Xanh nhạt")
                        .category(ao)
                        .build());

                productRepository.save(Product.builder()
                        .name("Quần jean slimfit")
                        .description("Quần jean co giãn nhẹ, form slimfit trẻ trung.")
                        .price(350000L)
                        .stock(40)
                        .imageUrl("/images/avatar-default.png")
                        .sizeOptions("29,30,31,32,33")
                        .colorOptions("Xanh đậm,Đen")
                        .category(quan)
                        .build());

                productRepository.save(Product.builder()
                        .name("Quần short kaki")
                        .description("Quần short kaki nam, chất liệu bền, thoải mái vận động.")
                        .price(200000L)
                        .stock(60)
                        .imageUrl("/images/avatar-default.png")
                        .sizeOptions("29,30,31,32")
                        .colorOptions("Be,Đen,Rêu")
                        .category(quan)
                        .build());

                productRepository.save(Product.builder()
                        .name("Giày sneaker trắng")
                        .description("Giày sneaker basic, dễ phối đồ, đế êm.")
                        .price(450000L)
                        .stock(25)
                        .imageUrl("/images/avatar-default.png")
                        .sizeOptions("39,40,41,42,43")
                        .colorOptions("Trắng,Đen")
                        .category(giay)
                        .build());

                productRepository.save(Product.builder()
                        .name("Dép sandal quai ngang")
                        .description("Sandal quai ngang, chất liệu êm chân, chống trơn trượt.")
                        .price(180000L)
                        .stock(35)
                        .imageUrl("/images/avatar-default.png")
                        .sizeOptions("39,40,41,42")
                        .colorOptions("Đen,Nâu")
                        .category(giay)
                        .build());

                productRepository.save(Product.builder()
                        .name("Nón lưỡi trai")
                        .description("Nón lưỡi trai unisex, điều chỉnh size dễ dàng.")
                        .price(120000L)
                        .stock(70)
                        .imageUrl("/images/avatar-default.png")
                        .sizeOptions("Freesize")
                        .colorOptions("Đen,Trắng,Be")
                        .category(phuKien)
                        .build());

                productRepository.save(Product.builder()
                        .name("Thắt lưng da nam")
                        .description("Thắt lưng da thật, khóa kim loại chống gỉ.")
                        .price(220000L)
                        .stock(20)
                        .imageUrl("/images/avatar-default.png")
                        .sizeOptions("Freesize")
                        .colorOptions("Đen,Nâu")
                        .category(phuKien)
                        .build());
            }
        };
    }
}
