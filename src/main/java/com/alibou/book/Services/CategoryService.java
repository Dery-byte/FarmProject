package com.alibou.book.Services;

import com.alibou.book.DTO.CategoryRequestDTO;
import com.alibou.book.DTO.FarmDTO;
import com.alibou.book.DTO.ProductRequestDTO;
import com.alibou.book.Entity.Category;
import com.alibou.book.Entity.Farm;
import com.alibou.book.Entity.Product;
import com.alibou.book.Repositories.CategoryRepository;
import com.alibou.book.user.User;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class CategoryService {
    private final UserDetailsService userDetailsService;

    private final CategoryRepository categoryRepository;

    public CategoryService(UserDetailsService userDetailsService, CategoryRepository categoryRepository) {
        this.userDetailsService = userDetailsService;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ResponseEntity<?> addCategory(CategoryRequestDTO categoryRequest, Principal principal) throws IOException {
        if (principal == null) {
            return ResponseEntity.badRequest().body("Principal is null");
        }

        // Fetch the user using the principal (currently logged-in user)
        User user = (User) this.userDetailsService.loadUserByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        // Create and populate Category entity
        Category category = new Category();
        category.setCategoryName(categoryRequest.getCategoryName());
        category.setCategoryDescription(categoryRequest.getCategoryDescription());
        category.setUserRole(user.getAuthorities().toString());
//        category.setUser(user);

        // Save to DB
        Category savedCategory = categoryRepository.save(category);
        // Convert to DTO
        CategoryRequestDTO responseDTO = convertToDTO(savedCategory);
        return ResponseEntity.ok(responseDTO);
    }


    // 📖 GET ALL CATEGORIES
    public ResponseEntity<?> getCategories(Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body("Principal is null");
        }

        User user = (User) userDetailsService.loadUserByUsername(principal.getName());

        // Now fetch categories by userRole, not user
        List<CategoryRequestDTO> categories = categoryRepository.findByUserRole(user.getAuthorities().toString()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(categories);
    }


    // ✏️ UPDATE CATEGORY
    @Transactional
    public ResponseEntity<?> updateCategory(Long id, CategoryRequestDTO categoryRequest, Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body("Principal is null");
        }

        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
        }

        Category category = optionalCategory.get();
        category.setCategoryName(categoryRequest.getCategoryName());
        category.setCategoryDescription(categoryRequest.getCategoryDescription());

        Category updatedCategory = categoryRepository.save(category);
        return ResponseEntity.ok(convertToDTO(updatedCategory));
    }

    // ❌ DELETE CATEGORY
    @Transactional
    public ResponseEntity<?> deleteCategory(Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body("Principal is null");
        }

        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
        }

        categoryRepository.delete(optionalCategory.get());
        return ResponseEntity.ok("Category deleted successfully");
    }




    public ResponseEntity<?> getCategoryById(Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body("Principal is null");
        }
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
        }
        Category category = optionalCategory.get();
        // Optional: Restrict access to only categories matching the user's role
        if (!category.getUserRole().equals(user.getAuthorities().toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        CategoryRequestDTO dto = convertToDTO(category);
        return ResponseEntity.ok(dto);
    }




    // 🔄 Helper: Convert entity to DTO
    private CategoryRequestDTO convertToDTO(Category category) {
        return CategoryRequestDTO.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .categoryDescription(category.getCategoryDescription())
                .userRole(category.getUserRole())
                .build();
    }
}

