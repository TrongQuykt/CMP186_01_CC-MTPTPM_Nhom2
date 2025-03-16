package com.example.ecommerce.gearvn.repository;

import com.example.ecommerce.gearvn.model.Order;
import com.example.ecommerce.gearvn.model.Shipment;
import com.example.ecommerce.gearvn.model.ShipmentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentMethodRepository extends JpaRepository<ShipmentMethod, Long> {
}
