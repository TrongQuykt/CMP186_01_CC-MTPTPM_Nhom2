package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.Category;
import com.example.ecommerce.gearvn.model.Product;
import com.example.ecommerce.gearvn.repository.CategoryRepository;
import com.example.ecommerce.gearvn.service.BrandService;
import com.example.ecommerce.gearvn.service.CategoryService;
import com.example.ecommerce.gearvn.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final CategoryRepository categoryRepository;
    @Autowired
    public HomeController(ProductService productService, CategoryService categoryService, BrandService brandService, CategoryRepository categoryRepository) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String home(@RequestParam(value = "category", required = false) String category,
                       @RequestParam(value = "brand", required = false) String brand,
                       Model model) {
        List<Product> products;
        if (category != null && !category.isEmpty() && brand != null && !brand.isEmpty()) {
            products = productService.findByCategoryAndBrand(category, brand);
        } else if (category != null && !category.isEmpty()) {
            products = productService.findByCategory(category);
        } else if (brand != null && !brand.isEmpty()) {
            products = productService.findByBrand(brand);
        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());
        return "home/index";
    }
}
