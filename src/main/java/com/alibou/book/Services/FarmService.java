package com.alibou.book.Services;

import com.alibou.book.DTO.FarmDTO;
import com.alibou.book.DTO.FarmResponse;
import com.alibou.book.DTO.FarmWithProductsDTO;
import com.alibou.book.DTO.ProductDTO;
import com.alibou.book.Entity.Farm;
import com.alibou.book.Entity.Product;
import com.alibou.book.Repositories.FarmRepository;
import com.alibou.book.security.JwtService;
import com.alibou.book.user.User;
import com.alibou.book.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class FarmService {

    private FarmRepository farmRepository;
    private UserRepository userRepository;
    private JwtService jwtService;
    private final UserDetailsService userDetailsService;


    public FarmService(FarmRepository farmRepository, UserRepository userRepository, JwtService jwtService, UserDetailsService userDetailsService) {
        this.farmRepository = farmRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public List<Farm> getAllFarms() {
        return farmRepository.findAll();
    }

    public Optional<Farm> getFarmById(Long id) {
        return farmRepository.findById(id);
    }

    public List<Farm> getFarmsByName(String name) {
        return farmRepository.findByFarmNameContainingIgnoreCase(name);
    }

    public List<Farm> getFarmsByLocation(String location) {
        return farmRepository.findByLocationContainingIgnoreCase(location);
    }



    public Farm saveFarm(Farm farm, String userEmail) {
        User user = userRepository.findByUsername(userEmail)
                .orElseThrow(()->new UsernameNotFoundException("User not found"));

        System.out.println(STR."This is the: \{user.getUsername()}");
        farm.setFarmer(user);
        return farmRepository.save(farm);
    }




















    public void deleteFarm(Long id) {
        farmRepository.deleteById(id);
    }

//    Upadate farm

//    public Farm updateFarm(Long id, Farm newFarm) {
//        return farmRepository.findById(id).map(farm -> {
//            farm.setFarmName(String.valueOf(newFarm.getFarmName()));
//            farm.setFarmer(newFarm.getFarmer());
//            farm.setLocation(newFarm.getLocation());
//            return farmRepository.save(farm);
//        }).orElseThrow(() -> new RuntimeException("Product not found!"));
//    }


    public Farm updateFarm(Long id, Farm updatedFarm) {
        return farmRepository.findById(id).map(existingFarm -> {
            // Retain the existing user (owner) if not provided in the update
            if (updatedFarm.getFarmer() == null) {
                updatedFarm.setFarmer(existingFarm.getFarmer());
            }
            // Copy other fields from updatedFarm to existingFarm
            existingFarm.setFarmName(updatedFarm.getFarmName());
            existingFarm.setLocation(updatedFarm.getLocation());
            existingFarm.setFarmSize(updatedFarm.getFarmSize());
            existingFarm.setFarmAddress(updatedFarm.getFarmAddress());
            existingFarm.setContact(updatedFarm.getContact());
            existingFarm.setFarmType(updatedFarm.getFarmType());
            existingFarm.setYearEstablished(updatedFarm.getYearEstablished());
            existingFarm.setNumberOfworkers(updatedFarm.getNumberOfworkers());
//            existingFarm.setSize(updatedFarm.getSize());
            return farmRepository.save(existingFarm);
        }).orElseThrow(() -> new RuntimeException(STR."Farm not found with ID: \{id}"));
    }








    public List<Farm> getFarmsByUser(Long userId) {
        return farmRepository.findByFarmerId(userId);
    }





    public List<FarmResponse> findFarmsWithProductsByName(String farmName) {
        return farmRepository.findByFarmNameContainingIgnoreCase(farmName)
                .stream()
                .map(farm -> FarmResponse.builder()
                        .id(farm.getFarm_id())
                        .farmName(farm.getFarmName())
                        .location(farm.getLocation())
                        .products(farm.getProductList())
                        .build())
                .toList();
    }



    // Option 1: Get farms by name
    public List<Farm> findFarmsByName(String farmName) {
        return farmRepository.findByFarmNameContainingIgnoreCase(farmName);
    }

    // Option 2: Get products by farm name
    public List<Product> findProductsByFarmName(String farmName) {
        return farmRepository.findProductsByFarmName(farmName);
    }









































    public List<FarmDTO> getFarmsForCurrentFarmer(Long farmerId) {
//        if (principal == null) {
//            throw new IllegalArgumentException("User must be authenticated to fetch orders.");
//        }
//        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        //User currentFarmer = authenticationService.getCurrentUser();
        List<Farm> farms = farmRepository.findByFarmerId(farmerId);
        return farms.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Optional: Get farms with products
    public List<FarmWithProductsDTO> getFarmsWithProductsForCurrentFarmer(Long farmerId) {
//        if (principal == null) {
//            throw new IllegalArgumentException("User must be authenticated to fetch orders.");
//        }
//        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        List<Farm> farms = farmRepository.findByFarmerIdWithProducts(farmerId);
        return farms.stream()
                .map(this::convertToDTOWithProducts)
                .collect(Collectors.toList());
    }

    private FarmDTO convertToDTO(Farm farm) {
        return FarmDTO.builder()
                .id(farm.getFarm_id())
                .name(farm.getFarmName())
                .location(farm.getLocation())
                .address(farm.getFarmAddress())
                .type(farm.getFarmType())
                .size(farm.getFarmSize())
                .build();
    }

    private FarmWithProductsDTO convertToDTOWithProducts(Farm farm) {
        List<ProductDTO> productDTOs = farm.getProductList().stream()
                .map(p -> ProductDTO.builder()
                        .id(p.getId())
                        .name(p.getProductName())
                        .quantity(p.getQuantity())
                        .category(p.getCategory())
                        .price(p.getPrice())
                        .imageUrls(p.getImageUrls()) // Include image URLs
                        .build())
                .collect(Collectors.toList());
        return FarmWithProductsDTO.builder()
                .id(farm.getFarm_id())
                .name(farm.getFarmName())
                .products(productDTOs)
                .build();
    }















}
