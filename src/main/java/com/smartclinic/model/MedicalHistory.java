package com.smartclinic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Medical History document for MongoDB
 * Stores patient's medical records and history
 */
@Document(collection = "medical_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistory {
    
    @Id
    private String id;
    
    private Long patientId;
    
    private List<MedicalRecord> records;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicalRecord {
        private String recordType; // DIAGNOSIS, TREATMENT, TEST_RESULT, etc.
        private String description;
        private String doctorNotes;
        private Long doctorId;
        private LocalDateTime recordDate;
        private List<String> attachments; // file paths or IDs
    }
}