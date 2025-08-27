package com.example.service;

import com.example.model.Admin;
import com.example.model.Doctor;
import com.example.model.Patient;
import com.example.repository.AdminRepository;
import com.example.repository.DoctorRepository;
import com.example.repository.PatientRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Component
public class TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey secretKey;

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // 1. Generate JWT Token
    public String generateToken(String identifier, String userType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000L); // 7 days

        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Extract identifier (subject) from token
    public String extractIdentifier(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null; // token invalid or expired
        }
    }

    // 3. Validate token for user type
    public boolean validateToken(String token, String userType) {
        String identifier = extractIdentifier(token);
        if (identifier == null) return false;

        switch (userType.toLowerCase()) {
            case "admin":
                Admin admin = adminRepository.findByUsername(identifier);
                return admin != null;
            case "doctor":
                Optional<Doctor> doctor = Optional.ofNullable(doctorRepository.findByEmail(identifier));
                return doctor.isPresent();
            case "patient":
                Optional<Patient> patient = Optional.ofNullable(patientRepository.findByEmail(identifier));
                return patient.isPresent();
            default:
                return false;
        }
    }

    // 4. Get the signing key
    public SecretKey getSigningKey() {
        return secretKey;
    }

    // Optional: extract email directly for convenience
    public String getEmailFromToken(String token) {
        return extractIdentifier(token);
    }
}
