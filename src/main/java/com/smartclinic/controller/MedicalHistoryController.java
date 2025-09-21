package com.smartclinic.controller;

import com.smartclinic.dto.ApiResponse;
import com.smartclinic.dto.MedicalHistoryRequest;
import com.smartclinic.model.MedicalHistory;
import com.smartclinic.service.MedicalHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for medical history management operations
 */
@RestController
@RequestMapping("/api/medical-history")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Medical History", description = "Patient medical history management APIs")
public class MedicalHistoryController {
    
    private final MedicalHistoryService medicalHistoryService;
    
    @PostMapping
    @Operation(summary = "Add a medical record (Doctor only)")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<MedicalHistory>> addMedicalRecord(@Valid @RequestBody MedicalHistoryRequest request) {
        try {
            MedicalHistory medicalHistory = medicalHistoryService.addMedicalRecord(request);
            return ResponseEntity.ok(ApiResponse.success("Medical record added successfully", medicalHistory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get medical history by ID")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<MedicalHistory>> getMedicalHistoryById(@PathVariable String id) {
        try {
            MedicalHistory medicalHistory = medicalHistoryService.getMedicalHistoryById(id);
            return ResponseEntity.ok(ApiResponse.success("Medical history retrieved successfully", medicalHistory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/record")
    @Operation(summary = "Update a medical history record (Doctor only)")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<MedicalHistory>> updateMedicalRecord(@PathVariable String id, @Valid @RequestBody MedicalHistoryRequest request) {
        try {
            MedicalHistory medicalHistory = medicalHistoryService.updateMedicalRecord(id, request);
            return ResponseEntity.ok(ApiResponse.success("Medical record updated successfully", medicalHistory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an entire medical history (Doctor only)")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<String>> deleteMedicalHistory(@PathVariable String id) {
        try {
            medicalHistoryService.deleteMedicalHistory(id);
            return ResponseEntity.ok(ApiResponse.success("Medical history deleted successfully", "Medical history with ID " + id + " has been deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}/record/{recordIndex}")
    @Operation(summary = "Delete a medical history record by index (Doctor only)")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<MedicalHistory>> deleteMedicalRecord(@PathVariable String id, @PathVariable int recordIndex) {
        try {
            MedicalHistory medicalHistory = medicalHistoryService.deleteMedicalRecord(id, recordIndex);
            return ResponseEntity.ok(ApiResponse.success("Medical record deleted successfully", medicalHistory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get a patient's medical history")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<MedicalHistory>> getPatientMedicalHistory(@PathVariable Long patientId) {
        try {
            Optional<MedicalHistory> medicalHistory = medicalHistoryService.getPatientMedicalHistory(patientId);
            if (medicalHistory.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Medical history retrieved successfully", medicalHistory.get()));
            } else {
                return ResponseEntity.ok(ApiResponse.success("No medical history found for patient", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/all")
    @Operation(summary = "Get all medical histories (Doctor only)")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<MedicalHistory>>> getAllMedicalHistories() {
        try {
            List<MedicalHistory> medicalHistories = medicalHistoryService.getAllMedicalHistories();
            return ResponseEntity.ok(ApiResponse.success("All medical histories retrieved successfully", medicalHistories));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}