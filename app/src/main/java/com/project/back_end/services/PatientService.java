package com.example.service;

import com.example.dto.AppointmentDTO;
import com.example.model.Appointment;
import com.example.model.Patient;
import com.example.repository.AppointmentRepository;
import com.example.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    // 1. Create new patient
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 2. Get appointments for a patient by token
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        String email = tokenService.getEmailFromToken(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null || !patient.getId().equals(id)) {
            response.put("error", "Unauthorized access");
            return ResponseEntity.status(401).body(response);
        }

        List<AppointmentDTO> appointments = appointmentRepository.findByPatientId(id).stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        response.put("appointments", appointments);
        return ResponseEntity.ok(response);
    }

    // 3. Filter appointments by condition (past or future)
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        List<Appointment> appointments = appointmentRepository.findByPatientId(id);

        LocalDateTime now = LocalDateTime.now();
        List<AppointmentDTO> filtered = appointments.stream()
                .filter(a -> condition.equalsIgnoreCase("past") ? a.getAppointmentTime().isBefore(now)
                        : a.getAppointmentTime().isAfter(now))
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        response.put("appointments", filtered);
        return ResponseEntity.ok(response);
    }

    // 4. Filter by doctor name
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        List<AppointmentDTO> appointments = appointmentRepository
                .filterByDoctorNameAndPatientId(name, patientId)
                .stream().map(AppointmentDTO::new).collect(Collectors.toList());

        response.put("appointments", appointments);
        return ResponseEntity.ok(response);
    }

    // 5. Filter by doctor and condition
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();
        List<Appointment> appointments = appointmentRepository
                .filterByDoctorNameAndPatientId(name, patientId);

        LocalDateTime now = LocalDateTime.now();
        List<AppointmentDTO> filtered = appointments.stream()
                .filter(a -> condition.equalsIgnoreCase("past") ? a.getAppointmentTime().isBefore(now)
                        : a.getAppointmentTime().isAfter(now))
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        response.put("appointments", filtered);
        return ResponseEntity.ok(response);
    }

    // 6. Get patient details from token
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        String email = tokenService.getEmailFromToken(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null) {
            response.put("error", "Patient not found");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("patient", patient);
        return ResponseEntity.ok(response);
    }
}
