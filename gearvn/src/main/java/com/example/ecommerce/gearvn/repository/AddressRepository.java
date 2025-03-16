package com.example.ecommerce.gearvn.repository;

import com.example.ecommerce.gearvn.model.Address;
import com.example.ecommerce.gearvn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
}
