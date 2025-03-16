package com.example.ecommerce.gearvn.service;

import com.example.ecommerce.gearvn.model.Cart;
import com.example.ecommerce.gearvn.model.CartItem;
import com.example.ecommerce.gearvn.model.Product;
import com.example.ecommerce.gearvn.model.User;
import com.example.ecommerce.gearvn.repository.CartItemRepository;
import com.example.ecommerce.gearvn.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);
    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user);
    }

    public void addProductToCart(User user, Product product, int quantity) {
        Cart cart = cartRepository.findByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        Optional<CartItem> existingCartItemOpt = Optional.ofNullable(cartItemRepository.findByCartAndProduct(cart, product));
        if (existingCartItemOpt.isPresent()) {
            CartItem existingCartItem = existingCartItemOpt.get();
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            cartItemRepository.save(existingCartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }
    public void updateProductQuantity(User user, Product product, int quantity) {
        Cart cart = cartRepository.findByUser(user);
        if (cart != null) {
            CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product);
            if (cartItem != null) {
                cartItem.setQuantity(quantity);
                cartItemRepository.save(cartItem);
            }
        }
    }

    public void removeProductFromCart(User user, Product product) {
        Cart cart = cartRepository.findByUser(user);
        if (cart == null) {
            logger.error("Cart not found for user: {}", user.getUsername());
            return;
        }

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product);
        if (cartItem != null) {
            logger.info("Attempting to remove cart item: {}", cartItem);
            cartItemRepository.delete(cartItem);
            // Verify deletion
            CartItem deletedCartItem = cartItemRepository.findByCartAndProduct(cart, product);
            if (deletedCartItem == null) {
                logger.info("Successfully removed product {} from cart for user {}", product.getName(), user.getUsername());
            } else {
                logger.error("Failed to remove product {} from cart for user {}", product.getName(), user.getUsername());
            }
        } else {
            logger.error("CartItem not found for product: {} in cart", product.getName());
        }
    }
    public void clearCart(User user) {
        Cart cart = cartRepository.findByUser(user);
        if (cart != null) {
            cartItemRepository.deleteAll(cart.getItems()); // Use cart.getItems() instead of cart.getCartItems()
        }
    }








}
