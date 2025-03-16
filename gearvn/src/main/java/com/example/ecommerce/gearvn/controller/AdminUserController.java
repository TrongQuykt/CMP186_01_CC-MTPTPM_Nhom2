package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.User;
import com.example.ecommerce.gearvn.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
public class AdminUserController {
    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "admin/adminusers/login";
    }

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User());
        return "admin/adminusers/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, @NotNull BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "admin/adminusers/register";
        }
        userService.save(user);
        userService.setDefaultRole(user.getUsername());
        return "redirect:admin/adminusers/login";
    }
    @GetMapping("/listusers")
    public String listUsers(Model model) {
        List<User> normalUsers = userService.findUsersByRole("USER");
        model.addAttribute("normalUsers", normalUsers);
        return "admin/adminusers/listusers";
    }
    @GetMapping("/adminuserslist")
    public String listAdminUsers(Model model) {
        Set<String> roles = Set.of("ADMIN", "STAFF");
        List<User> adminUsers = userService.findUsersByRoles(roles);
        model.addAttribute("adminUsers", adminUsers);
        return "admin/adminusers/adminusers";
    }

    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/adminusers/create";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("user") User user, @NotNull BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errorMessage", "Đã có lỗi xảy ra trong quá trình tạo tài khoản.");
            return "admin/adminusers/create";
        }

        if (userService.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
            return "admin/adminusers/create";
        }

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("errorMessage", "Email đã tồn tại.");
            return "admin/adminusers/create";
        }

        userService.save(user);
        userService.setDefaultRole(user.getUsername());
        model.addAttribute("successMessage", "Tạo tài khoản thành công!");
        return "redirect:/admin/listusers";
    }


    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            model.addAttribute("errorMessage", "User not found");
            return "admin/adminusers/edit";
        }
        model.addAttribute("user", user);
        return "admin/adminusers/edit";
    }



    @PostMapping("/edit")
    public String editUser(@Valid @ModelAttribute("user") User user, @NotNull BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errorMessage", "Đã có lỗi xảy ra trong quá trình chỉnh sửa tài khoản.");
            return "admin/adminusers/edit";
        }

        if (user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getPhone() == null || user.getPhone().isEmpty()) {
            model.addAttribute("errorMessage", "Không được để trống bất kỳ ô thông tin nào.");
            return "admin/adminusers/edit";
        }

        User existingUser = userService.findById(user.getId());
        if (existingUser == null) {
            model.addAttribute("errorMessage", "User not found");
            return "admin/adminusers/edit";
        }

        // Cập nhật các thông tin cần thiết từ người dùng hiện tại
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());

        // Nếu mật khẩu mới được cung cấp, cập nhật mật khẩu
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        }

        userService.save(existingUser);
        model.addAttribute("successMessage", "Chỉnh sửa tài khoản thành công!");
        return "redirect:/admin/listusers";
    }


    @GetMapping("/createadmin")
    public String createAdminForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", userService.getAllRoles()); // Thêm các vai trò vào model
        return "admin/adminusers/createadmin";
    }

    @PostMapping("/createadmin")
    public String createAdmin(@Valid @ModelAttribute("user") User user, @NotNull BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errorMessage", "Đã có lỗi xảy ra trong quá trình tạo tài khoản.");
            return "admin/adminusers/createadmin";
        }

        if (userService.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
            return "admin/adminusers/createadmin";
        }

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("errorMessage", "Email đã tồn tại.");
            return "admin/adminusers/createadmin";
        }

        userService.save(user);
        userService.setRole(user.getUsername(), user.getRoles().iterator().next().getName());
        model.addAttribute("successMessage", "Tạo tài khoản quản lý thành công!");
        return "redirect:/admin/adminuserslist";
    }

    @GetMapping("/editql/{id}")
    public String editql(@PathVariable("id") Long id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            model.addAttribute("errorMessage", "User not found");
            return "admin/adminusers/editadmin";
        }
        model.addAttribute("user", user);
        model.addAttribute("roles", userService.getAllRoles()); // Thêm các vai trò vào model
        return "admin/adminusers/editadmin";
    }

    @PostMapping("/editql")
    public String editql(@Valid @ModelAttribute("user") User user, @NotNull BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errorMessage", "Đã có lỗi xảy ra trong quá trình chỉnh sửa tài khoản.");
            return "admin/adminusers/editadmin";
        }

        if (user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getPhone() == null || user.getPhone().isEmpty()) {
            model.addAttribute("errorMessage", "Không được để trống bất kỳ ô thông tin nào.");
            return "admin/adminusers/editadmin";
        }

        User existingUser = userService.findById(user.getId());
        if (existingUser == null) {
            model.addAttribute("errorMessage", "User not found");
            return "admin/adminusers/editadmin";
        }

        // Cập nhật các thông tin cần thiết từ người dùng hiện tại
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());

        // Nếu mật khẩu mới được cung cấp, cập nhật mật khẩu
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        }

        // Cập nhật vai trò người dùng
        existingUser.setRoles(user.getRoles());

        userService.save(existingUser);
        model.addAttribute("successMessage", "Chỉnh sửa tài khoản thành công!");
        return "redirect:/admin/listusers";
    }





    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return "redirect:/admin/listusers";
    }
    @GetMapping("/deleteql/{id}")
    public String deleteql(@PathVariable("id") Long id) {
        userService.delete(id);
        return "redirect:/admin/adminusers";
    }


}
