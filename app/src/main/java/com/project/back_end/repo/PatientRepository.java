package com.example.repository;

import com.example.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // 1. Find a patient by email
    Patient findByEmail(String email);

    // 2. Find a patient by email or phone number
    Patient findByEmailOrPhone(String email, String phone);
}
