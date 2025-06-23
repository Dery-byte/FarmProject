package com.alibou.book.Services;

import com.alibou.book.DTO.PaymentStatusResponseDTO;
import com.alibou.book.DTO.UserSummaryDTOs;
import com.alibou.book.Entity.PaymentStatuss;
import com.alibou.book.Repositories.PaymentStatusRepository;
import com.alibou.book.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentStatusService {

    private final PaymentStatusRepository paymentStatusRepository;
    private final UserDetailsService userDetailsService;



    public PaymentStatusService(PaymentStatusRepository paymentStatusRepository, UserDetailsService userDetailsService) {
        this.paymentStatusRepository = paymentStatusRepository;
        this.userDetailsService = userDetailsService;
    }



    public PaymentStatuss save(PaymentStatuss status) {
        return paymentStatusRepository.save(status);
    }

    public List<PaymentStatuss> findAll() {
        return paymentStatusRepository.findAll();
    }

    public Optional<PaymentStatuss> findById(Long id) {
        return paymentStatusRepository.findById(id);
    }

    public Optional<PaymentStatuss> findByExternalRef(String externalRef) {
        return paymentStatusRepository.findByExternalRef(externalRef);
    }

    public void deleteById(Long id) {
        paymentStatusRepository.deleteById(id);
    }



    //payment For Farmer

    public List<PaymentStatusResponseDTO> paymentForFarmer(Long farmerId) {
        List<PaymentStatuss> payments = paymentStatusRepository.findByFarmer_Id(farmerId);

        return payments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    private PaymentStatusResponseDTO mapToDto(PaymentStatuss entity) {
        PaymentStatusResponseDTO dto = new PaymentStatusResponseDTO();
        dto.setId(entity.getId());
        dto.setTxStatus(entity.getTxStatus());
        dto.setPayer(entity.getPayer());
        dto.setPayee(entity.getPayee());
        dto.setAmount(entity.getAmount());
        dto.setValue(entity.getValue());
        dto.setTransactionId(entity.getTransactionId());
        dto.setExternalRef(entity.getExternalRef());
        dto.setThirdPartyRef(entity.getThirdPartyRef());
        dto.setSecret(entity.getSecret());
        dto.setTimestamp(entity.getTimestamp());

        // Map Customer
        if (entity.getCustomer() != null) {
            UserSummaryDTOs customer = new UserSummaryDTOs();
            customer.setId(Long.valueOf(entity.getCustomer().getId()));
            customer.setFirstname(entity.getCustomer().getFirstname());
            customer.setLastname(entity.getCustomer().getLastname());
            customer.setPhoneNummber(entity.getCustomer().getPhoneNummber());
            customer.setFullName(entity.getCustomer().getFullName());

            // Delivery
//            if (entity.getCustomer().getDelivery() != null) {
//                DeliveryDTO delivery = new DeliveryDTO();
//                delivery.setId(entity.getCustomer().getDelivery().getId());
//                delivery.setRecipientName(entity.getCustomer().getDelivery().getRecipientName());
//                delivery.setPhoneNumber(entity.getCustomer().getDelivery().getPhoneNumber());
//                delivery.setDigitalAddress(entity.getCustomer().getDelivery().getDigitalAddress());
//                delivery.setStreet(entity.getCustomer().getDelivery().getStreet());
//                delivery.setArea(entity.getCustomer().getDelivery().getArea());
//                delivery.setDistrict(entity.getCustomer().getDelivery().getDistrict());
//                delivery.setRegion(entity.getCustomer().getDelivery().getRegion());
//                delivery.setNotes(entity.getCustomer().getDelivery().getNotes());
//                delivery.setLandmark(entity.getCustomer().getDelivery().getLandmark());
//
//                customer.setDelivery(delivery);
//            }

            dto.setCustomer(customer);
        }

        // Map Farmer
//        if (entity.getFarmer() != null) {
//            UserSummaryDTO farmer = new UserSummaryDTO();
//            farmer.setId(entity.getFarmer().getId());
//            farmer.setFirstname(entity.getFarmer().getFirstname());
//            farmer.setLastname(entity.getFarmer().getLastname());
//            farmer.setPhoneNummber(entity.getFarmer().getPhoneNummber());
//            farmer.setFullName(entity.getFarmer().getFullName());
//            // delivery is null for farmer in your example
//            dto.setFarmer(farmer);
//        }

        return dto;
    }


}

