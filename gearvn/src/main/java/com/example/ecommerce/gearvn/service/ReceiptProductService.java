package com.example.ecommerce.gearvn.service;

import com.example.ecommerce.gearvn.model.Product;
import com.example.ecommerce.gearvn.model.ProductInventory;
import com.example.ecommerce.gearvn.model.ReceiptProduct;
import com.example.ecommerce.gearvn.repository.ProductInventoryRepository;
import com.example.ecommerce.gearvn.repository.ProductRepository;
import com.example.ecommerce.gearvn.repository.ReceiptProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReceiptProductService {

    private final ReceiptProductRepository receiptProductRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ReceiptProductService(ReceiptProductRepository receiptProductRepository, ProductInventoryRepository productInventoryRepository, ProductRepository productRepository) {
        this.receiptProductRepository = receiptProductRepository;
        this.productInventoryRepository = productInventoryRepository;
        this.productRepository = productRepository;
    }

    public List<ReceiptProduct> getAllReceiptProducts() {
        return receiptProductRepository.findAll();
    }

    public Optional<ReceiptProduct> getReceiptProductById(Long id) {
        return receiptProductRepository.findById(id);
    }

    public ReceiptProduct addReceiptProduct(ReceiptProduct receiptProduct) {
        return receiptProductRepository.save(receiptProduct);
    }

    @Transactional
    public ReceiptProduct updateReceiptProduct(ReceiptProduct receiptProduct) {
        // Find the existing receipt product by ID
        ReceiptProduct existingReceiptProduct = receiptProductRepository.findById(receiptProduct.getId())
                .orElseThrow(() -> new IllegalStateException("Receipt Product with ID " +
                        receiptProduct.getId() + " does not exist."));

        // Retain the original createDay
        LocalDate createDay = existingReceiptProduct.getCreateDay();

        // Update fields from the incoming receiptProduct
        existingReceiptProduct.setProduct(receiptProduct.getProduct());
        existingReceiptProduct.setQuantity(receiptProduct.getQuantity());
        existingReceiptProduct.setPrice(receiptProduct.getPrice());
        existingReceiptProduct.setImage(receiptProduct.getImage());
        existingReceiptProduct.setStatus(receiptProduct.getStatus());

        // Set back the original createDay
        existingReceiptProduct.setCreateDay(createDay);

        // Save the updated receipt product
        return receiptProductRepository.save(existingReceiptProduct);
    }


    public void deleteReceiptProductById(Long id) {
        if (!receiptProductRepository.existsById(id)) {
            throw new IllegalStateException("Receipt Product with ID " + id + " does not exist.");
        }
        receiptProductRepository.deleteById(id);
    }
    @Transactional
    public ReceiptProduct save(ReceiptProduct receiptProduct) {
        ReceiptProduct savedReceiptProduct = receiptProductRepository.save(receiptProduct);

        // Lấy thông tin sản phẩm và số lượng từ phiếu nhập hàng
        Product product = savedReceiptProduct.getProduct();
        int newQuantity = savedReceiptProduct.getQuantity();

        // Tìm hoặc tạo mới ProductInventory cho sản phẩm
        ProductInventory productInventory = productInventoryRepository.findByProduct(product);
        if (productInventory == null) {
            productInventory = new ProductInventory(product, newQuantity, 0);
        } else {
            productInventory.setQuantity(productInventory.getQuantity() + newQuantity);
        }

        // Lưu ProductInventory sau khi cập nhật số lượng
        productInventoryRepository.save(productInventory);

        // Cập nhật lại thông tin quantity và quantitySold của sản phẩm
        product.setQuantity(productInventory.getQuantity());
        product.setQuantitySold(productInventory.getQuantitySold());
        productRepository.save(product); // Lưu lại thông tin sản phẩm sau khi cập nhật quantity và quantitySold

        // Trả về phiếu nhập hàng đã lưu
        return savedReceiptProduct;
    }
}
