package vn.iotstar.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.service.FileStorageService;
import vn.iotstar.specification.ProductSpecifications;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        Specification<Product> spec = Specification.allOf(
                ProductSpecifications.nameContains(keyword)
        );
        Page<Product> products = productRepository.findAll(
                spec, PageRequest.of(page, 8, Sort.by(Sort.Direction.DESC, "id")));

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "admin/products/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/products/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/products/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                        @RequestParam String name,
                        @RequestParam(required = false) String description,
                        @RequestParam Long price,
                        @RequestParam Integer stock,
                        @RequestParam Long categoryId,
                        @RequestParam(required = false) String sizeOptions,
                        @RequestParam(required = false) String colorOptions,
                        @RequestParam(defaultValue = "true") boolean active,
                        @RequestParam(required = false) MultipartFile imageFile,
                        RedirectAttributes redirectAttributes) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không hợp lệ"));

        Product product;
        if (id != null) {
            product = productRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        } else {
            product = new Product();
        }

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);
        product.setSizeOptions(sizeOptions);
        product.setColorOptions(colorOptions);
        product.setActive(active);

        if (imageFile != null && !imageFile.isEmpty()) {
            String url = fileStorageService.store(imageFile, "products");
            product.setImageUrl(url);
        } else if (product.getImageUrl() == null) {
            product.setImageUrl("/images/avatar-default.png");
        }

        productRepository.save(product);
        redirectAttributes.addFlashAttribute("message", "Đã lưu sản phẩm \"" + name + "\".");
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productRepository.findById(id).ifPresent(p -> {
            p.setActive(false);
            productRepository.save(p);
        });
        redirectAttributes.addFlashAttribute("message", "Đã ẩn sản phẩm khỏi shop.");
        return "redirect:/admin/products";
    }
}
