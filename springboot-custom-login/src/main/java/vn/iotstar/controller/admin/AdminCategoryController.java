package vn.iotstar.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.entity.Category;
import vn.iotstar.repository.CategoryRepository;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("newCategory", new Category());
        return "admin/categories/list";
    }

    @PostMapping
    public String create(@RequestParam String name, RedirectAttributes redirectAttributes) {
        categoryRepository.save(Category.builder().name(name).build());
        redirectAttributes.addFlashAttribute("message", "Đã thêm danh mục.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @RequestParam String name, RedirectAttributes redirectAttributes) {
        categoryRepository.findById(id).ifPresent(c -> {
            c.setName(name);
            categoryRepository.save(c);
        });
        redirectAttributes.addFlashAttribute("message", "Đã cập nhật danh mục.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Đã xoá danh mục.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xoá: danh mục đang có sản phẩm.");
        }
        return "redirect:/admin/categories";
    }
}
