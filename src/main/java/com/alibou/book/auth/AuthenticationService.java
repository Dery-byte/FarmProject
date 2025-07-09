package com.alibou.book.auth;

import com.alibou.book.DTO.ForgottenPasswordRequest;
import com.alibou.book.DTO.ResetPasswordRequest;
import com.alibou.book.DTO.UserResponseDTO;
import com.alibou.book.DTO.UserSummaryDTO;
import com.alibou.book.email.EmailService;
import com.alibou.book.email.EmailTemplateName;
import com.alibou.book.email.MNotifyV2SmsService;
import com.alibou.book.exception.ResetPasswordTokenAlreadyUsedException;
import com.alibou.book.exception.ResetPasswordTokenExpiredException;
import com.alibou.book.role.Role;
import com.alibou.book.role.RoleRepository;
import com.alibou.book.security.JwtService;
import com.alibou.book.user.Token;
import com.alibou.book.user.TokenRepository;
import com.alibou.book.user.User;
import com.alibou.book.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;

    private final MNotifyV2SmsService mNotifyV2SmsService;



    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Value("${application.mailing.frontend.baseUrl}")
    private String frontendBaseUrl;




    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                // todo - better exception handling
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .username(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNummber(request.getRecipient().get(0))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws MessagingException {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullName", user.getFullName());

        var jwtToken = jwtService.generateToken(claims, (User) auth.getPrincipal());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }




    //@Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                // todo exception has to be defined
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been sent to the same email address");
        }
        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    private String generateAndSaveActivationToken(User user) {
        // Generate a token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);

        return generatedToken;
    }


//    private void sendSMS(User user){
//        var newToken = generateAndSaveActivationToken(user);
//        System.out.println(STR."This is the recipient \{user.getPhoneNummber()}");
//        String message=newToken;
//        mNotifyV2SmsService.sendSms(Collections.singletonList(user.getPhoneNummber()),message);
//
//    }




    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        String finalActivationUrl = frontendBaseUrl + "/activate-account?token=" + newToken;
//        String resetUrl = frontendBaseUrl + "/reset-password?token=" + newToken;

        System.out.println("Final Activation Link: " + finalActivationUrl); // ✅ Debugging



        // ✅ SEND SMS if user has a phone number
//        if (user.getRecipient() != null && !user.getRecipient().isBlank()) {
//            String smsMessage = "Hello " + user.getFullName() +
//                    ", your activation code is: " + newToken +
//                    ". It expires in 15 minutes.";
//
//            try {
//                System.out.println("📱 Attempting to send SMS to: " + user.getRecipient());
//
//                mNotifyV2SmsService.sendSms(
//                        List.of(user.getRecipient()),
//                        smsMessage
//                );
//
//                System.out.println("✅ SMS send attempt finished.");
//
//            } catch (Exception e) {
//                System.err.println("❌ Failed to send SMS: " + e.getMessage());
//            }
//
//            System.out.println(user.getRecipient());
//        }



//        mNotifyV2SmsService.sendSms(user.getPhoneNummber(),message);

//        mNotifyV2SmsService.sendSms(Collections.singletonList(user.getPhoneNummber()),message);
        Map<String, Object> vars = new HashMap<>();
        vars.put("user", user.getFullName());
        vars.put("username", user.getFullName());
        vars.put("confirmationUrl", activationUrl);
        vars.put("newToken", newToken);
        vars.put("baseUrl", "https://farm-4fa35.web.app");

        emailService.sendEmail(
                user.getUsername(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                vars,
                "Account activation"
        );



//
//        emailService.sendEmail(
//                user.getUsername(),
//                user.getFullName(),
//                EmailTemplateName.ACTIVATE_ACCOUNT,
//                activationUrl,
//                newToken,
//                "Account activation"
//                );
        System.out.println(STR."This is the recipient \{user.getPhoneNummber()}");
//        String message = "Hello " + user.getFirstname() + ", your OTP is: " + newToken;
        String smsMessage = "Hello " + user.getFullName() +
                    ", your activation code is: " + newToken +
                    ". It expires in 15 minutes.";
        mNotifyV2SmsService.sendSms(Collections.singletonList(user.getPhoneNummber()),smsMessage);
    }

    public User findByUserId(Integer id) {
        return userRepository.findById(id)  // Use findById instead of getReferenceById
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }






















    // Basic version
    public List<User> getAllNonAdminUsers() {
        return userRepository.findAllNonAdminUsers();
    }

//    // Enhanced version with pagination
//    public Page<User> getAllNonAdminUsers(Pageable pageable) {
//        return userRepository.findAllNonAdminUsers(pageable);
//    }
    // DTO projection version
    public List<UserResponseDTO> getAllNonAdminUserDTOs() {
        return userRepository.findAllNonAdminUsers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }

    public long countNonAdminUsers() {
        return userRepository.countNonAdminUsers();
        // or: return userRepository.countByRoles_NameNot("ROLE_ADMIN");
    }
    public Page<User> getLatestUsers(int count) {
        return userRepository.findAllByOrderByCreatedDateDesc(
                (org.springframework.data.domain.Pageable) PageRequest.of(0, count)
        );
    }
    public Page<User> getLatestNonAdminUsers(int count) {
        return userRepository.findLatestNonAdminUsers(
                (Pageable) PageRequest.of(0, count)
        );
    }

    public List<User> getUsersSignedUpAfter(LocalDateTime date) {
        return userRepository.findByCreatedDateAfter(date);
    }

    public Page<UserSummaryDTO> getLatestUsersSummary(int count) {
        return userRepository.findAllByOrderByCreatedDateDesc(
                        PageRequest.of(0, count))
                .map(UserSummaryDTO::fromUser);
    }


//    USER ROLE UPDATES
public void updateUserRoles(Long userId, List<String> roleNames) {
    User user = userRepository.findById(Math.toIntExact(userId))
            .orElseThrow(() -> new RuntimeException("User not found"));

    Set<Role> roles = roleNames.stream()
            .map(roleName -> roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
            .collect(Collectors.toSet());

    user.setRoles(new ArrayList<>(roles)); // ✅ Convert Set to List
    userRepository.save(user);
}



    public List<String> getUserRoleNames(Long userId) {
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }














    public List<User> getAllUsers() {
        return userRepository.findAll();  // Uses the JPA method with entity graph
    }


    public Page<User> getUsersByRole(String roleName, Pageable pageable) {
        return userRepository.findByRoles_Name(roleName, pageable)
                .map(user -> User.builder()
                        .id(user.getId())
                        .firstname(user.getFirstname())
                        .lastname(user.getLastname())
                        .username(user.getUsername())
                        .phoneNummber(user.getPhoneNummber())
                        .createdDate(user.getCreatedDate())
                        .enabled(user.isEnabled())
                        .accountLocked(user.isAccountLocked())
                        .dateOfBirth(user.getDateOfBirth())

                        .roles(new ArrayList<>(user.getRoles()))  // ✅ Fix

                       // .roles(user.getRoles().stream().map(Role::getName).toList())
                       // .authorities(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                        .build()
                );
    }





    public void forgottenPassword(ForgottenPasswordRequest request) throws MessagingException {
        var user = userRepository.findByUsername(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        sendResetPasswordEmail(user);
    }
//
//    public void resetPassword(ResetPasswordRequest request) {
//        // 1. Find token in database
//        Token token = tokenRepository.findByToken(request.getToken())
//                .orElseThrow(() -> new RuntimeException("Invalid token"));
//
//        // 2. Validate token (check expiry and usage)
//        if (LocalDateTime.now().isAfter(token.getExpiresAt())) {
//            throw new RuntimeException("Token expired");
//        }
//        if (token.getValidatedAt() != null) {
//            throw new RuntimeException("Token already used");
//        }
//
//        // 3. Update user password
//        User user = token.getUser();
//        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
//        userRepository.save(user);
//
//        // 4. Invalidate token
//        token.setValidatedAt(LocalDateTime.now());
//        tokenRepository.save(token);
//    }



    public void resetPassword(ResetPasswordRequest request) {
        Token token = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (LocalDateTime.now().isAfter(token.getExpiresAt())) {
            throw new ResetPasswordTokenExpiredException("Token has expired. Please request a new one.");
        }

        if (token.getValidatedAt() != null) {
            throw new ResetPasswordTokenAlreadyUsedException("This token has already been used.");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        token.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(token);
    }


    private void sendResetPasswordEmail(User user) throws MessagingException {
        var newToken = generateAndSaveResetPasswordToken(user);
//        String resetUrl = resetURL.replace("resetpassword", "reset-password") + "?token=" + newToken;
        String resetUrl = frontendBaseUrl + "/reset-password?token=" + newToken;

        System.out.println(resetUrl);
        System.out.println(newToken);

        Map<String, Object> vars = new HashMap<>();
        vars.put("username", user.getFullName());
        vars.put("resetUrl", resetUrl);
        vars.put("newToken", newToken);
        vars.put("baseUrl", "http://localhost:4200/");

        emailService.sendEmail(
                user.getUsername(),
                EmailTemplateName.RESET_PASSWORD,
                vars,
                "Password Reset"
        );

//        emailService.sendEmail(
//                user.getUsername(),
//                user.getFullName(),
//                EmailTemplateName.RESET_PASSWORD,
//                resetUrl,
//                newToken,
//                "Password Reset"
//        );
    }

    private String generateAndSaveResetPasswordToken(User user) {
        String generatedToken = generateActivationCode(6); // Reuse your existing token generator
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }


}
