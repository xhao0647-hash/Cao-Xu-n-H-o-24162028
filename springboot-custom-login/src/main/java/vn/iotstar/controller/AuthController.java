package vn.iotstar.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;

import java.security.SecureRandom;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String RANDOM_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    private final SecureRandom random = new SecureRandom();

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    // ===== Dang ky =====

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                            @RequestParam String email,
                            @RequestParam String password,
                            @RequestParam String confirmPassword,
                            @RequestParam String fullName,
                            @RequestParam(required = false) String phone,
                            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu nhập lại không khớp.");
            return backToRegisterForm(model, username, email, fullName, phone);
        }
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username đã tồn tại, vui lòng chọn username khác.");
            return backToRegisterForm(model, username, email, fullName, phone);
        }
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email đã được sử dụng.");
            return backToRegisterForm(model, username, email, fullName, phone);
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("USER").build()));

        userRepository.save(User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .phone(phone)
                .images("/images/avatar-default.png")
                .role(userRole)
                .enabled(true)
                .build());

        return "redirect:/login?registered=true";
    }

    private String backToRegisterForm(Model model, String username, String email, String fullName, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPhone(phone);
        model.addAttribute("user", user);
        return "auth/register";
    }

    // ===== Quen mat khau (ban rut gon: khong dung mail server that,
    // sinh mat khau tam thoi va hien thi truc tiep cho nguoi dung) =====

    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String username,
                                  @RequestParam String email,
                                  Model model) {
        var userOpt = userRepository.findByUsername(username)
                .filter(u -> u.getEmail().equalsIgnoreCase(email));

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy tài khoản khớp username + email này.");
            return "auth/forgot-password";
        }

        String tempPassword = generateTempPassword();
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        model.addAttribute("tempPassword", tempPassword);
        model.addAttribute("username", user.getUsername());
        return "auth/reset-result";
    }

    private String generateTempPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(RANDOM_CHARS.charAt(random.nextInt(RANDOM_CHARS.length())));
        }
        return sb.toString();
    }
}
