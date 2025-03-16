package com.example.ecommerce.gearvn.service;

import com.example.ecommerce.gearvn.model.ProductInventory;
import com.example.ecommerce.gearvn.repository.ProductInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductInventoryService {

    private final ProductInventoryRepository productInventoryRepository;

    @Autowired
    public ProductInventoryService(ProductInventoryRepository productInventoryRepository) {
        this.productInventoryRepository = productInventoryRepository;
    }

    public List<ProductInventory> getAllProductInventories() {
        return productInventoryRepository.findAll();
    }

    public Optional<ProductInventory> getProductInventoryById(Long id) {
        return productInventoryRepository.findById(id);
    }

    public ProductInventory addProductInventory(ProductInventory productInventory) {
        return productInventoryRepository.save(productInventory);
    }

    public ProductInventory updateProductInventory(ProductInventory productInventory) {
        ProductInventory existingProductInventory = productInventoryRepository.findById(productInventory.getId())
                .orElseThrow(() -> new IllegalStateException("Product Inventory with ID " +
                        productInventory.getId() + " does not exist."));
        existingProductInventory.setProduct(productInventory.getProduct());
        existingProductInventory.setQuantity(productInventory.getQuantity());
        existingProductInventory.setQuantitySold(productInventory.getQuantitySold());
        return productInventoryRepository.save(existingProductInventory);
    }

    public void deleteProductInventoryById(Long id) {
        if (!productInventoryRepository.existsById(id)) {
            throw new IllegalStateException("Product Inventory with ID " + id + " does not exist.");
        }
        productInventoryRepository.deleteById(id);
    }
}
