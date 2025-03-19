package com.example.ecommerce.gearvn.service;

import com.example.ecommerce.gearvn.model.Message;
import com.example.ecommerce.gearvn.model.User;
import com.example.ecommerce.gearvn.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChatService {

    private final MessageRepository messageRepository;
    private final UserService userService;

    @Autowired
    public ChatService(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    public List<User> getUsersWithMessages(Long adminId) {
        Set<Long> userIds = messageRepository.findDistinctUserIdsByAdminId(adminId);
        Set<Long> receiverIds = messageRepository.findDistinctReceiverIdsByAdminId(adminId);

        Set<Long> allUserIds = Stream.concat(userIds.stream(), receiverIds.stream())
                .filter(id -> !id.equals(adminId))
                .collect(Collectors.toSet());

        return allUserIds.stream()
                .map(userId -> userService.findByUserId(userId).orElse(null))
                .filter(Objects::nonNull)
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName().equals("USER"))) // Sửa "USER" thành "ROLE_USER" nếu cần
                .collect(Collectors.toList());
    }

    public List<Message> getMessagesByUserId(Long userId, Long adminId) {
        return messageRepository.findMessagesBetweenAdminAndUser(userId, adminId);
    }

    public Message getLatestMessageByUserId(Long userId, Long adminId) {
        List<Message> messages = messageRepository.findMessagesBetweenAdminAndUser(userId, adminId);
        return messages.isEmpty() ? null : messages.get(messages.size() - 1); // Lấy tin nhắn mới nhất
    }
}