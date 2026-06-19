package com.alibou.book.Controllers;

import com.alibou.book.DTO.DeliveryAssignmentRequest;
import com.alibou.book.Entity.DeliveryAssignment;
import com.alibou.book.Entity.LogisticsPartner;
import com.alibou.book.Entity.Order;
import com.alibou.book.Repositories.LogisticsPartnerRepository;
import com.alibou.book.Repositories.OrderRepository;
import com.alibou.book.Services.LogisticsPortalService;
import com.alibou.book.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/portal")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Logistics Partner Portal")
public class LogisticsPortalController {

    private final LogisticsPortalService portalService;
    private final LogisticsPartnerRepository partnerRepository;
    private final OrderRepository orderRepository;
    private final UserDetailsService userDetailsService;

    // ─── Partner Portal Endpoints ────────────────────────────────────────────

    /** My assigned deliveries */
    @GetMapping("/assignments")
    public ResponseEntity<List<DeliveryAssignment>> getMyAssignments() {
        return ResponseEntity.ok(portalService.getMyAssignments());
    }

    /** Accept or decline an assignment */
    @PostMapping("/assignments/{id}/respond")
    public ResponseEntity<DeliveryAssignment> respond(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String response = body.get("response");
        String notes = body.getOrDefault("partnerNotes", "");
        return ResponseEntity.ok(portalService.respondToAssignment(id, response, notes));
    }

    /** Mark picked up or delivered */
    @PostMapping("/assignments/{id}/status")
    public ResponseEntity<DeliveryAssignment> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(portalService.updateDeliveryStatus(id, body.get("status")));
    }

    /** Earnings summary */
    @GetMapping("/earnings")
    public ResponseEntity<Map<String, Object>> getEarnings() {
        return ResponseEntity.ok(portalService.getMyEarnings());
    }

    /** My profile */
    @GetMapping("/profile")
    public ResponseEntity<LogisticsPartner> getProfile() {
        return ResponseEntity.ok(portalService.getMyProfile());
    }

    // ─── Admin/Farmer: Create Assignment for an Order ────────────────────────

    @PostMapping("/orders/{orderId}/assign")
    public ResponseEntity<DeliveryAssignment> createAssignment(
            @PathVariable Long orderId,
            @RequestParam Long partnerId,
            @RequestBody DeliveryAssignmentRequest req,
            Principal principal) {

        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        LogisticsPartner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner not found"));

        DeliveryAssignment assignment = portalService.createDeliveryAssignment(
                order, partner,
                req.getPickupLocation(), req.getDestinationAddress(),
                req.getAnimalType(), req.getQuantityOrWeight(),
                req.getSpecialRequirements(), req.getAgreedCharge(),
                user
        );
        return ResponseEntity.ok(assignment);
    }

    /** Get all assignments for an order (Admin view) */
    @GetMapping("/orders/{orderId}/assignments")
    public ResponseEntity<List<DeliveryAssignment>> getOrderAssignments(@PathVariable Long orderId) {
        return ResponseEntity.ok(portalService.getAssignmentsByOrder(orderId));
    }

    /** DEBUG: Check what the current user sees */
    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> debug() {
        return ResponseEntity.ok(portalService.getDebugInfo());
    }
}
