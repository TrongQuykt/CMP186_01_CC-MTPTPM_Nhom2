package com.example.ecommerce.gearvn.repository;

import com.example.ecommerce.gearvn.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Lấy danh sách userId duy nhất (người gửi) liên quan đến admin (người nhận)
    @Query("SELECT DISTINCT m.senderId FROM Message m WHERE m.receiverId = :adminId")
    Set<Long> findDistinctUserIdsByAdminId(@Param("adminId") Long adminId);

    @Query("SELECT DISTINCT m.receiverId FROM Message m WHERE m.senderId = :adminId")
    Set<Long> findDistinctReceiverIdsByAdminId(@Param("adminId") Long adminId);

    // Lấy tin nhắn giữa admin và user, sắp xếp theo timestamp
    @Query("SELECT m FROM Message m WHERE (m.senderId = :userId AND m.receiverId = :adminId) OR (m.senderId = :adminId AND m.receiverId = :userId) ORDER BY m.timestamp ASC")
    List<Message> findMessagesBetweenAdminAndUser(@Param("userId") Long userId, @Param("adminId") Long adminId);


}