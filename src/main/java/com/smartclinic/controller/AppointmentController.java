package com.smartclinic.controller;

import com.smartclinic.dto.ApiResponse;
import com.smartclinic.dto.AppointmentRequest;
import com.smartclinic.model.Appointment;
import com.smartclinic.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for appointment management operations
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Appointments", description = "Appointment management APIs")
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    
    @PostMapping
    @Operation(summary = "Book an appointment")
    public ResponseEntity<ApiResponse<Appointment>> bookAppointment(@Valid @RequestBody AppointmentRequest request) {
        try {
            Appointment appointment = appointmentService.bookAppointment(request);
            return ResponseEntity.ok(ApiResponse.success("Appointment booked successfully", appointment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get appointments for a patient")
    public ResponseEntity<ApiResponse<List<Appointment>>> getPatientAppointments(@PathVariable Long patientId) {
        try {
            List<Appointment> appointments = appointmentService.getPatientAppointments(patientId);
            return ResponseEntity.ok(ApiResponse.success("Patient appointments retrieved successfully", appointments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get appointments for a doctor")
    public ResponseEntity<ApiResponse<List<Appointment>>> getDoctorAppointments(@PathVariable Long doctorId) {
        try {
            List<Appointment> appointments = appointmentService.getDoctorAppointments(doctorId);
            return ResponseEntity.ok(ApiResponse.success("Doctor appointments retrieved successfully", appointments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{appointmentId}/status")
    @Operation(summary = "Update appointment status")
    public ResponseEntity<ApiResponse<Appointment>> updateAppointmentStatus(
            @PathVariable Long appointmentId, 
            @RequestParam String status) {
        try {
            Appointment appointment = appointmentService.updateAppointmentStatus(appointmentId, status);
            return ResponseEntity.ok(ApiResponse.success("Appointment status updated successfully", appointment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}