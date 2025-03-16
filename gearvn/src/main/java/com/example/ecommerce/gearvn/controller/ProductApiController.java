package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.Product;
import com.example.ecommerce.gearvn.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/products")
public class ProductApiController {
    @Autowired
    private ProductService productService;

    private final String imageDir = "src/main/resources/static/images/products/";

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping
    public Product createProduct(@ModelAttribute Product product,
                                 @RequestParam("image") MultipartFile image,
                                 @RequestParam("image1") MultipartFile image1,
                                 @RequestParam("image2") MultipartFile image2,
                                 @RequestParam("image3") MultipartFile image3) throws IOException {
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
        return productService.addProduct(product, image, image1, image2, image3);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found on :: " + id));
        return ResponseEntity.ok().body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @ModelAttribute Product productDetails,
                                                 @RequestParam("image") MultipartFile image,
                                                 @RequestParam("image1") MultipartFile image1,
                                                 @RequestParam("image2") MultipartFile image2,
                                                 @RequestParam("image3") MultipartFile image3) throws IOException {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found on :: " + id));
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setDescription(productDetails.getDescription());
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
        final Product updatedProduct = productService.updateProduct(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found on :: " + id));
        productService.deleteProductById(id);
        return ResponseEntity.ok().build();
    }

    private String saveImage(MultipartFile image) throws IOException {
        String originalFileName = image.getOriginalFilename();
        if (originalFileName != null) {
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            String uniqueFileName = UUID.randomUUID().toString() + extension;
            String imagePath = imageDir + uniqueFileName;
            Files.copy(image.getInputStream(), Paths.get(imagePath), StandardCopyOption.REPLACE_EXISTING);
            return "/images/" + uniqueFileName;
        }
        throw new IOException("Invalid file name");
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String query) {
        return productService.searchProducts(query);
    }
}
