package com.smartclinic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for medical history request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistoryRequest {
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Medical record is required")
    private MedicalRecordDto record;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicalRecordDto {
        private String recordType; // DIAGNOSIS, TREATMENT, TEST_RESULT, etc.
        private String description;
        private String doctorNotes;
        private Long doctorId;
        private List<String> attachments; // file paths or IDs
    }
}