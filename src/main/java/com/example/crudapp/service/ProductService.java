package com.example.crudapp.service;

import com.example.crudapp.entity.Product;
import com.example.crudapp.exception.ResourceNotFoundException;
import com.example.crudapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product Service class containing business logic.
 * Implements CRUD operations and handles transaction management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Create a new product.
     * @param product the product to create
     * @return the created product
     */
    @Transactional
    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product.getName());
        return productRepository.save(product);
    }

    /**
     * Get all products.
     * @return list of all products
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll();
    }

    /**
     * Get product by ID.
     * @param id the product ID
     * @return the product
     * @throws ResourceNotFoundException if product not found
     */
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    /**
     * Update an existing product.
     * @param id the product ID
     * @param productDetails the updated product details
     * @return the updated product
     * @throws ResourceNotFoundException if product not found
     */
    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        log.info("Updating product with id: {}", id);
        Product existingProduct = getProductById(id);

        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setQuantity(productDetails.getQuantity());

        return productRepository.save(existingProduct);
    }

    /**
     * Delete a product by ID.
     * @param id the product ID
     * @throws ResourceNotFoundException if product not found
     */
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    /**
     * Search products by name.
     * @param name the search term
     * @return list of matching products
     */
    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String name) {
        log.info("Searching products by name: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Check if product exists by ID.
     * @param id the product ID
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean productExists(Long id) {
        return productRepository.existsById(id);
    }

    /**
     * Get total inventory value.
     * @return the total value of all products in stock
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalInventoryValue() {
        log.info("Calculating total inventory value");
        return productRepository.findAll().stream()
                .map(product -> product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
