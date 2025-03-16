package com.example.ecommerce.gearvn.service;

import com.example.ecommerce.gearvn.model.Wishlist;
import com.example.ecommerce.gearvn.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    @Autowired
    public WishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    public List<Wishlist> getAllWishlists() {
        return wishlistRepository.findAll();
    }

    public Optional<Wishlist> getWishlistById(Long id) {
        return wishlistRepository.findById(id);
    }

    public Wishlist addWishlist(Wishlist wishlist) {
        return wishlistRepository.save(wishlist);
    }

    public Wishlist updateWishlist(Wishlist wishlist) {
        Wishlist existingWishlist = wishlistRepository.findById(wishlist.getId())
                .orElseThrow(() -> new IllegalStateException("Wishlist with ID " + wishlist.getId() + " does not exist."));
        existingWishlist.setUser(wishlist.getUser());
        existingWishlist.setProduct(wishlist.getProduct());
        return wishlistRepository.save(existingWishlist);
    }

    public void deleteWishlistById(Long id) {
        if (!wishlistRepository.existsById(id)) {
            throw new IllegalStateException("Wishlist with ID " + id + " does not exist.");
        }
        wishlistRepository.deleteById(id);
    }

    public List<Wishlist> getWishlistByUserId(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    public Wishlist saveWishlist(Wishlist wishlist) {
        return wishlistRepository.save(wishlist);
    }

    public void deleteWishlist(Long id) {
        wishlistRepository.deleteById(id);
    }
    public boolean isProductInWishlist(Long userId, Long productId) {
        // Triển khai logic kiểm tra xem sản phẩm có trong danh sách yêu thích của người dùng hay không
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }
}
