package com.User_19.book.auth;

import com.User_19.book.Security.JwtService;
import com.User_19.book.email.EmailService;
import com.User_19.book.email.EmailTemplate;
import com.User_19.book.role.RoleRepository;
import com.User_19.book.user.Token;
import com.User_19.book.user.TokenRepository;
import com.User_19.book.user.User;
import com.User_19.book.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private String activationUrl;
    public void register(RegistrationRequest request) throws MessagingException {
        var userRole= roleRepository.findByName("USER")
                .orElseThrow(()->new IllegalArgumentException("ROLE USER was not initialize"));
        var user= User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplate.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account Activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        //generate token
        String generatedToken= generateActivationToken(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationToken(int length ) {
        String characters= "0123456789";
        StringBuilder codeBuilder= new StringBuilder();
        SecureRandom secureRandom= new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex= secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));

        }
        return codeBuilder.toString();


    }

    public AuthenticatoinResponse authenticate(AuthenticationRequest request) {
        var auth= authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var clamis= new HashMap<String ,Object>();
        var user=((User)auth.getPrincipal());
        clamis.put("fullname",user.getFullName());
        var token= jwtService.generateToken(clamis,user);
        return AuthenticatoinResponse.builder().Token(token).build();

    }

    public void activateAccount(String token) throws MessagingException {
        Token savedToken=tokenRepository.findByToken(token).orElseThrow(()-> new RuntimeException("invalid token "));
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("activation token has expired . Anew token has been sent to the same email ");

        }
        var user= userRepository.findById(savedToken.getUser().getId()).orElseThrow(()-> new UsernameNotFoundException("user not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);



    }
}
