package com.alibou.book.Services;

import com.alibou.book.DTO.ProductRequestDTO;
import com.alibou.book.Entity.Farm;
import com.alibou.book.Entity.Product;
import com.alibou.book.Repositories.FarmRepository;
import com.alibou.book.Repositories.ProductRepository;
import com.alibou.book.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserDetailsService userDetailsService;
    private final FarmRepository farmRepository;

    // 🔹 Add Product
//    public Product addProduct(Product product, Principal principal) {
//        if (principal == null) {
//            throw new IllegalArgumentException("User must be authenticated to add a product.");
//        }
//        // Load the user
//        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
//
//        // Validate farm information
//        Long farmId = product.getFarm() != null ? product.getFarm().getFarm_id() : null;
//        if (farmId == null) {
//            throw new IllegalArgumentException("Farm ID must be provided.");
//        }
//        // Retrieve the farm from the database
//        Farm farm = farmRepository.findById(farmId)
//                .orElseThrow(() -> new IllegalArgumentException("Farm not found with ID: " + farmId));
//        // Assign relationships
//        product.setFarm(farm);
//        product.setFarmer(user);
//        // Save and return the product
//        return productRepository.save(product);
//    }


    public Product addProduct(ProductRequestDTO productRequest, Principal principal) throws IOException {
        if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to add a product.");
        }
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Long farmId = productRequest.getFarmId();
        if (farmId == null) {
            throw new IllegalArgumentException("Farm ID must be provided.");
        }
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new IllegalArgumentException("Farm not found with ID: " + farmId));
        // 🖼️ Handle image upload (you can change path/storage as needed)
        String uploadDir = new File("src/main/resources/static/uploads/").getAbsolutePath();
        String fileName = UUID.randomUUID() + "_" + productRequest.getImage().getOriginalFilename();
        File destination = new File(uploadDir, fileName);
// Make sure the directory exists
        destination.getParentFile().mkdirs();
        productRequest.getImage().transferTo(destination);
        // Construct the Product object
        Product product = new Product();
        product.setProductName(productRequest.getProductName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        product.setCategory(productRequest.getCategory());
        product.setFarm(farm);
        product.setFarmer(user);
        product.setImageUrl("/uploads/" + fileName); // 👈 This will be accessible in browser
        return productRepository.save(product);
    }


    // 🔹 Delete Product by ID
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    // 🔹 Update Product by ID
    public Product updateProduct(Long id, Product newProduct) {
        return productRepository.findById(id).map(product -> {
            product.setProductName(newProduct.getProductName());
            product.setDescription(newProduct.getDescription());
            product.setPrice(newProduct.getPrice());
            product.setQuantity(newProduct.getQuantity());
            product.setCategory(newProduct.getCategory());
            return productRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Product not found!"));
    }


    // ✅ Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    // ✅ Get product by ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    // ✅ Get products by name
    public List<Product> getProductsByProductName(String name) {
        return productRepository.findByProductNameContainingIgnoreCase(name);
    }

    //GET PROCUCTS BASED ON LOCATION

    public  List<Product> getProductsByLocation(String location){
        return productRepository.findByFarm_LocationContainingIgnoreCase(location);
    }
}
