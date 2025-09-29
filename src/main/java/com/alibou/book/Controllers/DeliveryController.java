package com.alibou.book.Controllers;

import com.alibou.book.DTO.DeliveryInfoRequest;
import com.alibou.book.DTO.DeliveryResponse;
import com.alibou.book.Services.DeliveryService;
import com.alibou.book.exception.UserNotAuthenticatedException;
import com.alibou.book.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth/delivery")
@RequiredArgsConstructor
public class DeliveryController {

private final DeliveryService deliveryService;
    private final UserDetailsService userDetailsService;



    // Add or Update Delivery Info
//    @PostMapping("/addInfo")
//    public ResponseEntity<DeliveryResponse> addOrUpdateDelivery(
//           Principal principal,
//            @RequestBody DeliveryInfoRequest request
//    ) {
//
//        if (principal == null) {
//            throw new IllegalArgumentException("User must be authenticated to fetch orders.");
//        }
//        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
//        DeliveryResponse response = deliveryService.addOrUpdateDelivery(user, request);
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/addInfo")
    public ResponseEntity<DeliveryResponse> addOrUpdateDelivery(
            Principal principal,
            @RequestBody DeliveryInfoRequest request
    ) {
        if (principal == null) {
            throw new UserNotAuthenticatedException("User must be authenticated to fetch orders.");
        }
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        DeliveryResponse response = deliveryService.addOrUpdateDelivery(user, request);

        return ResponseEntity.ok(response);
    }






    // Get Delivery Info
    @GetMapping("/getDeliveryInfo")
    public ResponseEntity<DeliveryResponse> getDelivery(
            Principal principal
    ) {
        if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to fetch orders.");
        }
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        DeliveryResponse response = deliveryService.getDeliveryByUserId(user.getId());
        return ResponseEntity.ok(response);
    }



}