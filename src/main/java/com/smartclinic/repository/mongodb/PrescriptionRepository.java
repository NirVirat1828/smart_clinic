package com.smartclinic.repository.mongodb;

import com.smartclinic.model.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Prescription document operations in MongoDB
 */
@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {
    
    List<Prescription> findByPatientId(Long patientId);
    
    List<Prescription> findByDoctorId(Long doctorId);
    
    List<Prescription> findByPatientIdAndDoctorId(Long patientId, Long doctorId);
    
    List<Prescription> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Prescription> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}