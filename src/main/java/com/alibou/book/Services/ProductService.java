package com.alibou.book.Services;

import com.alibou.book.DTO.PriceRangeInfo;
import com.alibou.book.DTO.ProductRequestDTO;
import com.alibou.book.DTO.ProductResponsepPriceRange;
import com.alibou.book.DTO.ProductUpdateRequest;
import com.alibou.book.Entity.Farm;
import com.alibou.book.Entity.OrderDetails;
import com.alibou.book.Entity.Product;
import com.alibou.book.Repositories.FarmRepository;
import com.alibou.book.Repositories.OrderDetailsRepository;
import com.alibou.book.Repositories.ProductRepository;
import com.alibou.book.config.PriceRangeConfig;
import com.alibou.book.file.FileStorageService;
import com.alibou.book.file.FileStorageServices;
import com.alibou.book.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserDetailsService userDetailsService;
    private final FarmRepository farmRepository;
    private final OrderDetailsRepository orderDetailsRepository;
private final PriceRangeConfig priceRangeConfig;
private final FileStorageServices fileStorageServices;







    @Value("${application.file.upload-dir}")
    private String uploadDir;

//    public Product addProduct(ProductRequestDTO productRequest, Principal principal) throws IOException {
//        // Authentication check
//        if (principal == null) {
//            throw new IllegalArgumentException("User must be authenticated to add a product.");
//        }
//
//        // Load user and validate farm
//        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
//        Long farmId = productRequest.getFarmId();
//        if (farmId == null) {
//            throw new IllegalArgumentException("Farm ID must be provided.");
//        }
//        Farm farm = farmRepository.findById(farmId)
//                .orElseThrow(() -> new IllegalArgumentException("Farm not found with ID: " + farmId));
//
//        // Validate and process image
//        MultipartFile imageFile = productRequest.getImage();
//        if (imageFile == null || imageFile.isEmpty()) {
//            throw new IllegalArgumentException("Product image is required.");
//        }
//
//        // Create upload directory if it doesn't exist
//        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
//        try {
//            Files.createDirectories(uploadPath);
//        } catch (IOException e) {
//            throw new RuntimeException("Could not create the upload directory.", e);
//        }
//
//        // Generate unique filename
//        String originalFilename = imageFile.getOriginalFilename();
//        String fileExtension = originalFilename != null
//                ? originalFilename.substring(originalFilename.lastIndexOf("."))
//                : "";
////        String fileName = UUID.randomUUID() + "_" + System.currentTimeMillis() + fileExtension;
//                String fileName = UUID.randomUUID() + "_" + productRequest.getImage().getOriginalFilename();
//
//
//        // Save file with error handling
//        Path targetLocation = uploadPath.resolve(fileName);
//        try {
//            Files.copy(imageFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException ex) {
//            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
//        }
//
//        // Create and save product
//        Product product = new Product();
//        product.setProductName(productRequest.getProductName());
//        product.setDescription(productRequest.getDescription());
//        product.setPrice(productRequest.getPrice());
//        product.setQuantity(productRequest.getQuantity());
//        product.setCategory(productRequest.getCategory());
//        product.setFarm(farm);
//        product.setFarmer(user);
//        product.setImageUrl("/uploads/" + fileName);
//        return productRepository.save(product);
//    }



    public Product addProduct(ProductRequestDTO productRequest, Principal principal) throws IOException {
        // Authentication check
        if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to add a product.");
        }

        // Load user and validate farm
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Long farmId = productRequest.getFarmId();
        if (farmId == null) {
            throw new IllegalArgumentException("Farm ID must be provided.");
        }
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new IllegalArgumentException("Farm not found with ID: " + farmId));

        // Validate and process images
        List<MultipartFile> imageFiles = productRequest.getImages(); // 👈 now multiple images
        if (imageFiles == null || imageFiles.isEmpty()) {
            throw new IllegalArgumentException("At least one product image is required.");
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create the upload directory.", e);
        }

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile imageFile : imageFiles) {
            if (imageFile.isEmpty()) continue; // Skip empty files

            // Generate unique filename
            String originalFilename = imageFile.getOriginalFilename();
            String fileExtension = originalFilename != null
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String fileName = UUID.randomUUID() + "_" + (originalFilename != null ? originalFilename : "image") + fileExtension;

            // Save file
            Path targetLocation = uploadPath.resolve(fileName);
            try {
                Files.copy(imageFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
            }

            // Add URL to list
            imageUrls.add("/uploads/" + fileName);
        }

        // Create and save product
        Product product = new Product();
        product.setProductName(productRequest.getProductName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        product.setCategory(productRequest.getCategory());


        product.setWeight(productRequest.getWeight());
        product.setBreed(productRequest.getBreed());
        product.setHealthStatus(productRequest.getHealthStatus());
        product.setCondition(productRequest.getCondition());
        product.setGender(productRequest.getGender());
        product.setAge(productRequest.getAge());


        product.setFarm(farm);
        product.setFarmer(user);
        product.setImageUrls(imageUrls); // 👈 SET the List of image URLs

        return productRepository.save(product);
    }


    // 🔹 Delete Product by ID
    public void deleteProduct(Long id) {
        List<OrderDetails> orderDetails = orderDetailsRepository.findByProductId(id);
        orderDetails.forEach(od -> {
            od.setProduct(null); // This requires setProduct() method in OrderDetails
            orderDetailsRepository.save(od); // Save the change
        });

        productRepository.deleteById(id);
    }



    // 🔹 Update Product by ID
    public Product updateProduct(Long id, ProductUpdateRequest newProduct, List<MultipartFile> newImages) {
        return productRepository.findById(id).map(product -> {
            // Update basic fields
            product.setProductName(newProduct.getProductName());
            product.setDescription(newProduct.getDescription());
            product.setPrice(newProduct.getPrice());
            product.setQuantity(newProduct.getQuantity());
            product.setCategory(newProduct.getCategory());
            product.setAge(newProduct.getAge());
            product.setWeight(newProduct.getWeight());
            product.setBreed(newProduct.getBreed());
            product.setHealthStatus(newProduct.getHealthStatus());
            product.setCondition(newProduct.getCondition());

            // Handle images
            List<String> existingImageUrls = newProduct.getExistingImageUrls() != null ?
                    newProduct.getExistingImageUrls() : new ArrayList<>();

            List<String> newImageUrls = new ArrayList<>();

            if (newImages != null) {
                for (MultipartFile file : newImages) {
                    if (!file.isEmpty()) {
                        try {
                            String imageUrl = fileStorageServices.storeFile(file);
                            newImageUrls.add(imageUrl);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to store file", e);
                        }
                    }
                }
            }

            // Combine existing and new image URLs
            List<String> allImageUrls = new ArrayList<>();
            allImageUrls.addAll(existingImageUrls);
            allImageUrls.addAll(newImageUrls);

            product.setImageUrls(allImageUrls);

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


    public  List<Product> getProductsByCategory(String location){
        return productRepository.findByCategory(location);
    }




















    public List<Product> getProductsByFarmLocation(String location) {
        return productRepository.findByFarmLocation(location);
    }

    // Optional: Add validation or additional business logic here
    public List<Product> getAvailableProductsByFarmLocation(String location) {
        return productRepository.findByFarmLocation(location).stream()
                .filter(product -> product.getQuantity() > 0) // assuming Product has a quantity field
                .toList();
    }




























































//    PRICING SERVICE

    public PriceRangeInfo getDynamicPriceRanges() {
        Double minPrice = productRepository.findMinPrice();
        Double maxPrice = productRepository.findMaxPrice();

        List<PriceRangeInfo.PriceRange> ranges = new ArrayList<>();

        // Add "All" option
        ranges.add(createRange(null, null, "All"));

        if (minPrice != null && maxPrice != null) {
            double step = priceRangeConfig.getStepSize();
            double current = 0.0;
            double maxBeforePlus = priceRangeConfig.getMaxBeforePlus();

            // Generate ranges up to maxBeforePlus
            while (current < maxBeforePlus) {
                double rangeEnd = current + step;
                String label = String.format("GH₵%.2f - GH₵%.2f", current, rangeEnd);
                ranges.add(createRange(current, rangeEnd, label));
                current = rangeEnd;
            }

            // Add the "+" range
            ranges.add(createRange(current, null, String.format("GH₵%.2f+", current)));
        }

        return new PriceRangeInfo(minPrice, maxPrice, ranges);
    }

    public ResponseEntity<List<Product>>  getProductsByPriceRange(Double minPrice, Double maxPrice) {
        List<Product> products;

        // Case 1: No filters - return all products
        if (minPrice == null && maxPrice == null) {
            products = productRepository.findAllProducts();
        }
        // Case 2: Only minPrice specified - return products >= minPrice
        else if (maxPrice == null) {
            products = productRepository.findByPriceGreaterThanEqual(minPrice);
        }
        // Case 3: Only maxPrice specified - return products <= maxPrice
        else if (minPrice == null) {
            products = productRepository.findByPriceLessThanEqual(maxPrice);
        }
        // Case 4: Both min and max specified - return products between
        else {
            products = productRepository.findByPriceBetween(minPrice, maxPrice);
        }
        return ResponseEntity.ok(products);
    }
    private PriceRangeInfo.PriceRange createRange(Double min, Double max, String label) {
        Long count;
        if (min == null && max == null) {
            count = productRepository.countAllProducts();
        } else if (max == null) {
            count = productRepository.countByPriceGreaterThanEqual(min);
        } else {
            count = productRepository.countByPriceBetween(min, max);
        }
        return new PriceRangeInfo.PriceRange(min, max, count, label);
    }









}
