package com.smartclinic.repository.mysql;

import com.smartclinic.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Repository interface for Doctor entity operations in MySQL
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    Optional<Doctor> findByUserId(Long userId);
    
    List<Doctor> findBySpecialization(String specialization);
    
    Optional<Doctor> findByUserEmail(String email);
}