package com.example.service;

import com.example.model.Prescription;
import com.example.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    // 1. Save a new prescription
    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Map<String, String> response = new HashMap<>();
        try {
            prescriptionRepository.save(prescription);
            response.put("message", "Prescription saved");
            return ResponseEntity.status(201).body(response); // 201 Created
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Failed to save prescription");
            return ResponseEntity.status(500).body(response); // 500 Internal Server Error
        }
    }

    // 2. Get prescription by appointment ID
    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(appointmentId);
            response.put("prescriptions", prescriptions);
            return ResponseEntity.ok(response); // 200 OK
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Failed to retrieve prescription");
            return ResponseEntity.status(500).body(response); // 500 Internal Server Error
        }
    }
}
