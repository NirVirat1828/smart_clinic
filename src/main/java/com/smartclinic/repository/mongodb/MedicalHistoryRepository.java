package com.smartclinic.repository.mongodb;

import com.smartclinic.model.MedicalHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for MedicalHistory document operations in MongoDB
 */
@Repository
public interface MedicalHistoryRepository extends MongoRepository<MedicalHistory, String> {
    
    Optional<MedicalHistory> findByPatientId(Long patientId);
    
    boolean existsByPatientId(Long patientId);
}