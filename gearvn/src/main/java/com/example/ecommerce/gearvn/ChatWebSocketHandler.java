package com.example.ecommerce.gearvn;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Lấy thông tin người dùng và vai trò từ session (nếu có)
        String userName = (String) session.getAttributes().get("username"); // Giả sử bạn lưu thông tin người dùng trong session
        String role = (String) session.getAttributes().get("role"); // Lấy vai trò của người dùng

        // Hiển thị thông tin người dùng và vai trò trong log (debug)
        System.out.println("User: " + userName + " with role: " + role);

        // Gửi thông tin người dùng và vai trò tới client
        String welcomeMessage = "Welcome " + userName + "! Your role is " + role;
        session.sendMessage(new TextMessage(welcomeMessage));
    }
}
