package com.practice.project_1.controller;


import com.practice.project_1.model.Users;
import com.practice.project_1.repository.UserRepository;
import com.practice.project_1.service.OtpService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.Data;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Data
    static class OtpRequest {
        private String mobileNumber;
    }

    @Data
    static class OtpVerificationRequest {
        private String mobileNumber;
        private String otp;
    }

    @Data
    static class UserCredentialsRequest {
        private String name;
        private String email;
        private String address;
        private String mobileNumber;
    }

    private String normalizeMobileNumber(String mobileNumber) {
        if (mobileNumber != null) {
            return mobileNumber.trim().replaceAll("\\s+", "");
        }
        return mobileNumber;
    }

    private String generateJwtToken(String mobileNumber) {
        return Jwts.builder()
                .setSubject(mobileNumber)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody OtpRequest request) {
        String mobileNumber = normalizeMobileNumber(request.getMobileNumber());
        logger.info("Attempting signup for mobileNumber: {}", mobileNumber);
        if (!mobileNumber.startsWith("+") || !mobileNumber.matches("\\+\\d{10,15}")) {
            logger.warn("Invalid mobile number format: {}", mobileNumber);
            return ResponseEntity.badRequest().body("Invalid mobile number format. Use E.164 format (e.g., +1234567890)");
        }
        if (userRepository.existsById(mobileNumber)) {
            logger.warn("User already exists for mobileNumber: {}", mobileNumber);
            return ResponseEntity.badRequest().body("User already exists");
        }

        Users user = new Users();
        user.setMobileNumber(mobileNumber);
        userRepository.save(user);
        logger.info("User saved: {}", user);

        String otp = otpService.generateOtp(mobileNumber);
        return ResponseEntity.ok("OTP sent to " + mobileNumber);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody OtpRequest request) {
        String mobileNumber = normalizeMobileNumber(request.getMobileNumber());
        logger.info("Attempting login for mobileNumber: {}", mobileNumber);
        if (!mobileNumber.startsWith("+") || !mobileNumber.matches("\\+\\d{10,15}")) {
            logger.warn("Invalid mobile number format: {}", mobileNumber);
            return ResponseEntity.badRequest().body("Invalid mobile number format. Use E.164 format (e.g., +1234567890)");
        }
        if (!userRepository.existsById(mobileNumber)) {
            logger.warn("User not found for mobileNumber: {}", mobileNumber);
            return ResponseEntity.badRequest().body("User not found");
        }

        String otp = otpService.generateOtp(mobileNumber);
        logger.info("OTP generated for mobileNumber: {}", mobileNumber);
        return ResponseEntity.ok("OTP sent to " + mobileNumber);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationRequest request) {
        String mobileNumber = normalizeMobileNumber(request.getMobileNumber());
        logger.info("Verifying OTP for mobileNumber: {}", mobileNumber);
        if (!mobileNumber.startsWith("+") || !mobileNumber.matches("\\+\\d{10,15}")) {
            logger.warn("Invalid mobile number format: {}", mobileNumber);
            return ResponseEntity.badRequest().body("Invalid mobile number format. Use E.164 format (e.g., +1234567890)");
        }
        if (otpService.validateOtp(mobileNumber, request.getOtp())) {
            otpService.clearOtp(mobileNumber);
            String token = generateJwtToken(mobileNumber);
            logger.info("OTP verified successfully for mobileNumber: {}", mobileNumber);
            return ResponseEntity.ok(token);
        }
        logger.warn("Invalid OTP for mobileNumber: {}", mobileNumber);
        return ResponseEntity.badRequest().body("Invalid OTP");
    }

    @PostMapping("/update-credentials")
    public ResponseEntity<String> updateCredentials(@Valid @RequestBody UserCredentialsRequest request,
                                                    @RequestHeader("Authorization") String token) {
        String mobileNumber = normalizeMobileNumber(request.getMobileNumber());
        logger.info("Updating credentials for mobileNumber: {}", mobileNumber);
        logger.debug("Request data: name={}, email={}, address={}",
                request.getName(), request.getEmail(), request.getAddress());
        try {
            String jwt = token.replace("Bearer ", "");
            String tokenMobileNumber = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody().getSubject();
            if (!mobileNumber.equals(tokenMobileNumber)) {
                logger.warn("Token mismatch for mobileNumber: {}", mobileNumber);
                return ResponseEntity.badRequest().body("Invalid token");
            }
            Users user = userRepository.findById(mobileNumber)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            logger.debug("User before update: {}", user);
            user.setUsername(request.getName());
            user.setUserEmail(request.getEmail());
            user.setUserLocation(request.getAddress());
            logger.debug("User after setting fields: {}", user);
            Users savedUser = userRepository.save(user);
            logger.debug("User after save: {}", savedUser);
            logger.info("Credentials updated for mobileNumber: {}", mobileNumber);
            return ResponseEntity.ok("Credentials updated successfully");
        } catch (Exception e) {
            logger.error("Error updating credentials: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid token or user not found");
        }
    }
}
