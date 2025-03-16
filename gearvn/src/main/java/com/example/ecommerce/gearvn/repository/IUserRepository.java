package com.example.ecommerce.gearvn.repository;

import com.example.ecommerce.gearvn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :role")
    List<User> findByRole(@Param("role") String role);
    Optional<User> findById(Long id);
    List<User> findByRolesId(int roleId);
    int countByRolesIdIn(List<Integer> roleIds);
    List<User> findByRolesNameIn(Set<String> roles);
    Optional<User> findByPhone(String phone);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String token);
}