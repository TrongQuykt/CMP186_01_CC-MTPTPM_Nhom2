package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.Product;
import com.example.ecommerce.gearvn.model.User;
import com.example.ecommerce.gearvn.model.Wishlist;
import com.example.ecommerce.gearvn.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ConditionService conditionService;
    private final WarrantyService warrantyService;
    private final WishlistService wishlistService;
    private final UserService userService;
    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService, BrandService brandService, ConditionService conditionService, WarrantyService warrantyService, WishlistService wishlistService, UserService userService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.conditionService = conditionService;
        this.warrantyService = warrantyService;
        this.wishlistService = wishlistService;
        this.userService = userService;
    }
//    @GetMapping("/detail/{id}")
//    public String showProductDetail(@PathVariable Long id, Model model) {
//        Product product = productService.getProductById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
//        model.addAttribute("product", product);
//
//        List<Product> productsByCategory = productService.getProductsByCategory(product.getCategory().getId(), product.getId());
//        List<Product> similarProducts = productService.getRandomProducts(productsByCategory, 5); // Lấy ngẫu nhiên 5 sản phẩm
//        model.addAttribute("similarProducts", similarProducts);
//        return "/products/product-detail";
//    }
    @GetMapping("/detail/{id}")
    public String showProductDetail(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Lấy thông tin chi tiết sản phẩm
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);

        // Lấy danh sách sản phẩm tương tự
        List<Product> productsByCategory = productService.getProductsByCategory(product.getCategory().getId(), product.getId());
        List<Product> similarProducts = productService.getRandomProducts(productsByCategory, 5);
        model.addAttribute("similarProducts", similarProducts);

        // Nếu người dùng đã đăng nhập
        if (userDetails != null) {
            User user = userService.getCurrentUser(userDetails);

            // Kiểm tra xem sản phẩm đã có trong danh sách yêu thích của người dùng hay chưa
            if (!wishlistService.isProductInWishlist(user.getId(), product.getId())) {
                // Nếu chưa có, thêm sản phẩm vào danh sách yêu thích
                Wishlist wishlist = new Wishlist();
                wishlist.setUser(user);
                wishlist.setProduct(product);
                wishlistService.addWishlist(wishlist);
            }
        }

        return "/products/product-detail";
    }

}
