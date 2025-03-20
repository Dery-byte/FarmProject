package com.alibou.book.Services;

import com.alibou.book.Entity.Farm;
import com.alibou.book.Entity.Product;
import com.alibou.book.Repositories.FarmRepository;
import com.alibou.book.security.JwtService;
import com.alibou.book.user.User;
import com.alibou.book.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.List;
import java.util.Optional;


@Service
public class FarmService {

    private FarmRepository farmRepository;
    private UserRepository userRepository;
    private JwtService jwtService;

    public FarmService(FarmRepository farmRepository, UserRepository userRepository, JwtService jwtService) {
        this.farmRepository = farmRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
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
//            existingFarm.setSize(updatedFarm.getSize());
            return farmRepository.save(existingFarm);
        }).orElseThrow(() -> new RuntimeException(STR."Farm not found with ID: \{id}"));
    }








    public List<Farm> getFarmsByUser(Long userId) {
        return farmRepository.findByFarmerId(userId);
    }


}
