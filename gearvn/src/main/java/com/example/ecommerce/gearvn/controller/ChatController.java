package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.Message;
import com.example.ecommerce.gearvn.model.User;
import com.example.ecommerce.gearvn.service.ChatService;
import com.example.ecommerce.gearvn.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, ChatService chatService, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.userService = userService;
    }

    @GetMapping("/chat/user")
    public String userChat(HttpServletRequest request, Model model, Principal principal, Authentication authentication) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        String role = authentication.getAuthorities().toString();
        if (role.contains("ADMIN")) {
            return "redirect:/chat/admin";
        }

        model.addAttribute("username", username);
        model.addAttribute("role", role);
        return "user-chat";
    }

    @GetMapping("/chat/admin")
    public String adminChat(HttpServletRequest request, Model model, Principal principal, Authentication authentication) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        String role = authentication.getAuthorities().toString();
        if (!role.contains("ADMIN")) {
            return "redirect:/chat/user";
        }

        model.addAttribute("username", username);
        model.addAttribute("role", role);
        return "admin-chat";
    }

    @MessageMapping("/sendMessage")
    public void sendMessage(Message message, Principal principal) {
        String username = principal.getName();
        Optional<User> senderOptional = userService.getUserIdByUsername(username);
        if (!senderOptional.isPresent()) {
            System.err.println("User not found: " + username);
            return;
        }
        User sender = senderOptional.get();
        message.setSenderId(sender.getId());
        message.setUsername(username);
        message.setTimestamp(LocalDateTime.now());
        message.setStatus("sent");

        Long receiverId;
        if (sender.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
            if (message.getReceiverId() == null) {
                System.err.println("Receiver ID is required for admin");
                return;
            }
            Optional<User> receiverOptional = userService.findByUserId(message.getReceiverId());
            if (receiverOptional.isPresent() && receiverOptional.get().getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
                System.err.println("Admin cannot send message to another admin");
                return;
            }
            receiverId = message.getReceiverId();
        } else {
            List<User> adminList = userService.findByRole("ADMIN");
            if (adminList.isEmpty()) {
                System.err.println("No admin found");
                return;
            }
            Optional<User> adminOptional = adminList.stream().findFirst();
            receiverId = adminOptional.get().getId();
            message.setReceiverId(receiverId);
        }

        chatService.saveMessage(message);

        Optional<User> senderUser = userService.findByUserId(message.getSenderId());
        Optional<User> receiverUser = userService.findByUserId(receiverId);
        if (!senderUser.isPresent() || !receiverUser.isPresent()) {
            System.err.println("Sender or receiver not found");
            return;
        }

        String senderUsername = senderUser.get().getUsername();
        String receiverUsername = receiverUser.get().getUsername();

        messagingTemplate.convertAndSendToUser(receiverUsername, "/queue/messages", message);
        messagingTemplate.convertAndSendToUser(senderUsername, "/queue/messages", message);
    }

    @GetMapping("/api/users-with-messages")
    @ResponseBody
    public List<User> getUsersWithMessages(Authentication authentication) {
        if (!authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            throw new SecurityException("Only admin can access this");
        }
        String username = authentication.getName();
        Optional<User> adminOptional = userService.getUserIdByUsername(username);
        if (!adminOptional.isPresent()) {
            throw new IllegalArgumentException("Admin không tồn tại");
        }
        Long adminId = adminOptional.get().getId();
        List<User> users = chatService.getUsersWithMessages(adminId);
        System.out.println("Users with messages for admin " + adminId + ": " + users);
        return users;
    }

    @GetMapping("/api/messages")
    @ResponseBody
    public List<Message> getMessages(@RequestParam Long userId, Authentication authentication) {
        if (!authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            throw new SecurityException("Only admin can access this");
        }
        String username = authentication.getName();
        Optional<User> adminOptional = userService.getUserIdByUsername(username);
        if (!adminOptional.isPresent()) {
            throw new IllegalArgumentException("Admin không tồn tại");
        }
        Long adminId = adminOptional.get().getId();
        List<Message> messages = chatService.getMessagesByUserId(userId, adminId);
        System.out.println("Messages between admin " + adminId + " and user " + userId + ": " + messages);
        return messages;
    }

    @GetMapping("/api/latest-message")
    @ResponseBody
    public Message getLatestMessage(@RequestParam Long userId, Authentication authentication) {
        if (!authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            throw new SecurityException("Only admin can access this");
        }
        String username = authentication.getName();
        Optional<User> adminOptional = userService.getUserIdByUsername(username);
        if (!adminOptional.isPresent()) {
            throw new IllegalArgumentException("Admin không tồn tại");
        }
        Long adminId = adminOptional.get().getId();
        return chatService.getLatestMessageByUserId(userId, adminId);
    }

    @GetMapping("/api/user/messages")
    @ResponseBody
    public List<Message> getUserMessages(Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOptional = userService.getUserIdByUsername(username);
        if (!userOptional.isPresent()) {
            System.err.println("User not found: " + username);
            return new ArrayList<>();
        }
        User user = userOptional.get();
        Long userId = user.getId();

        List<User> adminList = userService.findByRole("ADMIN");
        if (adminList.isEmpty()) {
            System.err.println("No admin found for user " + userId);
            return new ArrayList<>();
        }
        Optional<User> adminOptional = adminList.stream().findFirst();
        Long adminId = adminOptional.get().getId();

        List<Message> messages = chatService.getMessagesByUserId(userId, adminId);
        System.out.println("Messages for user " + userId + " with admin " + adminId + ": " + messages);
        return messages;
    }
}