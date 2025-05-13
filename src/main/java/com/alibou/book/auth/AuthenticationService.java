package com.alibou.book.auth;

import com.alibou.book.email.EmailService;
import com.alibou.book.email.EmailTemplateName;
import com.alibou.book.email.MNotifyV2SmsService;
import com.alibou.book.role.RoleRepository;
import com.alibou.book.security.JwtService;
import com.alibou.book.user.Token;
import com.alibou.book.user.TokenRepository;
import com.alibou.book.user.User;
import com.alibou.book.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
        String finalActivationUrl = activationUrl + "?token=" + newToken;
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




        emailService.sendEmail(
                user.getUsername(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
                );



        System.out.println(STR."This is the recipient \{user.getPhoneNummber()}");
        String message = "Hello " + user.getFirstname() + ", your OTP is: " + newToken;
        mNotifyV2SmsService.sendSms(Collections.singletonList(user.getPhoneNummber()),message);

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
}
