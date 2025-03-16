package com.example.ecommerce.gearvn.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "receipt_products")
public class ReceiptProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private double price;
    private String image; // Hình ảnh phiếu nhập hàng

    private LocalDate createDay; // Ngày nhập hàng

    private int status; // Trạng thái

    // Constructor without id
    public ReceiptProduct(Product product, int quantity, double price, String image, LocalDate createDay, int status) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.image = image;
        this.createDay = createDay;
        this.status = status;
    }
    public String getFormattedPrice() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormat.format(price);
    }
}
