package com.alibou.book.Controllers;

import com.alibou.book.DTO.*;
import com.alibou.book.Entity.Product;
import com.alibou.book.Repositories.ProductRepository;
import com.alibou.book.Services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("https://farm-front-end-eta.vercel.app/")
@RequestMapping("/auth/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;

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
            e.printStackTrace();
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
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @ModelAttribute ProductUpdateRequest productUpdateRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        Product updatedProduct = productService.updateProduct(id, productUpdateRequest, images);
        return ResponseEntity.ok(updatedProduct);
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








    @GetMapping("/location/{location}")
    public ResponseEntity<List<Product>> getProductsByFarmLocation(@PathVariable String location) {
        List<Product> products = productService.getProductsByFarmLocation(location);
        return ResponseEntity.ok(products);
    }

    // Optional endpoint for available products only
    @GetMapping("/location/{location}/available/products")
    public ResponseEntity<List<Product>> getAvailableProductsByFarmLocation(@PathVariable String location) {
        List<Product> products = productService.getAvailableProductsByFarmLocation(location);
        return ResponseEntity.ok(products);
    }













//    PRICING CONTROLLER
@GetMapping("/price-ranges")
public ResponseEntity<PriceRangeInfo> getPriceRanges() {
    return ResponseEntity.ok(productService.getDynamicPriceRanges());
}

    @GetMapping("/filter")
    public ResponseEntity<List<Product>> filterProducts(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        ResponseEntity<List<Product>> products = productService.getProductsByPriceRange(minPrice,maxPrice);
        return ResponseEntity.ok(products.getBody());




    }


    // GET: /api/products/latest
    @GetMapping("/latest")
    public List<LatestProductDTO> getLatestProducts() {
        return productService.getLatestProducts();
    }

    // GET: /api/products?page=0&size=6
    @GetMapping("/latest/pagination")
    public Page<LatestProductDTO> getProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getProductsPaginated(page, size);
    }


}
