package com.alibou.book.Services;

import com.alibou.book.DTO.*;
import com.alibou.book.Entity.Category;
import com.alibou.book.Entity.Farm;
import com.alibou.book.Entity.OrderDetails;
import com.alibou.book.Entity.Product;
import com.alibou.book.Mapper.ProductMapper;
import com.alibou.book.Repositories.CategoryRepository;
import com.alibou.book.Repositories.FarmRepository;
import com.alibou.book.Repositories.OrderDetailsRepository;
import com.alibou.book.Repositories.ProductRepository;
import com.alibou.book.config.PriceRangeConfig;
import com.alibou.book.file.FileStorageService;
import com.alibou.book.file.FileStorageServices;
import com.alibou.book.user.User;
//import com.azure.storage.blob.BlobClient;
//import com.azure.storage.blob.BlobContainerClient;
//import com.azure.storage.blob.BlobContainerClientBuilder;
//import com.azure.storage.blob.BlobServiceClient;
//import com.azure.storage.blob.models.PublicAccessType;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserDetailsService userDetailsService;
    private final FarmRepository farmRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final PriceRangeConfig priceRangeConfig;
    private final FileStorageServices fileStorageServices;
   // private final BlobServiceClient blobServiceClient; // Injected Azure client
    private final Cloudinary cloudinary;

    private final String containerName = "product-images"; // Your container name
    private final CategoryRepository categoryRepository;


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
//        // Validate and process images
//        List<MultipartFile> imageFiles = productRequest.getImages(); // 👈 now multiple images
//        if (imageFiles == null || imageFiles.isEmpty()) {
//            throw new IllegalArgumentException("At least one product image is required.");
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
//        List<String> imageUrls = new ArrayList<>();
//
//        for (MultipartFile imageFile : imageFiles) {
//            if (imageFile.isEmpty()) continue; // Skip empty files
//
//            // Generate unique filename
//            String originalFilename = imageFile.getOriginalFilename();
//            String fileExtension = originalFilename != null
//                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
//                    : "";
//            String fileName = UUID.randomUUID() + "_" + (originalFilename != null ? originalFilename : "image") + fileExtension;
//
//            // Save file
//            Path targetLocation = uploadPath.resolve(fileName);
//            try {
//                Files.copy(imageFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//            } catch (IOException ex) {
//                throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
//            }
//
//            // Add URL to list
//            imageUrls.add("/uploads/" + fileName);
//        }
//
//        // Create and save product
//        Product product = new Product();
//        product.setProductName(productRequest.getProductName());
//        product.setDescription(productRequest.getDescription());
//        product.setPrice(productRequest.getPrice());
//        product.setQuantity(productRequest.getQuantity());
//        product.setCategory(productRequest.getCategory());
//
//
//        product.setWeight(productRequest.getWeight());
//        product.setBreed(productRequest.getBreed());
//        product.setHealthStatus(productRequest.getHealthStatus());
//        product.setCondition(productRequest.getCondition());
//        product.setGender(productRequest.getGender());
//        product.setAge(productRequest.getAge());
//
//
//        product.setFarm(farm);
//        product.setFarmer(user);
//        product.setImageUrls(imageUrls); // 👈 SET the List of image URLs
//
//        return productRepository.save(product);
//    }
//
//    @Transactional
//    public Product addProduct(ProductRequestDTO productRequest, Principal principal) throws IOException {
//        validateRequest(productRequest, principal);
//
//        User user = getAuthenticatedUser(principal);
//        Farm farm = getFarm(productRequest.getFarmId());
//
//        List<String> imageUrls = uploadImages(productRequest.getImages());
//
//        try {
//            Product product = createProduct(productRequest, farm, user, imageUrls);
//            return productRepository.save(product);
//        } catch (Exception e) {
//            // Clean up uploaded images if product creation fails
//            deleteUploadedImages(imageUrls);
//            throw e;
//        }
//    }
//
//    private void validateRequest(ProductRequestDTO productRequest, Principal principal) {
//        if (principal == null) {
//            throw new IllegalArgumentException("User must be authenticated to add a product.");
//        }
//        if (productRequest.getFarmId() == null) {
//            throw new IllegalArgumentException("Farm ID must be provided.");
//        }
//        if (productRequest.getImages() == null || productRequest.getImages().isEmpty()) {
//            throw new IllegalArgumentException("At least one product image is required.");
//        }
//    }
//
//    private User getAuthenticatedUser(Principal principal) {
//        return (User) userDetailsService.loadUserByUsername(principal.getName());
//    }
//
//    private Farm getFarm(Long farmId) {
//        return farmRepository.findById(farmId)
//                .orElseThrow(() -> new IllegalArgumentException("Farm not found with ID: " + farmId));
//    }
//
//    private List<String> uploadImages(List<MultipartFile> imageFiles) throws IOException {
//        BlobContainerClient containerClient = getOrCreateContainer();
//        List<String> imageUrls = new ArrayList<>();
//        List<String> uploadedFileNames = new ArrayList<>();
//
//        try {
//            for (MultipartFile imageFile : imageFiles) {
//                if (imageFile.isEmpty()) continue;
//
//                String fileName = generateUniqueFileName(imageFile);
//                String blobUrl = uploadFileToBlobStorage(containerClient, imageFile, fileName);
//
//                imageUrls.add(blobUrl);
//                uploadedFileNames.add(fileName);
//            }
//            return imageUrls;
//        } catch (Exception e) {
//            // Clean up any successfully uploaded files if one fails
//            uploadedFileNames.forEach(name -> containerClient.getBlobClient(name).deleteIfExists());
//            throw e;
//        }
//    }
//
//    private BlobContainerClient getOrCreateContainer() {
//        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
//
//        if (!containerClient.exists()) {
//            containerClient.create();
//            containerClient.setAccessPolicy(PublicAccessType.BLOB, null);
//        }
//
//        return containerClient;
//    }
//
//    private String generateUniqueFileName(MultipartFile file) {
//        String originalFilename = file.getOriginalFilename();
//        String fileExtension = originalFilename != null ?
//                originalFilename.substring(originalFilename.lastIndexOf('.')) : ".jpg";
//        return "product_" + UUID.randomUUID() + fileExtension;
//    }
//
//    private String uploadFileToBlobStorage(BlobContainerClient containerClient,
//                                           MultipartFile file,
//                                           String fileName) throws IOException {
//        BlobClient blobClient = containerClient.getBlobClient(fileName);
//
//        try (InputStream inputStream = file.getInputStream()) {
//            blobClient.upload(inputStream, file.getSize(), true);
//
//            // Verify upload was successful
//            if (!blobClient.exists()) {
//                throw new IOException("Failed to verify uploaded file: " + fileName);
//            }
//
//            return blobClient.getBlobUrl();
//        }
//    }
//
//    private Product createProduct(ProductRequestDTO request,
//                                  Farm farm,
//                                  User user,
//                                  List<String> imageUrls) {
//        return Product.builder()
//                .productName(request.getProductName())
//                .description(request.getDescription())
//                .price(request.getPrice())
//                .quantity(request.getQuantity())
//                .category(request.getCategory())
//                .weight(request.getWeight())
//                .breed(request.getBreed())
//                .healthStatus(request.getHealthStatus())
//                .condition(request.getCondition())
//                .gender(request.getGender())
//                .age(request.getAge())
//                .farm(farm)
//                .farmer(user)
//                .imageUrls(imageUrls)
//                .build();
//    }
//
//    private void deleteUploadedImages(List<String> imageUrls) {
//        if (imageUrls == null || imageUrls.isEmpty()) return;
//
//        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
//
//        imageUrls.forEach(url -> {
//            try {
//                String blobName = url.substring(url.lastIndexOf('/') + 1);
//                containerClient.getBlobClient(blobName).deleteIfExists();
//            } catch (Exception e) {
//                // Log deletion failure but don't throw
//                System.err.println("Failed to delete blob: " + url);
//            }
//        });
//    }
//
//
//    // 🔹 Delete Product by ID
//    public void deleteProduct(Long id) {
//        List<OrderDetails> orderDetails = orderDetailsRepository.findByProductId(id);
//        orderDetails.forEach(od -> {
//            od.setProduct(null); // This requires setProduct() method in OrderDetails
//            orderDetailsRepository.save(od); // Save the change
//        });
//
//        productRepository.deleteById(id);
//    }







//    CLOUDINARY SETUP
@Transactional
public Product addProduct(ProductRequestDTO productRequest, Principal principal) throws IOException {
    validateRequest(productRequest, principal);

    User user = getAuthenticatedUser(principal);
    Farm farm = getFarm(productRequest.getFarmId());

    Category category = getCategory(productRequest.getCategoryId());

    List<String> imageUrls = uploadImages(productRequest.getImages());

    try {
        Product product = createProduct(productRequest, farm, category, user, imageUrls);
        return productRepository.save(product);
    } catch (Exception e) {
        // Clean up uploaded images if product creation fails
        deleteUploadedImages(imageUrls);
        throw e;
    }
}

    private void validateRequest(ProductRequestDTO productRequest, Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to add a product.");
        }
        if (productRequest.getFarmId() == null) {
            throw new IllegalArgumentException("Farm ID must be provided.");
        }
        if (productRequest.getImages() == null || productRequest.getImages().isEmpty()) {
            throw new IllegalArgumentException("At least one product image is required.");
        }
    }

    private User getAuthenticatedUser(Principal principal) {
        return (User) userDetailsService.loadUserByUsername(principal.getName());
    }

    private Farm getFarm(Long farmId) {
        return farmRepository.findById(farmId)
                .orElseThrow(() -> new IllegalArgumentException("Farm not found with ID: " + farmId));
    }


    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Farm not found with ID: " + categoryId));
    }

    // 🔹 MODIFIED: Upload images to Cloudinary instead of Azure Blob
    private List<String> uploadImages(List<MultipartFile> imageFiles) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        List<String> uploadedPublicIds = new ArrayList<>();

        try {
            for (MultipartFile imageFile : imageFiles) {
                if (imageFile.isEmpty()) continue;

                String publicId = generateUniquePublicId();
                String imageUrl = uploadFileToCloudinary(imageFile, publicId);

                imageUrls.add(imageUrl);
                uploadedPublicIds.add(publicId);
            }
            return imageUrls;
        } catch (Exception e) {
            // Clean up any successfully uploaded files if one fails
            deleteCloudinaryImages(uploadedPublicIds);
            throw e;
        }
    }

    // 🔹 NEW: Generate unique public ID for Cloudinary
    private String generateUniquePublicId() {
        return "products/product_" + UUID.randomUUID().toString();
    }

    // 🔹 NEW: Upload single file to Cloudinary
    private String uploadFileToCloudinary(MultipartFile file, String publicId) throws IOException {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "folder", "products", // Optional: organize in folders
                            "resource_type", "image",
                            "quality", "auto", // Automatic quality optimization
                            "fetch_format", "auto" // Automatic format optimization
                    )
            );

            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            throw new IOException("Failed to upload image to Cloudinary: " + e.getMessage(), e);
        }
    }

    private Product createProduct(ProductRequestDTO request,
                                  Farm farm,
                                  Category category,
                                  User user,
                                  List<String> imageUrls) {
        return Product.builder()
                .productName(request.getProductName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
//                .category(request.getCategory().getCategoryName())
                .category(category)
                .weight(request.getWeight())
                .breed(request.getBreed())
                .healthStatus(request.getHealthStatus())
                .condition(request.getCondition())
                .gender(request.getGender())
                .age(request.getAge())
                .farm(farm)
                .farmer(user)
                .imageUrls(imageUrls)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 🔹 MODIFIED: Delete images from Cloudinary instead of Azure Blob
    private void deleteUploadedImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;

        List<String> publicIds = extractPublicIdsFromUrls(imageUrls);
        deleteCloudinaryImages(publicIds);
    }

    // 🔹 NEW: Delete images from Cloudinary using public IDs
    private void deleteCloudinaryImages(List<String> publicIds) {
        publicIds.forEach(publicId -> {
            try {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (Exception e) {
                // Log deletion failure but don't throw
                System.err.println("Failed to delete Cloudinary image: " + publicId + " - " + e.getMessage());
            }
        });
    }

    // 🔹 NEW: Extract public IDs from Cloudinary URLs
    private List<String> extractPublicIdsFromUrls(List<String> imageUrls) {
        return imageUrls.stream()
                .map(this::extractPublicIdFromUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 🔹 NEW: Extract public ID from a Cloudinary URL
    private String extractPublicIdFromUrl(String url) {
        try {
            // Cloudinary URL format: https://res.cloudinary.com/{cloud_name}/image/upload/v{version}/{public_id}.{format}
            String[] parts = url.split("/");
            if (parts.length >= 2) {
                String lastPart = parts[parts.length - 1];
                // Remove file extension
                return lastPart.substring(0, lastPart.lastIndexOf('.'));
            }
        } catch (Exception e) {
            System.err.println("Failed to extract public ID from URL: " + url);
        }
        return null;
    }

    // 🔹 Delete Product by ID
    public void deleteProduct(Long id) {
        // Get the product to access its image URLs for cleanup
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            // Delete images from Cloudinary
            deleteUploadedImages(product.getImageUrls());
        }

        List<OrderDetails> orderDetails = orderDetailsRepository.findByProductId(id);
        orderDetails.forEach(od -> {
            od.setProduct(null); // This requires setProduct() method in OrderDetails
            orderDetailsRepository.save(od); // Save the change
        });

        productRepository.deleteById(id);
    }

//    // 🔹 Update Product by ID
//    public Product updateProduct(Long id, ProductUpdateRequest newProduct, List<MultipartFile> newImages) {
//        return productRepository.findById(id).map(product -> {
//            // Update basic fields
//            product.setProductName(newProduct.getProductName());
//            product.setDescription(newProduct.getDescription());
//            product.setPrice(newProduct.getPrice());
//            product.setQuantity(newProduct.getQuantity());
//            product.setCategory(newProduct.getCategory());
//            product.setAge(newProduct.getAge());
//            product.setWeight(newProduct.getWeight());
//            product.setBreed(newProduct.getBreed());
//            product.setHealthStatus(newProduct.getHealthStatus());
//            product.setCondition(newProduct.getCondition());
//
//            // Handle images
//            List<String> existingImageUrls = newProduct.getExistingImageUrls() != null ?
//                    newProduct.getExistingImageUrls() : new ArrayList<>();
//
//            List<String> newImageUrls = new ArrayList<>();
//
//            if (newImages != null) {
//                for (MultipartFile file : newImages) {
//                    if (!file.isEmpty()) {
//                        try {
//                            String imageUrl = fileStorageServices.storeFile(file);
//                            newImageUrls.add(imageUrl);
//                        } catch (IOException e) {
//                            throw new RuntimeException("Failed to store file", e);
//                        }
//                    }
//                }
//            }
//
//            // Combine existing and new image URLs
//            List<String> allImageUrls = new ArrayList<>();
//            allImageUrls.addAll(existingImageUrls);
//            allImageUrls.addAll(newImageUrls);
//
//            product.setImageUrls(allImageUrls);
//
//            return productRepository.save(product);
//        }).orElseThrow(() -> new RuntimeException("Product not found!"));
//    }




    // UPDATE WITH CLOUDINARY INTEGRATION STARTS HERE
    @Transactional
    public Product updateProduct(Long id, ProductUpdateRequest newProduct, List<MultipartFile> newImages) {
        return productRepository.findById(id).map(product -> {
            try {
                // Store original image URLs for cleanup if update fails
                List<String> originalImageUrls = new ArrayList<>(product.getImageUrls());

                // Update basic fields
                product.setProductName(newProduct.getProductName());
                product.setDescription(newProduct.getDescription());
                product.setPrice(newProduct.getPrice());
                product.setQuantity(newProduct.getQuantity());
//                product.setCategory(newProduct.getCategory());
                product.setCategory(newProduct.getCategory());
                product.setAge(newProduct.getAge());
                product.setWeight(newProduct.getWeight());
                product.setBreed(newProduct.getBreed());
                product.setHealthStatus(newProduct.getHealthStatus());
                product.setCondition(newProduct.getCondition());

                // Handle images
                List<String> existingImageUrls = newProduct.getExistingImageUrls() != null ?
                        newProduct.getExistingImageUrls() : new ArrayList<>();

                // Find images to delete (images that were in original but not in existingImageUrls)
                List<String> imagesToDelete = originalImageUrls.stream()
                        .filter(url -> !existingImageUrls.contains(url))
                        .collect(Collectors.toList());

                // Upload new images to Cloudinary
                List<String> newImageUrls = uploadNewImages(newImages);

                // Combine existing and new image URLs
                List<String> allImageUrls = new ArrayList<>();
                allImageUrls.addAll(existingImageUrls);
                allImageUrls.addAll(newImageUrls);

                // Validate that we have at least one image
                if (allImageUrls.isEmpty()) {
                    throw new IllegalArgumentException("At least one product image is required.");
                }

                product.setImageUrls(allImageUrls);

                // Save the updated product
                Product savedProduct = productRepository.save(product);

                // Delete removed images from Cloudinary (only after successful save)
                deleteCloudinaryImagesByUrls(imagesToDelete);

                return savedProduct;

            } catch (Exception e) {
                throw new RuntimeException("Failed to update product: " + e.getMessage(), e);
            }
        }).orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }

    // 🔹 Helper method to upload new images to Cloudinary
    private List<String> uploadNewImages(List<MultipartFile> newImages) throws IOException {
        List<String> newImageUrls = new ArrayList<>();
        List<String> uploadedPublicIds = new ArrayList<>();

        if (newImages == null || newImages.isEmpty()) {
            return newImageUrls;
        }

        try {
            for (MultipartFile file : newImages) {
                if (!file.isEmpty()) {
                    String publicId = generateUniquePublicId();
                    String imageUrl = uploadFileToCloudinary(file, publicId);

                    newImageUrls.add(imageUrl);
                    uploadedPublicIds.add(publicId);
                }
            }
            return newImageUrls;
        } catch (Exception e) {
            // Clean up any successfully uploaded files if one fails
            deleteCloudinaryImages(uploadedPublicIds);
            throw new IOException("Failed to upload new images: " + e.getMessage(), e);
        }
    }

// 🔹 Helper method to delete images from Cloudinary using URLs
    private void deleteCloudinaryImagesByUrls(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;

        List<String> publicIds = extractPublicIdsFromUrls(imageUrls);
        deleteCloudinaryImages(publicIds);
    }


//UPDATE WITH CLOUDINARY INTEGRATION ENDS HERE


























    // ✅ Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }



//    PAGINATION
// ✅ Service method for paginated products
public ProductPageResponse getAllProducts(Pageable pageable) {
    Page<Product> page = productRepository.findAll(pageable);

    List<ProductDTO> productDTOs = page.getContent().stream().map(product -> {
        FarmerDTO farmerDTO = new FarmerDTO(
                product.getFarmer().getId(),
//                product.getFarmer().getFarmerId(),
                product.getFarmer().getFullName(),
                product.getFarmer().getPhoneNummber(),
//                product.getFarmer().getPhoneNumber(),
                product.getFarmer().getUsername(),
//                product.getFarmer().getAuthorities()
//                product.getFarmer().getRole()
                product.getFarmer().getRoles()
        );

        FarmDTO farmDTO = null;
        if (product.getFarm() != null) {
            farmDTO = new FarmDTO(
//                    product.getFarm().getFarmId(),
                    product.getFarm().getFarm_id(),
                    product.getFarm().getFarmName(),
                    product.getFarm().getLocation(),
                    product.getFarm().getFarmAddress(),
                    product.getFarm().getFarmType(),
                    product.getFarm().getFarmSize(),
                    product.getFarm().getEmail(),
//                    product.getFarm().getFarmAddress(),
//                    product.getFarm().getDescription(),
//                    product.getFarm().getFarmOwner(),
                    farmerDTO, // owner of farm
                    product.getFarm().getContact()

                    );
        }

        CategoryRequestDTO categoryRequestDTO = null;
        if (product.getCategory() != null) {
            categoryRequestDTO = new CategoryRequestDTO(
                    product.getCategory().getId(),
                    product.getCategory().getCategoryName(),
                    product.getCategory().getCategoryDescription(),
                    product.getCategory().getUserRole()
//                    farmerDTO, // owner of farm
//                    product.getFarm().getContact()

            );
        }






        return new ProductDTO(
                product.getId(),
                product.getProductName(),
                product.getPrice(),
                product.getQuantity(),
                categoryRequestDTO,
//                product.getCategory(),
                product.getImageUrls(),
                product.getDescription(),
                product.getWeight(),
                product.getBreed(),
                product.getHealthStatus(),
                product.getCondition(),
                product.getGender(),
                product.getAge(),
                product.getCreatedAt(),
                farmDTO,
                farmerDTO
        );
    }).collect(Collectors.toList());

    return new ProductPageResponse(
            productDTOs,
            page.getNumber(),
            page.getTotalPages(),
            page.getTotalElements(),
            page.getSize(),
            page.hasNext(),
            page.hasPrevious()
    );
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
        return productRepository.findByCategory_CategoryName(location);
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





    // Get latest 6 products
    public List<LatestProductDTO> getLatestProducts() {
        PageRequest pageRequest = PageRequest.of(0, 6, Sort.by("createdAt").descending());
        return productRepository.findAll(pageRequest)
                .getContent()
                .stream()
                .map(ProductMapper::toDTO)
                .toList();
    }

    // Paginated + DTO
    public Page<LatestProductDTO> getProductsPaginated(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.findAll(pageRequest)
                .map(ProductMapper::toDTO); // ✅ map each entity to DTO
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
