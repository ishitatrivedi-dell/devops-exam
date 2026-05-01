package com.example.crudapp.service;

import com.example.crudapp.entity.Product;
import com.example.crudapp.exception.ResourceNotFoundException;
import com.example.crudapp.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService class.
 * Uses Mockito for mocking dependencies.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setQuantity(10);
    }

    @Test
    @DisplayName("Create Product - Success")
    void createProduct_Success() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product created = productService.createProduct(testProduct);

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    @DisplayName("Get All Products - Success")
    void getAllProducts_Success() {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Second Product");
        product2.setDescription("Another Description");
        product2.setPrice(new BigDecimal("50.00"));
        product2.setQuantity(5);

        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct, product2));

        List<Product> products = productService.getAllProducts();

        assertThat(products).hasSize(2);
        assertThat(products.get(0).getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get Product By ID - Success")
    void getProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Product found = productService.getProductById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getName()).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("Get Product By ID - Not Found")
    void getProductById_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(999L);
        });
    }

    @Test
    @DisplayName("Update Product - Success")
    void updateProduct_Success() {
        Product updatedDetails = new Product();
        updatedDetails.setName("Updated Product");
        updatedDetails.setDescription("Updated Description");
        updatedDetails.setPrice(new BigDecimal("150.00"));
        updatedDetails.setQuantity(20);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product updated = productService.updateProduct(1L, updatedDetails);

        assertThat(updated.getName()).isEqualTo("Updated Product");
        assertThat(updated.getPrice()).isEqualTo(new BigDecimal("150.00"));
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    @DisplayName("Delete Product - Success")
    void deleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(testProduct);

        assertDoesNotThrow(() -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).delete(testProduct);
    }

    @Test
    @DisplayName("Delete Product - Not Found")
    void deleteProduct_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct(999L);
        });
    }

    @Test
    @DisplayName("Search Products By Name - Success")
    void searchProductsByName_Success() {
        when(productRepository.findByNameContainingIgnoreCase("Test"))
                .thenReturn(Arrays.asList(testProduct));

        List<Product> results = productService.searchProductsByName("Test");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).contains("Test");
    }

    @Test
    @DisplayName("Product Exists - True")
    void productExists_True() {
        when(productRepository.existsById(1L)).thenReturn(true);

        assertTrue(productService.productExists(1L));
    }

    @Test
    @DisplayName("Product Exists - False")
    void productExists_False() {
        when(productRepository.existsById(999L)).thenReturn(false);

        assertFalse(productService.productExists(999L));
    }

    @Test
    @DisplayName("Get Total Inventory Value - Success")
    void getTotalInventoryValue_Success() {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Second Product");
        product2.setPrice(new BigDecimal("50.00"));
        product2.setQuantity(5);

        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct, product2));

        BigDecimal totalValue = productService.getTotalInventoryValue();

        // 99.99 * 10 + 50.00 * 5 = 999.90 + 250.00 = 1249.90
        assertThat(totalValue).isEqualTo(new BigDecimal("1249.90"));
    }

    @Test
    @DisplayName("Get Total Inventory Value - Empty List")
    void getTotalInventoryValue_EmptyList() {
        when(productRepository.findAll()).thenReturn(Arrays.asList());

        BigDecimal totalValue = productService.getTotalInventoryValue();

        assertThat(totalValue).isEqualTo(BigDecimal.ZERO);
    }
}
