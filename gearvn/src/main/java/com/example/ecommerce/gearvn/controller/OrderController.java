package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.Order;
import com.example.ecommerce.gearvn.model.User;
import com.example.ecommerce.gearvn.repository.*;
import com.example.ecommerce.gearvn.service.CartService;
import com.example.ecommerce.gearvn.service.DiscountService;
import com.example.ecommerce.gearvn.service.OrderService;
import com.example.ecommerce.gearvn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class OrderController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private  OrderService orderService;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentMethodRepository shipmentMethodRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscountService discountService;

    @Autowired
    private AddressRepository addressRepository;

    @GetMapping("/order/{orderId}")
    public String viewOrder(@PathVariable Long orderId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getCurrentUser(userDetails);
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty() || !orderOptional.get().getUser().getId().equals(user.getId())) {
            return "redirect:/";
        }

        Order order = orderOptional.get();
        model.addAttribute("order", order);
        model.addAttribute("orderItems", order.getOrderItems());
        paymentRepository.findByOrder(order).ifPresent(payment -> model.addAttribute("payment", payment));
        shipmentRepository.findByOrder(order).ifPresent(shipment -> model.addAttribute("shipment", shipment));

        return "/order/order_details";
    }
    @PostMapping("/cancel/{orderId}")
    public String cancelOrder(@RequestParam("orderId") Long orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(5);
            orderRepository.save(order);
            model.addAttribute("message", "Hủy đơn thành công.");
        }
        return "redirect:/order-history";
    }

    @PostMapping("/confirm-received/{orderId}")
    public String confirmShipping(@RequestParam("orderId") Long orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(4);
            orderRepository.save(order);
            model.addAttribute("message", "Xác nhận giao hàng thành công.");
        }
        return "redirect:/order-history";
    }
}
