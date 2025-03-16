package com.example.ecommerce.gearvn.service;

import com.example.ecommerce.gearvn.model.Product;
import com.example.ecommerce.gearvn.model.ProductInventory;
import com.example.ecommerce.gearvn.repository.ProductInventoryRepository;
import com.example.ecommerce.gearvn.repository.ProductRepository;

import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final String imageDir = "src/main/resources/static/images/products/";

    @Autowired
    public ProductService(ProductRepository productRepository, ProductInventoryRepository productInventoryRepository) {
        this.productRepository = productRepository;
        this.productInventoryRepository = productInventoryRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    public List<Product> getProductsByCategory(Long categoryId, Long currentProductId) {
        return productRepository.findByCategoryIdAndIdNot(categoryId, currentProductId);
    }
    public List<Product> getProductsByBrandAndCategory(Long brandId, Long categoryId) {
        return productRepository.findByBrandIdAndCategoryId(brandId, categoryId);
    }
    public List<Product> findByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrandName(category, brand);
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    public List<Product> findByBrand(String brand) {
        return productRepository.findByBrandName(brand);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }
    public List<Product> searchProductsByName(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }
    public List<Product> getRandomProducts(List<Product> products, int count) {
        if (products == null || products.isEmpty()) {
            return Collections.emptyList(); // Trả về danh sách rỗng nếu không có sản phẩm nào
        }

        if (count <= 0 || count > products.size()) {
            count = products.size(); // Đảm bảo số lượng sản phẩm yêu cầu hợp lệ
        }

        Random random = new Random();
        return random.ints(0, products.size())
                .distinct()
                .limit(count)
                .mapToObj(products::get)
                .collect(Collectors.toList());
    }
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product addProduct(Product product, MultipartFile image, MultipartFile image1, MultipartFile image2, MultipartFile image3) throws IOException {
        if (image != null && !image.isEmpty()) {
            String imagePath = saveImage(image);
            product.setURLImage(imagePath);
        }
        if (image1 != null && !image1.isEmpty()) {
            String imagePath1 = saveImage(image1);
            product.setURLImage1(imagePath1);
        }
        if (image2 != null && !image2.isEmpty()) {
            String imagePath2 = saveImage(image2);
            product.setURLImage2(imagePath2);
        }
        if (image3 != null && !image3.isEmpty()) {
            String imagePath3 = saveImage(image3);
            product.setURLImage3(imagePath3);
        }
        return productRepository.save(product);
    }

    public Product updateProduct(@NotNull Product product) {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Product với ID " +
                        product.getId() + " không tồn tại."));
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setCondition(product.getCondition());
        existingProduct.setWarranty(product.getWarranty());
        existingProduct.setStatus(product.getStatus());
        existingProduct.setURLImage(product.getURLImage());
        existingProduct.setURLImage1(product.getURLImage1());
        existingProduct.setURLImage2(product.getURLImage2());
        existingProduct.setURLImage3(product.getURLImage3());
        return productRepository.save(existingProduct);
    }

    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product với ID " + id + " không tồn tại.");
        }
        productRepository.deleteById(id);
    }

    public String saveImage(MultipartFile image) throws IOException {  // Đổi thành public
        String originalFileName = image.getOriginalFilename();
        if (originalFileName != null) {
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            String uniqueFileName = UUID.randomUUID().toString() + extension;
            String imagePath = imageDir + uniqueFileName;
            Files.copy(image.getInputStream(), Paths.get(imagePath), StandardCopyOption.REPLACE_EXISTING);
            return "/images/products/" + uniqueFileName;
        }
        throw new IOException("Invalid file name");
    }
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getProductsByCategoryAndBrand(Long categoryId, Long brandId) {
        return productRepository.findByCategoryAndBrand(categoryId, brandId);
    }
    @Transactional
    public void updateProductSoldQuantity(Product product, int quantitySold) {
        // Cập nhật số lượng đã bán trong Product
        product.setQuantitySold(product.getQuantitySold() + quantitySold);
        if (product.getQuantitySold() >= product.getQuantity()) {
            product.setStatus(0); // Đặt trạng thái sản phẩm là hết hàng
        }
        productRepository.save(product);

        // Cập nhật số lượng đã bán trong ProductInventory
        ProductInventory productInventory = productInventoryRepository.findByProduct(product);
        if (productInventory != null) {
            productInventory.setQuantitySold(productInventory.getQuantitySold() + quantitySold);
            productInventoryRepository.save(productInventory);
        }
    }
    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }
    public List<Product> searchByName(String query) {
        // Implement your search logic here, for example:
        return productRepository.findByNameContainingIgnoreCase(query);
    }
    public List<Product> getActiveProducts() {
        return productRepository.findByStatus(1);
    }

    public int countProductTypes(String categoryName) {
        return productRepository.countByCategory_Name(categoryName);
    }
}
