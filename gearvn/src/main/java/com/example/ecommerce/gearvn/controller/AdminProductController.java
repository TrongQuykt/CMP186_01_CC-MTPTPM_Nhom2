package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.Product;
import com.example.ecommerce.gearvn.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ConditionService conditionService;
    private final WarrantyService warrantyService;

    @Autowired
    public AdminProductController(ProductService productService, CategoryService categoryService, BrandService brandService, ConditionService conditionService, WarrantyService warrantyService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.conditionService = conditionService;
        this.warrantyService = warrantyService;
    }

    @GetMapping("/list")
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/products/products-list";
    }
    @GetMapping("/product-detail/{id}")
    public String showProductDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        return "/products/product-detail";
    }


    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("conditions", conditionService.getAllConditions());
        model.addAttribute("warranties", warrantyService.getAllWarranties());
        return "/products/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult result,
                             @RequestParam("image") MultipartFile image,
                             @RequestParam("image1") MultipartFile image1,
                             @RequestParam("image2") MultipartFile image2,
                             @RequestParam("image3") MultipartFile image3) throws IOException {
        if (result.hasErrors()) {
            return "/products/add-product";
        }
        productService.addProduct(product, image, image1, image2, image3);
        return "redirect:/admin/products/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("conditions", conditionService.getAllConditions());
        model.addAttribute("warranties", warrantyService.getAllWarranties());
        return "/products/update-product";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product, BindingResult result,
                                @RequestParam("image") MultipartFile image,
                                @RequestParam("image1") MultipartFile image1,
                                @RequestParam("image2") MultipartFile image2,
                                @RequestParam("image3") MultipartFile image3) throws IOException {
        if (result.hasErrors()) {
            product.setId(id); // set ID to retain in form if there are errors
            return "/products/update-product";
        }
        Product existingProduct = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setCondition(product.getCondition());
        existingProduct.setWarranty(product.getWarranty());
        if (image != null && !image.isEmpty()) {
            String imagePath = productService.saveImage(image);
            existingProduct.setURLImage(imagePath);
        }
        if (image1 != null && !image1.isEmpty()) {
            String imagePath1 = productService.saveImage(image1);
            existingProduct.setURLImage1(imagePath1);
        }
        if (image2 != null && !image2.isEmpty()) {
            String imagePath2 = productService.saveImage(image2);
            existingProduct.setURLImage2(imagePath2);
        }
        if (image3 != null && !image3.isEmpty()) {
            String imagePath3 = productService.saveImage(image3);
            existingProduct.setURLImage3(imagePath3);
        }
        productService.updateProduct(existingProduct);
        return "redirect:/admin/products/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/admin/products/list";
    }
}
