package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.Brand;
import com.example.ecommerce.gearvn.model.Category;
import com.example.ecommerce.gearvn.model.Product;
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
@RequestMapping("/collections")
public class CollectionController
{
    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    @Autowired
    public CollectionController(ProductService productService, CategoryService categoryService, BrandService brandService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
    }
    @GetMapping("/{categoryName}")
    public String getProductsByCategory(@PathVariable("categoryName") String categoryName,
                                        @RequestParam(value = "brandName", required = false) String brandName, Model model) {
        Category category = categoryService.findByName(categoryName);
        if (category == null) {
            return "error/404";  // Trả về trang lỗi nếu category không tồn tại
        }

        List<Product> products;
        Brand selectedBrand = null;
        if (brandName != null) {
            selectedBrand = brandService.findByName(brandName);
            if (selectedBrand != null) {
                products = productService.getProductsByCategoryAndBrand(category.getId(), selectedBrand.getId());
            } else {
                products = productService.getProductsByCategory(category.getId());
            }
        } else {
            products = productService.getProductsByCategory(category.getId());
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("category", category);
        model.addAttribute("selectedBrand", selectedBrand);
        model.addAttribute("categoryName", categoryName);
        return "collections/view";  // Đường dẫn view trong thư mục collections
    }


}
