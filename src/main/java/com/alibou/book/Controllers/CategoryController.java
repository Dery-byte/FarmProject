package com.alibou.book.Controllers;


import com.alibou.book.DTO.CategoryRequestDTO;
import com.alibou.book.Services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/auth/category")
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@RequestBody CategoryRequestDTO categoryRequest, Principal principal) throws IOException, IOException {
        return categoryService.addCategory(categoryRequest, principal);
    }

    // 📖 Get All Categories (for logged-in user)
    @GetMapping("/allCategories")
    public ResponseEntity<?> getCategories(Principal principal) {
        return categoryService.getCategories(principal);
    }

    // ✏️ Update Category
    @PutMapping("/updateCategory/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CategoryRequestDTO categoryRequest, Principal principal) {
        return categoryService.updateCategory(id, categoryRequest, principal);
    }


    @GetMapping("/getAllCategories")
    public ResponseEntity<?> getCategories() {
        return categoryService.getAllCategories();
    }




    // ❌ Delete Category
    @DeleteMapping("/deleteCategory/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id, Principal principal) {
        return categoryService.deleteCategory(id, principal);
    }


    @GetMapping("/getCateoryById/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id, Principal principal) {
        return categoryService.getCategoryById(id, principal);
    }





}
