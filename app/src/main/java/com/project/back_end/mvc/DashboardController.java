package com.example.dashboard.controller;

import com.example.dashboard.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private TokenService tokenService;

    /**
     * Admin dashboard view
     * @param token JWT token passed as path variable
     * @return Thymeleaf view name or redirect if invalid
     */
    @GetMapping("/adminDashboard/{token}")
    public Object adminDashboard(@PathVariable String token) {
        Map<String, Object> validationResult = tokenService.validateToken(token, "admin");
        if (validationResult.isEmpty()) {
            // Token valid
            return "admin/adminDashboard";
        } else {
            // Invalid token, redirect to login
            return new RedirectView("http://localhost:8080/");
        }
    }

    /**
     * Doctor dashboard view
     * @param token JWT token passed as path variable
     * @return Thymeleaf view name or redirect if invalid
     */
    @GetMapping("/doctorDashboard/{token}")
    public Object doctorDashboard(@PathVariable String token) {
        Map<String, Object> validationResult = tokenService.validateToken(token, "doctor");
        if (validationResult.isEmpty()) {
            // Token valid
            return "doctor/doctorDashboard";
        } else {
            // Invalid token, redirect to login
            return new RedirectView("http://localhost:8080/");
        }
    }
}
