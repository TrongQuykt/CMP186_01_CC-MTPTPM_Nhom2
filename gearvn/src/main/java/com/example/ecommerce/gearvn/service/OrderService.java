package com.example.ecommerce.gearvn.service;

import com.example.ecommerce.gearvn.model.*;
import com.example.ecommerce.gearvn.repository.*;
import jakarta.servlet.ServletOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order placeOrder(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(new Date());
        order.setStatus(1); // Set status to 'placed'

        order.setOrderItems(cart.getItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            return orderItem;
        }).collect(Collectors.toList()));

        // Calculate total
        double total = order.getOrderItems().stream()
                .mapToDouble(orderItem -> orderItem.getProduct().getPrice() * orderItem.getQuantity())
                .sum();
        order.setTotal(total);

        return orderRepository.save(order);
    }

    public List<Order> getAllNonPendingOrders() {
        return orderRepository.findByStatusNot(1);
    }

    public List<Order> getCompletedOrCancelledOrders() {
        return orderRepository.findByStatusNotIn(List.of(4, 5));
    }

    public List<Order> getCancelledOrders() {
        return orderRepository.findByStatus(5);
    }

    public int getCompletedOrdersCount() {
        return (int) orderRepository.countByStatus(4);
    }

    public double calculateTotalRevenue() {
        List<Order> completedOrders = orderRepository.findByStatus(4); // Lấy tất cả các đơn hàng hoàn thành
        return completedOrders.stream()
                .mapToDouble(Order::getTotal)
                .sum();
    }




    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetweenAndStatusNot(startDate, endDate, 1);
    }

    public List<Order> getCancelledOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetweenAndStatus(startDate, endDate, 5);
    }



    public void exportOrdersToExcel(List<Order> orders, ServletOutputStream outputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Đơn hàng");

        // Tạo font chữ tiêu đề
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        // Tạo kiểu ô cho tiêu đề
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Tạo hàng tiêu đề
        Row headerRow = sheet.createRow(0);

        // Định nghĩa các ô tiêu đề
        String[] headers = {"Mã đơn hàng", "Tên khách hàng", "Email", "SĐT", "Tổng tiền", "Ngày đặt", "Trạng thái", "Địa chỉ", "Sản phẩm đã mua"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Điền dữ liệu vào các hàng
        int rowNum = 1;
        for (Order order : orders) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(order.getId());
            row.createCell(1).setCellValue(order.getUser().getUsername());
            row.createCell(2).setCellValue(order.getUser().getEmail());
            row.createCell(3).setCellValue(order.getUser().getPhone());
            row.createCell(4).setCellValue(order.getTotal());
            row.createCell(5).setCellValue(order.getOrderDate().toString());
            row.createCell(6).setCellValue(getOrderStatusString(order.getStatus()));

            String address = order.getUser().getAddresses().stream()
                    .map(addr -> addr.getStreet() + ", " + addr.getWard() + ", " + addr.getDistrict() + ", " + addr.getCity())
                    .collect(Collectors.joining("; "));
            row.createCell(7).setCellValue(address);

            String products = order.getOrderItems().stream()
                    .map(oi -> oi.getProduct().getName() + " x " + oi.getQuantity())
                    .collect(Collectors.joining(", "));
            row.createCell(8).setCellValue(products);
        }

        // Ghi vào output stream
        workbook.write(outputStream);
        workbook.close();
    }

    private String getOrderStatusString(int status) {
        switch (status) {
            case 2:
                return "Chờ cửa hàng xác nhận";
            case 3:
                return "Khách chưa xác nhận";
            case 4:
                return "Đã hoàn thành";
            case 5:
                return "Đã Hủy";
            default:
                return "Không xác định";
        }
    }
    public int countCompletedOrders() {
        return (int) orderRepository.countByStatus(4);
    }
    public int countOrdersByStatuses(List<Integer> statuses) {
        return orderRepository.countByStatusIn(statuses);
    }

}
