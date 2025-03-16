package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.ReceiptProduct;
import com.example.ecommerce.gearvn.service.ProductService;
import com.example.ecommerce.gearvn.service.ReceiptProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/receiptproducts")
public class ReceiptProductController {

    private final ReceiptProductService receiptProductService;
    private final ProductService productService;
    private final String imageDir = "src/main/resources/static/images/receiptProducts/";

    @Autowired
    public ReceiptProductController(ReceiptProductService receiptProductService, ProductService productService) {
        this.receiptProductService = receiptProductService;
        this.productService = productService;
    }

    @GetMapping("/list")
    public String showReceiptProductList(Model model) {
        List<ReceiptProduct> receiptProducts = receiptProductService.getAllReceiptProducts();
        model.addAttribute("receiptProducts", receiptProducts);
        return "receipt-products/receipt-product-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("receiptProduct", new ReceiptProduct());
        model.addAttribute("products", productService.getAllProducts());
        return "receipt-products/add-receipt-product";
    }

    @PostMapping("/add")
    public String addReceiptProduct(@Valid @ModelAttribute ReceiptProduct receiptProduct, BindingResult result,
                                    @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (result.hasErrors()) {
            return "receipt-products/add-receipt-product";
        }

        if (!imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            receiptProduct.setImage(imagePath);
        }

        receiptProduct.setCreateDay(LocalDate.now());
        receiptProductService.save(receiptProduct);
        return "redirect:/receiptproducts/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ReceiptProduct receiptProduct = receiptProductService.getReceiptProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid receipt product Id:" + id));
        model.addAttribute("receiptProduct", receiptProduct);
        model.addAttribute("products", productService.getAllProducts());
        return "receipt-products/update-receipt-product";
    }

    @PostMapping("/update/{id}")
    public String updateReceiptProduct(@PathVariable Long id, @Valid @ModelAttribute ReceiptProduct receiptProduct,
                                       BindingResult result, @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (result.hasErrors()) {
            receiptProduct.setId(id); // giữ id trong form
            return "receipt-products/update-receipt-product";
        }

        if (!imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            receiptProduct.setImage(imagePath);
        }

        receiptProductService.save(receiptProduct);
        return "redirect:/receiptproducts/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteReceiptProduct(@PathVariable Long id) {
        receiptProductService.deleteReceiptProductById(id);
        return "redirect:/receiptproducts/list";
    }

    private String saveImage(MultipartFile image) throws IOException {
        String originalFileName = image.getOriginalFilename();
        if (originalFileName != null) {
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            String uniqueFileName = UUID.randomUUID().toString() + extension;
            String imagePath = imageDir + uniqueFileName;
            Files.copy(image.getInputStream(), Paths.get(imagePath), StandardCopyOption.REPLACE_EXISTING);
            return "/images/receiptProducts/" + uniqueFileName;
        }
        throw new IOException("Invalid file name");
    }

    @PostMapping
    public ResponseEntity<ReceiptProduct> createReceiptProduct(@RequestBody ReceiptProduct receiptProduct) {
        ReceiptProduct createdReceiptProduct = receiptProductService.save(receiptProduct);
        return new ResponseEntity<>(createdReceiptProduct, HttpStatus.CREATED);
    }
}
