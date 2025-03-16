package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.Order;
import com.example.ecommerce.gearvn.model.User;
import com.example.ecommerce.gearvn.repository.OrderRepository;
import com.example.ecommerce.gearvn.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private final OrderRepository orderRepository;
    @Autowired
    private JavaMailSender mailSender;
    @GetMapping("/login")
    public String login() {
        return "users/login";
    }

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User());
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, @NotNull BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "users/register";
        }

        if (userService.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
            return "users/register";
        }

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("errorMessage", "Email đã tồn tại.");
            return "users/register";
        }

        if (userService.findByPhone(user.getPhone()).isPresent()) {
            model.addAttribute("errorMessage", "Số điện thoại đã tồn tại.");
            return "users/register";
        }

        userService.save(user);
        userService.setDefaultRole(user.getUsername());
        model.addAttribute("successMessage", "Đăng ký thành công!");
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String showUserProfile(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        model.addAttribute("user", user);
        return "users/profile";
    }

    @GetMapping("/order-history")
    public String viewOrderHistory(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getCurrentUser(userDetails);
        if (user == null) {
            return "redirect:/login";
        }
        List<Order> orders = orderRepository.findByUser(user);
        model.addAttribute("orders", orders);
        return "users/order_history";
    }

    @PostMapping("/profile")
    public String updateUserProfile(@Valid @ModelAttribute("user") User user, @NotNull BindingResult bindingResult, Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "users/profile";
        }

        String username = principal.getName();
        User existingUser = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.getUsername().equals(existingUser.getUsername()) && userService.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
            return "users/profile";
        }

        if (!user.getEmail().equals(existingUser.getEmail()) && userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("errorMessage", "Email đã tồn tại.");
            return "users/profile";
        }

        if (!user.getPhone().equals(existingUser.getPhone()) && userService.findByPhone(user.getPhone()).isPresent()) {
            model.addAttribute("errorMessage", "Số điện thoại đã tồn tại.");
            return "users/profile";
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        userService.save(existingUser);
        model.addAttribute("successMessage", "Cập nhật thông tin thành công!");
        return "users/profile";
    }
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "users/forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        String token = UUID.randomUUID().toString();
        try {
            userService.createPasswordResetToken(email, token);
            model.addAttribute("successMessage", "Chúng tôi đã gửi một liên kết đặt lại mật khẩu đến email của bạn.");
        } catch (UsernameNotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        return "users/forgot_password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "users/reset_password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Mật khẩu không khớp.");
            model.addAttribute("token", token);
            return "users/reset_password";
        }
        try {
            userService.resetPassword(token, password);
            model.addAttribute("successMessage", "Mật khẩu đã được đặt lại thành công.");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("token", token);
        }
        return "users/login";
    }

}
