package com.alibou.book.Services;

import com.alibou.book.DTO.DeliveryInfoRequest;
import com.alibou.book.DTO.DeliveryResponse;
import com.alibou.book.Entity.Delivery;
import com.alibou.book.Repositories.DeliveryRepository;
import com.alibou.book.user.User;
import com.alibou.book.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final UserRepository userRepository;
    private final DeliveryRepository deliveryRepository;
    private final UserDetailsService userDetailsService;


    // Add or Update Delivery Info
    @Transactional
    public DeliveryResponse addOrUpdateDelivery(Principal principal, DeliveryInfoRequest request) {

        User user = (User) userDetailsService.loadUserByUsername(principal.getName());

        // Check if user already has delivery info
        Delivery delivery = user.getDelivery();
        if (delivery == null) {
            // Create new delivery if none exists
            delivery = new Delivery();
            delivery.setUser(user);
        }

        // Update delivery fields from request
        updateDeliveryFromRequest(delivery, request);

        // Save and return response
        Delivery savedDelivery = deliveryRepository.save(delivery);
        return mapToDeliveryResponse(savedDelivery);
    }

    // Get Delivery Info by User ID
    public DeliveryResponse getDeliveryByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Delivery delivery = user.getDelivery();
        if (delivery == null) {
            throw new EntityNotFoundException("Delivery info not found for this user");
        }

        return mapToDeliveryResponse(delivery);
    }

    // Helper method to update Delivery from Request
    private void updateDeliveryFromRequest(Delivery delivery, DeliveryInfoRequest request) {
        delivery.setRecipientName(request.getRecipientName());
        delivery.setPhoneNumber(request.getPhoneNumber());
        delivery.setDigitalAddress(request.getDigitalAddress());
        delivery.setStreet(request.getStreet());
        delivery.setArea(request.getArea());
        delivery.setDistrict(request.getDistrict());
        delivery.setRegion(request.getRegion());
        delivery.setNotes(request.getNotes());
        delivery.setLandmark(request.getLandmark());
    }

    // Helper method to convert Delivery to Response DTO
    private DeliveryResponse mapToDeliveryResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .recipientName(delivery.getRecipientName())
                .phoneNumber(delivery.getPhoneNumber())
                .digitalAddress(delivery.getDigitalAddress())
                .street(delivery.getStreet())
                .area(delivery.getArea())
                .district(delivery.getDistrict())
                .region(delivery.getRegion())
                .notes(delivery.getNotes())
                .landmark(delivery.getLandmark())
                .build();
    }
}