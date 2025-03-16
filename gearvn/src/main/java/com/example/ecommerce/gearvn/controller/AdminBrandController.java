package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.Brand;
import com.example.ecommerce.gearvn.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/brands")
public class AdminBrandController {

    private final BrandService brandService;

    @Autowired
    public AdminBrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/list")
    public String showBrandList(Model model) {
        List<Brand> brands = brandService.getAllBrands();
        model.addAttribute("brands", brands);
        return "brands/brands-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "brands/add-brand";
    }

    @PostMapping("/add")
    public String addBrand(@Valid @ModelAttribute Brand brand, BindingResult result) {
        if (result.hasErrors()) {
            return "brands/add-brand";
        }
        brandService.addBrand(brand);
        return "redirect:/admin/brands/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Brand brand = brandService.getBrandById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid brand Id:" + id));
        model.addAttribute("brand", brand);
        return "brands/update-brand";
    }

    @PostMapping("/update/{id}")
    public String updateBrand(@PathVariable Long id, @Valid @ModelAttribute Brand brand, BindingResult result) {
        if (result.hasErrors()) {
            brand.setId(id); // giữ id trong form
            return "brands/update-brand";
        }
        brandService.updateBrand(brand);
        return "redirect:/admin/brands/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable Long id) {
        brandService.deleteBrandById(id);
        return "redirect:/admin/brands/list";
    }
}
