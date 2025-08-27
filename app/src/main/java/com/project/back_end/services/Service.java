package com.example.service;

import com.example.dto.Login;
import com.example.model.Admin;
import com.example.model.Appointment;
import com.example.model.Doctor;
import com.example.model.Patient;
import com.example.repository.AdminRepository;
import com.example.repository.DoctorRepository;
import com.example.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public Service(TokenService tokenService, AdminRepository adminRepository, DoctorRepository doctorRepository,
                   PatientRepository patientRepository, DoctorService doctorService, PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // 1. Validate Token
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        if (!tokenService.validateToken(token, user)) {
            response.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(response);
        }
        response.put("message", "Token valid");
        return ResponseEntity.ok(response);
    }

    // 2. Validate Admin Login
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
        if (admin != null && admin.getPassword().equals(receivedAdmin.getPassword())) {
            String token = tokenService.generateToken(admin.getUsername(), "admin");
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        response.put("error", "Invalid credentials");
        return ResponseEntity.status(401).body(response);
    }

    // 3. Filter Doctors
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
    }

    // 4. Validate Appointment
    public int validateAppointment(Appointment appointment) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctorId());
        if (!doctorOpt.isPresent()) return -1; // doctor doesn't exist

        List<String> availableSlots = doctorService.getDoctorAvailability(appointment.getDoctorId(),
                appointment.getAppointmentTime().toLocalDate());
        if (availableSlots.contains(appointment.getAppointmentTime().toLocalTime().toString())) {
            return 1; // valid
        }
        return 0; // unavailable
    }

    // 5. Validate Patient Existence
    public boolean validatePatient(Patient patient) {
        Patient existing = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        return existing == null;
    }

    // 6. Validate Patient Login
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        Patient patient = patientRepository.findByEmail(login.getIdentifier());
        if (patient != null && patient.getPassword().equals(login.getPassword())) {
            String token = tokenService.generateToken(patient.getEmail(), "patient");
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        response.put("error", "Invalid credentials");
        return ResponseEntity.status(401).body(response);
    }

    // 7. Filter Patient Appointments
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();
        String email = tokenService.getEmailFromToken(token);
        Patient patient = patientRepository.findByEmail(email);
        if (patient == null) {
            response.put("error", "Unauthorized");
            return ResponseEntity.status(401).body(response);
        }

        if (condition != null && name != null) {
            return patientService.filterByDoctorAndCondition(condition, name, patient.getId());
        } else if (condition != null) {
            return patientService.filterByCondition(condition, patient.getId());
        } else if (name != null) {
            return patientService.filterByDoctor(name, patient.getId());
        } else {
            return patientService.getPatientAppointment(patient.getId(), token);
        }
    }
}
