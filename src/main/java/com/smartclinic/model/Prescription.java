package com.smartclinic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Prescription document for MongoDB
 * Stores prescription information with medicine list and notes
 */
@Document(collection = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    
    @Id
    private String id;
    
    private Long patientId;
    
    private Long doctorId;
    
    private List<Medicine> medicineList;
    
    private String notes;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Medicine {
        private String name;
        private String dosage;
        private String frequency;
        private Integer duration; // in days
        private String instructions;
    }
}