package com.smartclinic.service;

import com.smartclinic.dto.MedicalHistoryRequest;
import com.smartclinic.model.MedicalHistory;
import com.smartclinic.repository.mongodb.MedicalHistoryRepository;
import com.smartclinic.repository.mysql.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for medical history management operations in MongoDB
 */
@Service
@RequiredArgsConstructor
public class MedicalHistoryService {
    
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final PatientRepository patientRepository;
    
    public MedicalHistory addMedicalRecord(MedicalHistoryRequest request) {
        // Validate patient exists
        if (!patientRepository.existsById(request.getPatientId())) {
            throw new RuntimeException("Patient not found");
        }
        
        // Convert DTO to entity
        MedicalHistory.MedicalRecord record = new MedicalHistory.MedicalRecord();
        record.setRecordType(request.getRecord().getRecordType());
        record.setDescription(request.getRecord().getDescription());
        record.setDoctorNotes(request.getRecord().getDoctorNotes());
        record.setDoctorId(request.getRecord().getDoctorId());
        record.setRecordDate(LocalDateTime.now());
        record.setAttachments(request.getRecord().getAttachments());
        
        // Find existing medical history or create new one
        Optional<MedicalHistory> existingHistory = medicalHistoryRepository.findByPatientId(request.getPatientId());
        
        MedicalHistory medicalHistory;
        if (existingHistory.isPresent()) {
            medicalHistory = existingHistory.get();
            medicalHistory.getRecords().add(record);
            medicalHistory.setUpdatedAt(LocalDateTime.now());
        } else {
            medicalHistory = new MedicalHistory();
            medicalHistory.setPatientId(request.getPatientId());
            medicalHistory.setRecords(new ArrayList<>());
            medicalHistory.getRecords().add(record);
            medicalHistory.setCreatedAt(LocalDateTime.now());
            medicalHistory.setUpdatedAt(LocalDateTime.now());
        }
        
        return medicalHistoryRepository.save(medicalHistory);
    }
    
    public Optional<MedicalHistory> getPatientMedicalHistory(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new RuntimeException("Patient not found");
        }
        return medicalHistoryRepository.findByPatientId(patientId);
    }
    
    public MedicalHistory getMedicalHistoryById(String id) {
        return medicalHistoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Medical history not found"));
    }
    
    public MedicalHistory updateMedicalRecord(String id, MedicalHistoryRequest request) {
        MedicalHistory existingHistory = medicalHistoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Medical history not found"));
        
        // Validate patient exists
        if (!patientRepository.existsById(request.getPatientId())) {
            throw new RuntimeException("Patient not found");
        }
        
        // Convert DTO to entity
        MedicalHistory.MedicalRecord record = new MedicalHistory.MedicalRecord();
        record.setRecordType(request.getRecord().getRecordType());
        record.setDescription(request.getRecord().getDescription());
        record.setDoctorNotes(request.getRecord().getDoctorNotes());
        record.setDoctorId(request.getRecord().getDoctorId());
        record.setRecordDate(LocalDateTime.now());
        record.setAttachments(request.getRecord().getAttachments());
        
        // Add new record to existing history
        existingHistory.getRecords().add(record);
        existingHistory.setUpdatedAt(LocalDateTime.now());
        
        return medicalHistoryRepository.save(existingHistory);
    }
    
    public void deleteMedicalHistory(String id) {
        if (!medicalHistoryRepository.existsById(id)) {
            throw new RuntimeException("Medical history not found");
        }
        medicalHistoryRepository.deleteById(id);
    }
    
    public MedicalHistory deleteMedicalRecord(String id, int recordIndex) {
        MedicalHistory existingHistory = medicalHistoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Medical history not found"));
        
        if (recordIndex < 0 || recordIndex >= existingHistory.getRecords().size()) {
            throw new RuntimeException("Invalid record index");
        }
        
        existingHistory.getRecords().remove(recordIndex);
        existingHistory.setUpdatedAt(LocalDateTime.now());
        
        return medicalHistoryRepository.save(existingHistory);
    }
    
    public List<MedicalHistory> getAllMedicalHistories() {
        return medicalHistoryRepository.findAll();
    }
}