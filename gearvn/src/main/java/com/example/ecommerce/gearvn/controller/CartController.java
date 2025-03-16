package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.Cart;
import com.example.ecommerce.gearvn.model.Product;
import com.example.ecommerce.gearvn.model.User;
import com.example.ecommerce.gearvn.repository.CartRepository;
import com.example.ecommerce.gearvn.service.CartService;
import com.example.ecommerce.gearvn.service.ProductService;
import com.example.ecommerce.gearvn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService; // Thêm UserService để lấy thông tin người dùng

    @GetMapping
    public String viewCart(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            model.addAttribute("cart", null);
            return "cart/view";
        }

        Optional<User> user = userService.findByUsername(userDetails.getUsername());
        if (!user.isPresent()) {
            model.addAttribute("cart", null);
            return "cart/view";
        }
        Cart cart = cartService.getCartByUser(user.get());
        boolean hasItems = cart != null && cart.getItems() != null && !cart.getItems().isEmpty();
        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", cart.getFormattedTotalPrice());
        model.addAttribute("totalQuantity", cart.getTotalQuantity());
        model.addAttribute("hasItems", hasItems);
        return "cart/view";
    }

    @PostMapping("/add")
    @ResponseBody
    public String addProductToCart(@RequestParam Long productId, @RequestParam int quantity, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "User not logged in";
        }

        Optional<User> user = userService.findByUsername(userDetails.getUsername());
        if (!user.isPresent()) {
            return "User not found";
        }

        Optional<Product> productOpt = productService.getProductById(productId);
        if (!productOpt.isPresent()) {
            return "Product not found";
        }

        Product product = productOpt.get();
        cartService.addProductToCart(user.get(), product, quantity);


        return "Product added to cart";
    }
    @PostMapping("/update")
    public String updateCartItem(@RequestParam Long productId, @RequestParam int quantity, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<User> user = userService.findByUsername(userDetails.getUsername());
        if (!user.isPresent()) {
            return "redirect:/login";
        }

        Optional<Product> productOpt = productService.getProductById(productId);
        if (!productOpt.isPresent()) {
            return "redirect:/cart";
        }

        Product product = productOpt.get();
        cartService.updateProductQuantity(user.get(), product, quantity);

        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeCartItem(@RequestParam Long productId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<User> user = userService.findByUsername(userDetails.getUsername());
        if (!user.isPresent()) {
            return "redirect:/login";
        }

        Optional<Product> productOpt = productService.getProductById(productId);
        if (!productOpt.isPresent()) {
            return "redirect:/cart";
        }

        Product product = productOpt.get();
        cartService.removeProductFromCart(user.get(), product);

        return "redirect:/cart";
    }
    @PostMapping("/clear")
    public String clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        // Lấy thông tin người dùng hiện tại
        User user = userService.getCurrentUser(userDetails);
        if (user == null) {
            return "redirect:/login";
        }

        Cart cart = cartRepository.findByUser(user);
        if (cart != null) {
            cart.getItems().clear();
            cartRepository.save(cart);
        }

        return "redirect:/cart";
    }


}
