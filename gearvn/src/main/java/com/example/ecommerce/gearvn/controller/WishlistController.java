package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.User;
import com.example.ecommerce.gearvn.model.Wishlist;
import com.example.ecommerce.gearvn.service.ProductService;
import com.example.ecommerce.gearvn.service.UserService;
import com.example.ecommerce.gearvn.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public WishlistController(WishlistService wishlistService, ProductService productService, UserService userService) {
        this.wishlistService = wishlistService;
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping
    public String showUserWishlists(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails);
        List<Wishlist> wishlists = wishlistService.getWishlistByUserId(currentUser.getId());
        model.addAttribute("wishlists", wishlists);
        return "wishlists/wishlist-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("wishlist", new Wishlist());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("users", userService.getAllUsers());
        return "wishlists/add-wishlist";
    }

//    @PostMapping("/add")
//    public String addWishlist(@Valid @ModelAttribute Wishlist wishlist, BindingResult result) {
//        if (result.hasErrors()) {
//            return "wishlists/add-wishlist";
//        }
//        wishlistService.addWishlist(wishlist);
//        return "redirect:/wishlists";
//    }
    @PostMapping("/add")
    public String addToWishlist(@RequestParam("productId") Long productId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getCurrentUser(userDetails);
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(productService.getProductById(productId).orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId)));
        wishlistService.addWishlist(wishlist);
        return "redirect:/products/detail/" + productId; // Chuyển hướng về trang chi tiết sản phẩm
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Wishlist wishlist = wishlistService.getWishlistById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid wishlist Id:" + id));
        model.addAttribute("wishlist", wishlist);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("users", userService.getAllUsers());
        return "wishlists/update-wishlist";
    }

    @PostMapping("/update/{id}")
    public String updateWishlist(@PathVariable Long id, @Valid @ModelAttribute Wishlist wishlist, BindingResult result) {
        if (result.hasErrors()) {
            wishlist.setId(id); // giữ id trong form
            return "wishlists/update-wishlist";
        }
        wishlistService.updateWishlist(wishlist);
        return "redirect:/wishlists";
    }

    @GetMapping("/delete/{id}")
    public String deleteWishlist(@PathVariable Long id) {
        wishlistService.deleteWishlistById(id);
        return "redirect:/wishlists";
    }

    @GetMapping("/user/{userId}")
    public String getWishlistByUserId(@PathVariable Long userId, Model model) {
        List<Wishlist> wishlist = wishlistService.getWishlistByUserId(userId);
        model.addAttribute("wishlist", wishlist);
        return "user-wishlist";
    }

    @PostMapping("/delete/{id}")
    public String deleteFromWishlist(@PathVariable Long id) {
        wishlistService.deleteWishlist(id);
        return "redirect:/wishlist/user";
    }
}
