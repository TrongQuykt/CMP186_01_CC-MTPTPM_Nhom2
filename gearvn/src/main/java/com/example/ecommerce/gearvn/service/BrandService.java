package com.example.ecommerce.gearvn.service;

import com.example.ecommerce.gearvn.model.Brand;
import com.example.ecommerce.gearvn.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    @Autowired
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Optional<Brand> getBrandById(Long id) {
        return brandRepository.findById(id);
    }
    public Brand findByName(String name) {
        return brandRepository.findByName(name);
    }
    public Brand addBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    public Brand updateBrand(Brand brand) {
        Brand existingBrand = brandRepository.findById(brand.getId())
                .orElseThrow(() -> new IllegalStateException("Brand with ID " +
                        brand.getId() + " does not exist."));
        existingBrand.setName(brand.getName());
        existingBrand.setDescription(brand.getDescription());
        return brandRepository.save(existingBrand);
    }

    public void deleteBrandById(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new IllegalStateException("Brand with ID " + id + " does not exist.");
        }
        brandRepository.deleteById(id);
    }
    public long countBrands() {
        return brandRepository.count();
    }
}
