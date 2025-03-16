package com.example.ecommerce.gearvn.service;

import com.example.ecommerce.gearvn.model.Condition;
import com.example.ecommerce.gearvn.repository.ConditionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConditionService {

    private final ConditionRepository conditionRepository;

    @Autowired
    public ConditionService(ConditionRepository conditionRepository) {
        this.conditionRepository = conditionRepository;
    }

    public List<Condition> getAllConditions() {
        return conditionRepository.findAll();
    }

}
