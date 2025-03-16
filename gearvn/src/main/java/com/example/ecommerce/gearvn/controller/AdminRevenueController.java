package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.Order;
import com.example.ecommerce.gearvn.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import com.example.ecommerce.gearvn.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/revenue")
public class AdminRevenueController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/index")
    public String index(Model model) {
        List<Order> orders = orderService.getAllNonPendingOrders();
        List<Order> revenueOrders = orderService.getCompletedOrCancelledOrders();
        List<Order> cancelledOrders = orderService.getCancelledOrders();
        int completedOrdersCount = orderService.getCompletedOrdersCount();
        double totalRevenue = orderService.calculateTotalRevenue();


        model.addAttribute("orders", orders);
        model.addAttribute("cancelledOrdersCount", cancelledOrders.size());
        model.addAttribute("totalOrdersCount", orders.size());
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("completedOrdersCount", completedOrdersCount);

        return "admin/revenue/index";
    }

    @PostMapping("/search")
    public String search(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                         @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                         Model model) {

        if (startDate.isAfter(endDate)) {
            notificationService.notifyError("Ngày bắt đầu không được lớn hơn ngày kết thúc");
            return "redirect:/admin/revenue/index";
        }

        if (startDate.isAfter(LocalDateTime.now())) {
            notificationService.notifyError("Ngày bắt đầu không được lớn hơn ngày hiện tại");
            return "redirect:/admin/revenue/index";
        }

        List<Order> orders = orderService.getOrdersByDateRange(startDate, endDate);
        double totalRevenue = orderService.calculateTotalRevenue();

        model.addAttribute("orders", orders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalOrdersCount", orders.size());
        model.addAttribute("cancelledOrdersCount", orderService.getCancelledOrdersByDateRange(startDate, endDate).size());

        return "admin/revenue/index";
    }

    @GetMapping("/export")
    public void exportToExcel(@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                              @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                              HttpServletResponse response) throws IOException {

        List<Order> orders;
        String fileName;

        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                notificationService.notifyError("Ngày bắt đầu không được lớn hơn ngày kết thúc");
                response.sendRedirect("/admin/revenue");
                return;
            }

            if (startDate.isAfter(LocalDateTime.now())) {
                notificationService.notifyError("Ngày bắt đầu không được lớn hơn ngày hiện tại");
                response.sendRedirect("/admin/revenue");
                return;
            }

            orders = orderService.getOrdersByDateRange(startDate, endDate);
            fileName = "DoanhThu_" + startDate.toString().replace(":", "-") + "_to_" + endDate.toString().replace(":", "-") + ".xlsx";
        } else {
            orders = orderService.getAllNonPendingOrders();
            fileName = "TongDoanhThu_" + LocalDateTime.now().toString().replace(":", "-") + ".xlsx";
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        orderService.exportOrdersToExcel(orders, response.getOutputStream());
    }
}
