package vn.iotstar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.iotstar.entity.Product;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.specification.ProductSpecifications;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private static final int PAGE_SIZE = 8;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping("/products")
    public String list(@RequestParam(required = false) String categoryId,
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String minPrice,
                        @RequestParam(required = false) String maxPrice,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {

        Long categoryIdVal = parseLong(categoryId);
        Long minPriceVal = parseLong(minPrice);
        Long maxPriceVal = parseLong(maxPrice);

        Specification<Product> spec = Specification.allOf(
                ProductSpecifications.activeOnly(),
                ProductSpecifications.categoryIdEquals(categoryIdVal),
                ProductSpecifications.nameContains(keyword),
                ProductSpecifications.priceGte(minPriceVal),
                ProductSpecifications.priceLte(maxPriceVal)
        );

        Page<Product> products = productRepository.findAll(
                spec, PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id")));

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("categoryId", categoryIdVal);
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPriceVal);
        model.addAttribute("maxPrice", maxPriceVal);
        return "products/list";
    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        model.addAttribute("product", product);
        return "products/detail";
    }

    private Long parseLong(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
