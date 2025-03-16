package com.example.ecommerce.gearvn.controller;


import com.example.ecommerce.gearvn.model.Payment;
import com.example.ecommerce.gearvn.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/payments")
public class AdminPaymentController {
    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping("/transactions")
    public String viewTransactions(Model model) {
        List<Payment> completedPayments = paymentRepository.findByStatus(1); // Lọc ra các giao dịch đã thanh toán (status = 1)
        model.addAttribute("completedPayments", completedPayments);
        return "admin/payments/transactions"; // Tên view hiển thị danh sách giao dịch
    }
}
