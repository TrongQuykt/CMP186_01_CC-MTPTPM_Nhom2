package com.example.ecommerce.gearvn.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private String description;
    private String URLImage;
    private String URLImage1;
    private String URLImage2;
    private String URLImage3;


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "condition_id")
    private Condition condition;

    @ManyToOne
    @JoinColumn(name = "warranty_id")
    private Warranty warranty;

    @OneToMany(mappedBy = "product")
    private List<ProductInventory> productInventories;

    private int quantity;
    private int quantitySold;
    private int status;

    public boolean isOutOfStock() {
//        System.out.println("Product ID: " + id);
//        System.out.println("Quantity: " + quantity);
//        System.out.println("Quantity Sold: " + quantitySold);
        return quantitySold >= quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getURLImage3() {
        return URLImage3;
    }

    public void setURLImage3(String URLImage3) {
        this.URLImage3 = URLImage3;
    }

    public String getURLImage2() {
        return URLImage2;
    }

    public void setURLImage2(String URLImage2) {
        this.URLImage2 = URLImage2;
    }

    public String getURLImage1() {
        return URLImage1;
    }

    public void setURLImage1(String URLImage1) {
        this.URLImage1 = URLImage1;
    }

    public String getURLImage() {
        return URLImage;
    }

    public void setURLImage(String URLImage) {
        this.URLImage = URLImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public String getFormattedPrice() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormat.format(price);
    }

    public String getFormattedTotalPrice(int quantity) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormat.format(price * quantity);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
