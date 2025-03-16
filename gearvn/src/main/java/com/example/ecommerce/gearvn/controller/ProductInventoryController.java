package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.ProductInventory;
import com.example.ecommerce.gearvn.service.ProductInventoryService;
import com.example.ecommerce.gearvn.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/product-inventories")
public class ProductInventoryController {

    private final ProductInventoryService productInventoryService;
    private final ProductService productService;

    @Autowired
    public ProductInventoryController(ProductInventoryService productInventoryService, ProductService productService) {
        this.productInventoryService = productInventoryService;
        this.productService = productService;
    }

    @GetMapping
    public String showProductInventoryList(Model model) {
        List<ProductInventory> productInventories = productInventoryService.getAllProductInventories();
        model.addAttribute("productInventories", productInventories);
        return "product-inventories/product-inventory-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("productInventory", new ProductInventory());
        model.addAttribute("products", productService.getAllProducts());
        return "product-inventories/add-product-inventory";
    }

    @PostMapping("/add")
    public String addProductInventory(@Valid @ModelAttribute ProductInventory productInventory, BindingResult result) {
        if (result.hasErrors()) {
            return "product-inventories/add-product-inventory";
        }
        productInventoryService.addProductInventory(productInventory);
        return "redirect:/product-inventories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProductInventory productInventory = productInventoryService.getProductInventoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product inventory Id:" + id));
        model.addAttribute("productInventory", productInventory);
        model.addAttribute("products", productService.getAllProducts());
        return "product-inventories/update-product-inventory";
    }

    @PostMapping("/update/{id}")
    public String updateProductInventory(@PathVariable Long id, @Valid @ModelAttribute ProductInventory productInventory, BindingResult result) {
        if (result.hasErrors()) {
            productInventory.setId(id); // giữ id trong form
            return "product-inventories/update-product-inventory";
        }
        productInventoryService.updateProductInventory(productInventory);
        return "redirect:/product-inventories";
    }

    @GetMapping("/delete/{id}")
    public String deleteProductInventory(@PathVariable Long id) {
        productInventoryService.deleteProductInventoryById(id);
        return "redirect:/product-inventories";
    }
}
