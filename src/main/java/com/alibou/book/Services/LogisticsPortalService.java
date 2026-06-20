package com.alibou.book.Services;

import com.alibou.book.Entity.*;
import com.alibou.book.Repositories.DeliveryAssignmentRepository;
import com.alibou.book.Repositories.LogisticsPartnerRepository;
import com.alibou.book.email.MNotifyV2SmsService;
import com.alibou.book.role.Role;
import com.alibou.book.role.RoleRepository;
import com.alibou.book.user.User;
import com.alibou.book.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogisticsPortalService {

    private final LogisticsPartnerRepository logisticsPartnerRepository;
    private final DeliveryAssignmentRepository deliveryAssignmentRepository;
    private final com.alibou.book.Repositories.OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MNotifyV2SmsService smsService;

    // ─── Credential Generation ────────────────────────────────────────────────

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private String generatePassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        return sb.toString();
    }

    private String generateUsername(String name) {
        String base = "partner_" + name.toLowerCase().replaceAll("[^a-z]", "").substring(0, Math.min(name.length(), 6));
        String suffix = String.format("%04d", RANDOM.nextInt(10000));
        return base + suffix;
    }

    // ─── Partner Account Creation ─────────────────────────────────────────────

    @Transactional
    public Map<String, String> createPartnerAccount(LogisticsPartner partner) {
        User currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"));

        // Get or create the LOGISTICS_PARTNER role
        Role logisticsRole = roleRepository.findByName("LOGISTICS_PARTNER")
                .orElseGet(() -> {
                    Role newRole = Role.builder().name("LOGISTICS_PARTNER").build();
                    return roleRepository.save(newRole);
                });

        // Generate credentials
        String rawPassword = generatePassword(10);
        String username = generateUsername(partner.getContactName() != null ? partner.getContactName() : "partner");

        // Create the portal user account
        User portalUser = User.builder()
                .firstname(partner.getContactName() != null ? partner.getContactName().split(" ")[0] : "Partner")
                .lastname(partner.getContactName() != null && partner.getContactName().contains(" ")
                        ? partner.getContactName().substring(partner.getContactName().indexOf(" ") + 1)
                        : "User")
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .phoneNummber(partner.getContactPhone())
                .companyName(partner.getCompanyName())
                .accountLocked(false)
                .enabled(true)
                .mustChangePassword(true)  // force change on first login
                .roles(List.of(logisticsRole))
                .build();
        userRepository.save(portalUser);

        // Link portal user to partner
        partner.setCreatedBy(currentUser);
        partner.setGlobal(isAdmin);
        partner.setPortalUser(portalUser);
        logisticsPartnerRepository.save(partner);

        // Send welcome SMS
        if (partner.getContactPhone() != null && !partner.getContactPhone().isBlank()) {
            String smsMessage = String.format(
                    "Welcome to FarmLink Logistics! Your portal account:\nUsername: %s\nPassword: %s\nLogin: http://localhost:4200/logistics-portal/login\nPlease change your password on first login.",
                    username, rawPassword
            );
            try {
                smsService.sendSms(Collections.singletonList(partner.getContactPhone()), smsMessage);
            } catch (Exception e) {
                System.err.println("SMS send failed: " + e.getMessage());
            }
        }

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", rawPassword);
        credentials.put("partnerName", partner.getPartnerName());
        return credentials;
    }

    // ─── Get Current User ─────────────────────────────────────────────────────

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    // ─── Portal: My Assignments ───────────────────────────────────────────────

    public List<DeliveryAssignment> getMyAssignments() {
        User user = getCurrentUser();
        // Step 1: find the LogisticsPartner whose portalUser matches the logged-in user
        LogisticsPartner partner = logisticsPartnerRepository.findByPortalUserId(user.getId())
                .orElse(null);
        if (partner == null) {
            return List.of(); // No partner profile linked — return empty list
        }
        // Step 2: find all assignments for this partner (EntityGraph fetches order + items)
        return deliveryAssignmentRepository.findByLogisticsPartnerId(partner.getId());
    }

    // ─── Portal: Respond to Assignment ───────────────────────────────────────

    @Transactional
    public DeliveryAssignment respondToAssignment(Long assignmentId, String response, String partnerNotes) {
        DeliveryAssignment assignment = deliveryAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (assignment.getStatus() != DeliveryAssignmentStatus.PENDING) {
            throw new IllegalStateException("Assignment has already been responded to");
        }

        if ("ACCEPTED".equalsIgnoreCase(response)) {
            assignment.setStatus(DeliveryAssignmentStatus.ACCEPTED);
        } else if ("DECLINED".equalsIgnoreCase(response)) {
            assignment.setStatus(DeliveryAssignmentStatus.DECLINED);
        } else {
            throw new IllegalArgumentException("Response must be ACCEPTED or DECLINED");
        }

        assignment.setPartnerNotes(partnerNotes);
        assignment.setRespondedAt(LocalDateTime.now());
        return deliveryAssignmentRepository.save(assignment);
    }

    // ─── Portal: Update Delivery Status ──────────────────────────────────────

    @Transactional
    public DeliveryAssignment updateDeliveryStatus(Long assignmentId, String newStatus) {
        DeliveryAssignment assignment = deliveryAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        switch (newStatus.toUpperCase()) {
            case "PICKED_UP" -> {
                if (assignment.getStatus() != DeliveryAssignmentStatus.ACCEPTED)
                    throw new IllegalStateException("Must be ACCEPTED before PICKED_UP");
                assignment.setStatus(DeliveryAssignmentStatus.PICKED_UP);
                assignment.setPickedUpAt(LocalDateTime.now());
            }
            case "DELIVERED" -> {
                if (assignment.getStatus() != DeliveryAssignmentStatus.PICKED_UP && assignment.getStatus() != DeliveryAssignmentStatus.ACCEPTED)
                    throw new IllegalStateException("Must be ACCEPTED or PICKED_UP before DELIVERED");
                assignment.setStatus(DeliveryAssignmentStatus.DELIVERED);
                assignment.setDeliveredAt(LocalDateTime.now());
                if (assignment.getOrder() != null) {
                    assignment.getOrder().setOrdersStatus(com.alibou.book.Entity.OrdersStatus.DELIVERED);
                    orderRepository.save(assignment.getOrder());
                }
            }
            default -> throw new IllegalArgumentException("Status must be PICKED_UP or DELIVERED");
        }

        return deliveryAssignmentRepository.save(assignment);
    }

    // ─── Portal: Earnings Summary ─────────────────────────────────────────────

    public Map<String, Object> getMyEarnings() {
        User user = getCurrentUser();
        List<DeliveryAssignment> all = deliveryAssignmentRepository.findByPartnerPortalUserId(user.getId());

        List<DeliveryAssignment> completed = all.stream()
                .filter(a -> a.getStatus() == DeliveryAssignmentStatus.DELIVERED)
                .collect(Collectors.toList());

        BigDecimal totalEarnings = completed.stream()
                .filter(a -> a.getAgreedCharge() != null)
                .map(DeliveryAssignment::getAgreedCharge)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("totalDeliveries", completed.size());
        result.put("totalEarnings", totalEarnings);
        result.put("pendingCount", all.stream().filter(a -> a.getStatus() == DeliveryAssignmentStatus.PENDING).count());
        result.put("acceptedCount", all.stream().filter(a -> a.getStatus() == DeliveryAssignmentStatus.ACCEPTED).count());
        result.put("completedDeliveries", completed);
        return result;
    }

    // ─── Portal: My Profile ───────────────────────────────────────────────────

    public LogisticsPartner getMyProfile() {
        User user = getCurrentUser();
        return logisticsPartnerRepository.findByPortalUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Partner profile not found"));
    }

    // ─── Create Delivery Assignment (called from OrderService) ───────────────

    @Transactional
    public DeliveryAssignment createDeliveryAssignment(Order order, LogisticsPartner partner,
                                                        String pickupLocation, String destinationAddress,
                                                        String animalType, String quantityOrWeight,
                                                        String specialRequirements, BigDecimal agreedCharge,
                                                        User assignedBy) {
        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setOrder(order);
        assignment.setLogisticsPartner(partner);
        assignment.setAssignedBy(assignedBy);
        assignment.setStatus(DeliveryAssignmentStatus.PENDING);
        assignment.setPickupLocation(pickupLocation);
        assignment.setDestinationAddress(destinationAddress);
        assignment.setAnimalType(animalType);
        assignment.setQuantityOrWeight(quantityOrWeight);
        assignment.setSpecialRequirements(specialRequirements);
        assignment.setAgreedCharge(agreedCharge);

        DeliveryAssignment saved = deliveryAssignmentRepository.save(assignment);

        // Send SMS notification to the partner
        if (partner.getContactPhone() != null && !partner.getContactPhone().isBlank()) {
            String smsMessage = String.format(
                    "FarmLink: New delivery assigned to you!\nOrder #%d\nPickup: %s\nDestination: %s\nLoad: %s (%s)\nCharge: GHS %.2f\nLogin to accept: http://localhost:4200/logistics-portal",
                    order.getId(), pickupLocation, destinationAddress, animalType, quantityOrWeight,
                    agreedCharge != null ? agreedCharge.doubleValue() : 0.0
            );
            try {
                smsService.sendSms(Collections.singletonList(partner.getContactPhone()), smsMessage);
            } catch (Exception e) {
                System.err.println("Delivery SMS failed: " + e.getMessage());
            }
        }

        return saved;
    }

    // ─── Admin: list all assignments for an order ─────────────────────────────

    public List<DeliveryAssignment> getAssignmentsByOrder(Long orderId) {
        return deliveryAssignmentRepository.findByOrderId(orderId);
    }

    // ─── DEBUG ───────────────────────────────────────────────────────────────

    public Map<String, Object> getDebugInfo() {
        User user = getCurrentUser();
        Map<String, Object> debug = new HashMap<>();
        debug.put("loggedInUserId", user.getId());
        debug.put("loggedInUsername", user.getUsername());
        debug.put("roles", user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList()));
        debug.put("totalAssignmentsInDB", deliveryAssignmentRepository.count());

        // Step 1: find partner
        var partnerOpt = logisticsPartnerRepository.findByPortalUserId(user.getId());
        if (partnerOpt.isPresent()) {
            LogisticsPartner p = partnerOpt.get();
            debug.put("partnerFound", true);
            debug.put("partnerId", p.getId());
            debug.put("partnerName", p.getPartnerName());
            // Step 2: count assignments
            List<DeliveryAssignment> byPartner = deliveryAssignmentRepository.findByLogisticsPartnerId(p.getId());
            debug.put("assignmentCountForPartner", byPartner.size());
            if (!byPartner.isEmpty()) {
                debug.put("firstAssignmentStatus", byPartner.get(0).getStatus());
                debug.put("firstAssignmentId", byPartner.get(0).getId());
            }
        } else {
            debug.put("partnerFound", false);
            debug.put("message", "No LogisticsPartner row has portalUser.id = " + user.getId() + ". The partner was either created before the portalUser link was added, or the partner registration did not complete.");
        }
        return debug;
    }
}
