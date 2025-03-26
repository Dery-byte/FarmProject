package com.alibou.book.Controllers;

import com.alibou.book.Entity.Farm;
import com.alibou.book.Entity.Product;
import com.alibou.book.Repositories.FarmRepository;
import com.alibou.book.Repositories.ProductRepository;
import com.alibou.book.Services.ProductService;
import com.alibou.book.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final ProductRepository productRepository;
    private final UserDetailsService userDetailsService;
    private final FarmRepository farmRepository;


    // ✅ Add Product
    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product product, Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body(product);
        }
        User user = (User) this.userDetailsService.loadUserByUsername(principal.getName());
        // Ensure farmId is provided in the request body
        if (product.getFarm() == null || product.getFarm().getFarm_id() == null) {
            return ResponseEntity.badRequest().body(product);
        }
        // Fetch the farm entity from the database
        Optional<Farm> farmOptional = farmRepository.findById(product.getFarm().getFarm_id());
        if (farmOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(product);
        }
        Farm farm = farmOptional.get();
        // Set the fetched farm and user
        product.setFarm(farm);
        product.setFarmer(user);
        // Save the product
        productRepository.save(product);
        return ResponseEntity.ok(product);
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


}
