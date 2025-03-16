package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.Discount;
import com.example.ecommerce.gearvn.service.DiscountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
@RequestMapping("/admin/discounts")
public class AdminDiscountController {

    private final DiscountService discountService;

    @Autowired
    public AdminDiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @GetMapping("/list")
    public String showDiscountList(Model model) {
        List<Discount> discounts = discountService.getAllDiscounts();
        model.addAttribute("discounts", discounts);
        return "discounts/discount-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("discount", new Discount());
        return "discounts/add-discount";
    }

    @PostMapping("/add")
    public String addDiscount(@Valid @ModelAttribute("discount") Discount discount, BindingResult result) {
        if (result.hasErrors()) {
            return "discounts/add-discount";
        }
        discountService.addDiscount(discount);
        return "redirect:/admin/discounts/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Discount discount = discountService.getDiscountById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid discount Id:" + id));
        model.addAttribute("discount", discount);
        return "discounts/update-discount";
    }

    @PostMapping("/update/{id}")
    public String updateDiscount(@PathVariable("id") Long id, @ModelAttribute("discount") Discount discount, BindingResult result) {
        System.out.println("Updating discount with ID: " + id);  // Add this line
        if (result.hasErrors()) {
            return "/update-discount";
        }
        discountService.updateDiscount(id, discount);
        return "redirect:/admin/discounts/list  ";
    }


    @GetMapping("/delete/{id}")
    public String deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscountById(id);
        return "redirect:/admin/discounts/list";
    }
}
