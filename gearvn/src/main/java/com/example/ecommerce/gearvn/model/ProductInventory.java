package com.example.ecommerce.gearvn.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_inventories")
public class ProductInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;


    private int quantitySold;

    public ProductInventory(Product product, int quantity, int quantitySold) {
        this.product = product;
        this.quantity = quantity;
        this.quantitySold = quantitySold;
    }
}
