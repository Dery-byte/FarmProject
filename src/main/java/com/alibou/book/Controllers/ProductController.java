package com.alibou.book.Controllers;

import com.alibou.book.DTO.ProductRequestDTO;
import com.alibou.book.Entity.Product;
import com.alibou.book.Services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // ✅ Add Product
//    @PostMapping("/add")
//    public ResponseEntity<Product> addProduct(@RequestBody Product product, Principal principal) {
////       List<Product> products = (List<Product>) productService.addProduct(product,principal);
//        return ResponseEntity.ok(productService.addProduct(product,principal));
//    }

    @PostMapping(value="/add", consumes = "multipart/form-data")
    public ResponseEntity<?> createProduct(
            @ModelAttribute ProductRequestDTO productRequest,
            Principal principal) {
        try {
            Product createdProduct = productService.addProduct(productRequest, principal);
            return ResponseEntity.ok(createdProduct);
        } catch (Exception e) {
            e.printStackTrace(); // Show real error
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }



    // ✅ Delete Product by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully!");
    }

    // ✅ Update Product by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    // ✅ Get all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // ✅ Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());  // Return product if found
        } else {
            Map<String, String> errorResponse = Map.of("message", "Product with ID " + id + " cannot be found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // ✅ Get products by name
    @GetMapping("/search")
    public ResponseEntity<Object> getProductsByName(@RequestParam String name) {
        List<Product> products = productService.getProductsByProductName(name);
        if (products.isEmpty()) {
            Map<String, String> errorResponse = Map.of("message", "No products found with name: " + name);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        return ResponseEntity.ok(products);
    }


    //Get Product Based on Location
    @GetMapping("/search/by-location")
    public ResponseEntity<List<Product>> getProductsByLocation(@RequestParam String location) {
        List<Product> products = productService.getProductsByLocation(location);
        return ResponseEntity.ok(products);
    }

    // Get products by category
    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category);
    }



}
