package com.alibou.book.Controllers;

import com.alibou.book.DTO.FarmResponse;
import com.alibou.book.Entity.Farm;
import com.alibou.book.Entity.Product;
import com.alibou.book.Repositories.FarmRepository;
import com.alibou.book.Services.FarmService;
import com.alibou.book.security.JwtService;
import com.alibou.book.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth/farms")
public class FarmController {
    private final FarmRepository farmRepository;
    private FarmService farmService;
    private JwtService jwtService;

    private UserDetailsService userDetailsService;

    public FarmController(FarmService farmService, JwtService jwtService, UserDetailsService userDetailsService, FarmRepository farmRepository) {
        this.farmService = farmService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.farmRepository = farmRepository;
    }

    // ✅ Get all farms
    @GetMapping
    public ResponseEntity<List<Farm>> getAllFarms() {
        List<Farm> farms = farmService.getAllFarms();
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable Long id) {
        Optional<Farm> farm = farmService.getFarmById(id);
        if (farm.isPresent()) {
            return ResponseEntity.ok(farm.get());  // Return product if found
        } else {
            Map<String, String> errorResponse = Map.of("message", STR."Farm with ID \{id} cannot be found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // ✅ Search farms by name
    @GetMapping("/search")
    public ResponseEntity<?> getFarmsByName(@RequestParam String name) {
        List<Farm> farms = farmService.getFarmsByName(name);
        return farms.isEmpty()
                ? ResponseEntity.badRequest().body(STR."No farms found with name containing: \{name}")
                : ResponseEntity.ok(farms);
    }


    // ✅ Search farms by location
    @GetMapping("/location")
    public ResponseEntity<?> getFarmsByLocation(@RequestParam(defaultValue = "No  farm on this location") String location) {
        List<Farm> farms = farmService.getFarmsByLocation(location);
        return farms.isEmpty()
                ? ResponseEntity.badRequest().body(STR."No farms found at: \{location}")
                : ResponseEntity.ok(farms);
    }

    // ✅ Add a new farm
//    @PostMapping("/add")
//    public ResponseEntity<Farm> addFarm(@RequestBody Farm farm,
//                                        @RequestHeader("Authorization") String token
//    ) {
//        String jwt = token.substring(7);
//        System.out.println(STR."JWT: \{jwt}");
//        String userEmail = jwtService.extractUsername(jwt); // Remove "Bearer "
//        System.out.println(STR."The User Name: \{userEmail}");
//        Farm savedFarm = farmService.saveFarm(farm, userEmail);
//
//        return ResponseEntity.ok(savedFarm);
////        return ResponseEntity.status(HttpStatus.CREATED).body(savedFarm);
//    }

    // ✅ Delete a farm
    @DeleteMapping("/deleteFarmBy/{id}")
    public ResponseEntity<String> deleteFarm(@PathVariable Long id) {
        if (farmService.getFarmById(id).isPresent()) {
            farmService.deleteFarm(id);
            return ResponseEntity.ok(STR."Farm with ID \{id} deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body(STR."Farm with ID \{id} not found.");
        }
    }


    // ✅ Update Farm by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<Farm> updateProduct(@PathVariable Long id, @RequestBody Farm farm) {
        return ResponseEntity.ok(farmService.updateFarm(id, farm));
    }


































    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody Farm farm, Principal principal) {
        // Validate if the principal is present
        if (principal == null) {
            return ResponseEntity.badRequest().body("Principal is null");
        }
        // Fetch the user using the principal (currently logged-in user)
        User user = (User) this.userDetailsService.loadUserByUsername(principal.getName());
        farm.setFarmer(user);
        farmRepository.save(farm);
        return ResponseEntity.ok(farm);
        // Get the quiz ID from the report
//        Long quizId = report.getQuiz().getqId();
        // Validate if quizId is present
    }


    // Get products by farm name
    @GetMapping("/{farmName}/products")
    public ResponseEntity<List<Product>> getProductsByFarmName(
            @PathVariable String farmName
    ) {
        return ResponseEntity.ok(farmService.findProductsByFarmName(farmName));
    }


    // Get farms with their products by name
    @GetMapping("/with-products")
    public ResponseEntity<List<FarmResponse>> getFarmsWithProductsByName(
            @RequestParam String farmName
    ) {
        return ResponseEntity.ok(farmService.findFarmsWithProductsByName(farmName));
    }











}

