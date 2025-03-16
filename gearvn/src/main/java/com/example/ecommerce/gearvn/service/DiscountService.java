package com.example.ecommerce.gearvn.service;

import com.example.ecommerce.gearvn.model.Discount;
import com.example.ecommerce.gearvn.repository.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DiscountService {

    private final DiscountRepository discountRepository;

    @Autowired
    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    public Optional<Discount> getDiscountById(Long id) {
        return discountRepository.findById(id);
    }

    public Discount addDiscount(Discount discount) {
        return discountRepository.save(discount);
    }
    public Discount findByCode(String code) {
        return discountRepository.findByCode(code).orElse(null);
    }
    public void updateDiscount(Discount discount) {
        discountRepository.save(discount);
    }
    public void updateDiscount(Long id, Discount discount) {
        Discount existingDiscount = discountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid discount Id:" + id));
        existingDiscount.setName(discount.getName());
        existingDiscount.setCode(discount.getCode());
        existingDiscount.setDescription(discount.getDescription());
        existingDiscount.setDiscountPrice(discount.getDiscountPrice());
        existingDiscount.setQuantity(discount.getQuantity());
        existingDiscount.setUseNumber(discount.getUseNumber());
        existingDiscount.setStatus(discount.getStatus());
        discountRepository.save(existingDiscount);
    }


    public void deleteDiscountById(Long id) {
        if (!discountRepository.existsById(id)) {
            throw new IllegalStateException("Discount with ID " + id + " does not exist.");
        }
        discountRepository.deleteById(id);
    }
    public List<Discount> getActiveDiscounts() {
        return discountRepository.findByStatus(1);
    }
}
