package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.Product;
import com.example.ecommerce.gearvn.model.ProductDTO;
import com.example.ecommerce.gearvn.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SearchController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String query) {
        List<Product> products = productService.searchProductsByName(query);
        List<ProductDTO> productDTOs = products.stream()
                .limit(6) // Limit to 6 products
                .map(product -> new ProductDTO(product.getId(), product.getName(), product.getFormattedPrice(), product.getURLImage()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }


}
