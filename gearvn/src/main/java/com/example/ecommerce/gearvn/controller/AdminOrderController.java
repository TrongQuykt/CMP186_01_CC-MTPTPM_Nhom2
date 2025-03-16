package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.Order;
import com.example.ecommerce.gearvn.model.Payment;
import com.example.ecommerce.gearvn.model.Shipment;
import com.example.ecommerce.gearvn.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentMethodRepository shipmentMethodRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/pending")
    public String viewPendingOrders(Model model) {
        List<Order> orders = orderRepository.findByStatus(2);
        model.addAttribute("orders", orders);
        return "admin/orders/pending";
    }

    @PostMapping("/confirm")
    public String confirmOrder(@RequestParam("orderId") Long orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null && order.getStatus() == 2) {
            order.setStatus(3);
            orderRepository.save(order);
            model.addAttribute("message", "Đơn hàng đã được xác nhận vận chuyển.");
        } else {
            model.addAttribute("message", "Đơn hàng không ở trạng thái chờ xác nhận.");
        }
        return "redirect:/admin/orders/pending";
    }

    @GetMapping("/cancelled")
    public String viewCancelledOrders(Model model) {
        List<Order> orders = orderRepository.findByStatus(5);
        model.addAttribute("orders", orders);
        return "admin/orders/cancelled";
    }


    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam("orderId") Long orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(5);
            orderRepository.save(order);
            model.addAttribute("message", "Hủy đơn thành công.");
        }
        return "redirect:/admin/orders/pending";
    }

    @GetMapping("/shipping")
    public String viewShippingOrders(Model model) {
        List<Order> orders = orderRepository.findByStatus(3);
        model.addAttribute("orders", orders);
        return "admin/orders/shipping";
    }

    @PostMapping("/confirmShipping")
    public String confirmShipping(@RequestParam("orderId") Long orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(4);
            orderRepository.save(order);
            model.addAttribute("message", "Xác nhận giao hàng thành công.");
        }
        return "redirect:/admin/orders/shipping";
    }

    @GetMapping("/completed")
    public String viewCompletedOrders(Model model) {
        List<Order> orders = orderRepository.findByStatus(4);
        model.addAttribute("orders", orders);
        return "admin/orders/completed";
    }

    @GetMapping("/details/{orderId}")
    public String viewOrderDetails(@PathVariable("orderId") Long orderId, Model model) {
        // Tìm đơn hàng dựa trên orderId
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order != null) {
            // Nếu đơn hàng tồn tại, thêm vào model để truyền dữ liệu đơn hàng đến view
            model.addAttribute("order", order);

            // Lấy thông tin của shipment nếu có
            Shipment shipment = shipmentRepository.findByOrder(order).orElse(null);
            if (shipment != null) {
                model.addAttribute("shipment", shipment);
            }

            // Lấy thông tin của payment nếu có
            Payment payment = paymentRepository.findByOrder(order).orElse(null);
            if (payment != null) {
                model.addAttribute("payment", payment);
            }
        }

        // Chuyển hướng đến trang hiển thị chi tiết đơn hàng trong admin
        return "admin/orders/details";
    }

}
