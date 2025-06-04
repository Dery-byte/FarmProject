package com.alibou.book.auth;

import com.alibou.book.DTO.RoleUpdateRequest;
import com.alibou.book.DTO.UserResponseDTO;
import com.alibou.book.DTO.UserSummaryDTO;
import com.alibou.book.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        service.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) throws MessagingException {
        return ResponseEntity.ok(service.authenticate(request));
    }


    @GetMapping("/activate-account")
    public void confirm(@RequestParam String token) throws MessagingException {
        service.activateAccount(token);
    }



    @GetMapping("/non-admins")
    public ResponseEntity<List<UserResponseDTO>> getNonAdminUsers() {
        return ResponseEntity.ok(service.getAllNonAdminUserDTOs());
    }



    @GetMapping("/non-admins/count")
    public ResponseEntity<Long> countNonAdminUsers() {
        return ResponseEntity.ok(service.countNonAdminUsers());
    }

    // Returns just the count number
    @GetMapping("/count")
    public long getNonAdminCount() {
        return service.countNonAdminUsers();
    }




    // Returns an object with count and other stats
    @GetMapping("/stats")
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("nonAdminCount", service.countNonAdminUsers());
        stats.put("timestamp", Instant.now());
        return stats;
    }















    // Get latest X users
    @GetMapping("/latest")
    public ResponseEntity<Page<User>> getLatestUsers(
            @RequestParam(defaultValue = "10") int count) {
        return ResponseEntity.ok(service.getLatestUsers(count));
    }

    // Get latest X non-admin users
    @GetMapping("/latest/non-admins")
    public ResponseEntity<Page<User>> getLatestNonAdminUsers(
            @RequestParam(defaultValue = "10") int count) {
        return ResponseEntity.ok(service.getLatestNonAdminUsers(count));
    }

    // Get users signed up in last X days
    @GetMapping("/recent")
    public ResponseEntity<List<User>> getRecentUsers(
            @RequestParam(defaultValue = "7") int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        return ResponseEntity.ok(service.getUsersSignedUpAfter(cutoff));
    }

    @GetMapping("/latestUsersSummary")
    public ResponseEntity<Page<UserSummaryDTO>> getLatestUsersSummary(
            @RequestParam(defaultValue = "6") int count) {
        return ResponseEntity.ok(service.getLatestUsersSummary(count));
    }








   // UPDATE USER ROLE


    @PutMapping("/users/{userid}/roles")
    public ResponseEntity<?> updateRoles(@PathVariable Long userid, @RequestBody RoleUpdateRequest request) {
        service.updateUserRoles(userid, request.getRoleNames());
        Map<String, String> response = new HashMap<>();
        response.put("message", "User role updated.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/roles")
    public ResponseEntity<List<String>> getUserRoles(@PathVariable Long id) {
        List<String> roles = service.getUserRoleNames(id);
        return ResponseEntity.ok(roles);
    }



    @GetMapping("allUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }











    @GetMapping("/admins")
    public ResponseEntity<Page<User>> getAdmins(
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getUsersByRole("ADMIN", pageable));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getRegularUsers(
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getUsersByRole("USER", pageable));
    }

    @GetMapping("/farmers")
    public ResponseEntity<Page<User>> getFarmers(
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getUsersByRole("FARMER", pageable));
    }


    @GetMapping("getUserById/{userId}")
    public User getUserById(@PathVariable Integer userId) {  // Add @PathVariable
        return service.findByUserId(userId);
    }
}
