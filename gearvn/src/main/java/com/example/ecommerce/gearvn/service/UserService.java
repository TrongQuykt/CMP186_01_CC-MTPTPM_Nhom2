package com.example.ecommerce.gearvn.service;

import com.example.ecommerce.gearvn.model.Role;
import com.example.ecommerce.gearvn.model.User;
import com.example.ecommerce.gearvn.repository.IRoleRepository;
import com.example.ecommerce.gearvn.repository.IUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    @Autowired
    private final IUserRepository userRepository;
    @Autowired
    private final IRoleRepository roleRepository;
    @Autowired
    private final JavaMailSender mailSender;

    private PasswordEncoder passwordEncoder;

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Lưu người dùng mới vào cơ sở dữ liệu sau khi mã hóa mật khẩu.
    public void save(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            User existingUser = userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
            user.setPassword(existingUser.getPassword());
        }
        userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(String.valueOf(id));
    }

    // Gán vai trò mặc định cho người dùng dựa trên tên người dùng.
    public void setDefaultRole(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Default role not found"));
        user.getRoles().add(defaultRole);
        userRepository.save(user);
    }

    public void setRole(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        user.getRoles().add(role);
        userRepository.save(user);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Tải thông tin chi tiết người dùng để xác thực.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Lấy danh sách các quyền (vai trò) của người dùng và thêm vào UserDetails
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .build();
    }

    // Tìm kiếm người dùng dựa trên tên đăng nhập.
    public Optional<User> findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public List<User> findUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    public List<User> findUsersByRoles(Set<String> roles) {
        return userRepository.findByRolesNameIn(roles);
    }

    // Tìm kiếm người dùng dựa trên ID.
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElse(null);
    }

    public List<User> getUsersByRoleId(int roleId) {
        return userRepository.findByRolesId(roleId);
    }

    public int countAccountsByRoles(List<Integer> roleIds) {
        return userRepository.countByRolesIdIn(roleIds);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    public void createPasswordResetToken(String email, String token) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email " + email));
        user.setResetToken(token);
        user.setTokenExpiryDate(LocalDateTime.now().plusMinutes(30)); // Set expiry date 30 minutes from now
        userRepository.save(user);
        sendEmail(email, token);
    }

    public void resetPassword(String token, String password) {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("Token không hợp lệ hoặc đã hết hạn"));
        user.setPassword(passwordEncoder.encode(password)); // Use passwordEncoder to encode the password
        user.setResetToken(null);
        user.setTokenExpiryDate(null);
        userRepository.save(user);
    }

    private void sendEmail(String recipientEmail, String token) {
        String resetPasswordLink = "http://localhost:8080/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("YÊU CẦU ĐẶT LẠI MẬT KHẨU");
        message.setText("Nhấp vào liên kết dưới đây để đặt lại mật khẩu của bạn:\n" + resetPasswordLink);
        mailSender.send(message);
    }
}
