package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.Discount;
import com.example.ecommerce.gearvn.model.Order;
import com.example.ecommerce.gearvn.model.Product;
import com.example.ecommerce.gearvn.model.User;
import com.example.ecommerce.gearvn.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Collections;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/admin")
public class AdminHomeController
{
    // Add this at the top of your class
    private static final Logger logger = LoggerFactory.getLogger(AdminHomeController.class);
    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscountService discountService;

    @GetMapping("/index")
    public String index(Model model) {
        List<Order> donHuy = orderService.getCancelledOrders();
        List<User> users = userService.getUsersByRoleId(2);
        List<Order> doanhThu = orderService.getCompletedOrCancelledOrders();
        List<Product> sanPham = productService.getActiveProducts();
        List<Discount> giamGia = discountService.getActiveDiscounts();
        long soLuongLoaiSanPham = categoryService.countCategories();
        int soLuongTaiKhoanQuanLy = userService.countAccountsByRoles(Collections.singletonList(1));
        int soLuongDonHangHoanThanh = orderService.countCompletedOrders();
        long soLuongBrand = brandService.countBrands();
        int soLuongSP = sanPham.size();
        double tongTien = orderService.calculateTotalRevenue();
//        int tongDonHang = donHuy.size() + doanhThu.size();
//        int soLuong = users.size();

        // Updated logic for counting orders by statuses
        List<Integer> validStatuses = Arrays.asList(2, 3, 4, 5);
        int tongDonHang = orderService.countOrdersByStatuses(validStatuses);
        int soLuong = users.size();


        // Log the values for debugging
        logger.info("Cancelled Orders: " + donHuy.size());
        logger.info("Completed/Cancelled Orders: " + doanhThu.size());
        logger.info("Total Orders: " + tongDonHang);
        // Format the total revenue
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedTongTien = currencyFormatter.format(tongTien);

        model.addAttribute("User", soLuong);
        model.addAttribute("tongTien", formattedTongTien);
        model.addAttribute("SanPham", soLuongSP);
        model.addAttribute("DonHuy", donHuy.size());
        model.addAttribute("TongDonHang", tongDonHang);
        model.addAttribute("MaGiamGia", giamGia.size());
        model.addAttribute("SoLuongLoaiSanPham", soLuongLoaiSanPham);
        model.addAttribute("SoLuongTaiKhoanQuanLy", soLuongTaiKhoanQuanLy);
        model.addAttribute("SoLuongDonHangHoanThanh", soLuongDonHangHoanThanh);
        model.addAttribute("soLuongBrand", soLuongBrand);

        return "admin/home/index";
    }
}
