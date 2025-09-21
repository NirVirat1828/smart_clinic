package com.smartclinic.controller;

import com.smartclinic.dto.ApiResponse;
import com.smartclinic.dto.PrescriptionRequest;
import com.smartclinic.model.Prescription;
import com.smartclinic.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for prescription management operations
 */
@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Prescriptions", description = "Prescription management APIs")
public class PrescriptionController {
    
    private final PrescriptionService prescriptionService;
    
    @PostMapping
    @Operation(summary = "Create a prescription (Doctor only)")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Prescription>> createPrescription(@Valid @RequestBody PrescriptionRequest request) {
        try {
            Prescription prescription = prescriptionService.createPrescription(request);
            return ResponseEntity.ok(ApiResponse.success("Prescription created successfully", prescription));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get prescription by ID")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<Prescription>> getPrescriptionById(@PathVariable String id) {
        try {
            Prescription prescription = prescriptionService.getPrescriptionById(id);
            return ResponseEntity.ok(ApiResponse.success("Prescription retrieved successfully", prescription));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a prescription (Doctor only)")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Prescription>> updatePrescription(@PathVariable String id, @Valid @RequestBody PrescriptionRequest request) {
        try {
            Prescription prescription = prescriptionService.updatePrescription(id, request);
            return ResponseEntity.ok(ApiResponse.success("Prescription updated successfully", prescription));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a prescription (Doctor only)")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<String>> deletePrescription(@PathVariable String id) {
        try {
            prescriptionService.deletePrescription(id);
            return ResponseEntity.ok(ApiResponse.success("Prescription deleted successfully", "Prescription with ID " + id + " has been deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get prescriptions for a patient")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<List<Prescription>>> getPatientPrescriptions(@PathVariable Long patientId) {
        try {
            List<Prescription> prescriptions = prescriptionService.getPatientPrescriptions(patientId);
            return ResponseEntity.ok(ApiResponse.success("Patient prescriptions retrieved successfully", prescriptions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get prescriptions created by a doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<Prescription>>> getDoctorPrescriptions(@PathVariable Long doctorId) {
        try {
            List<Prescription> prescriptions = prescriptionService.getDoctorPrescriptions(doctorId);
            return ResponseEntity.ok(ApiResponse.success("Doctor prescriptions retrieved successfully", prescriptions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}