package com.example.ecommerce.gearvn.model;

import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String price;
    private String imageUrl;

    public ProductDTO(Long id, String name, String price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
}

