package com.example.ecommerce.gearvn.service;

import com.example.ecommerce.gearvn.model.Warranty;
import com.example.ecommerce.gearvn.repository.WarrantyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarrantyService {

    private final WarrantyRepository warrantyRepository;

    @Autowired
    public WarrantyService(WarrantyRepository warrantyRepository) {
        this.warrantyRepository = warrantyRepository;
    }

    public List<Warranty> getAllWarranties() {
        return warrantyRepository.findAll();
    }
}
