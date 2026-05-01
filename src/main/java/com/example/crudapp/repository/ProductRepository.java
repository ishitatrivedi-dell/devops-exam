package com.example.crudapp.repository;

import com.example.crudapp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Product Repository interface for database operations.
 * Extends JpaRepository to provide CRUD operations and custom queries.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find products by name containing the search string (case-insensitive).
     * @param name the search string
     * @return list of matching products
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Check if a product with the given name exists.
     * @param name the product name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);
}
