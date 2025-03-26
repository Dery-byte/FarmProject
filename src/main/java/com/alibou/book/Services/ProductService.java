package com.alibou.book.Services;

import com.alibou.book.Entity.Product;
import com.alibou.book.Repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    // 🔹 Add Product
    public Product addProduct(Product product) {
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
