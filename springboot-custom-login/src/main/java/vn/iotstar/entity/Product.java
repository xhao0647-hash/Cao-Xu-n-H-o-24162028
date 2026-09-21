package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200, columnDefinition = "nvarchar(200)")
    private String name;

    @Column(length = 2000, columnDefinition = "nvarchar(2000)")
    private String description;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Column(length = 500)
    private String imageUrl;

    // Danh sách size/mau cach nhau boi dau phay, vi du: "S,M,L,XL"
    @Column(name = "size_options", length = 200)
    private String sizeOptions;

    @Column(name = "color_options", length = 200, columnDefinition = "nvarchar(200)")
    private String colorOptions;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
