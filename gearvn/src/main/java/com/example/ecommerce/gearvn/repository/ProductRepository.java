package com.example.ecommerce.gearvn.repository;

import com.example.ecommerce.gearvn.model.Category;
import com.example.ecommerce.gearvn.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryIdAndIdNot(Long categoryId, Long productId);
    List<Product> findByCategoryIdAndBrandId(Long categoryId, Long brandId);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByCategory(Category category);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.brand.id = :brandId")
    List<Product> findByCategoryAndBrand(@Param("categoryId") Long categoryId, @Param("brandId") Long brandId);
    List<Product> findByCategoryName(String categoryName);

    List<Product> findByBrandName(String brandName);

    List<Product> findByCategoryNameAndBrandName(String categoryName, String brandName);
    List<Product> findByBrandIdAndCategoryId(Long brandId, Long categoryId);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByStatus(int status);
    int countByCategory_Name(String categoryName);

}