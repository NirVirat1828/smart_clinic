package com.smartclinic.service;

import com.smartclinic.dto.PrescriptionRequest;
import com.smartclinic.model.Prescription;
import com.smartclinic.repository.mongodb.PrescriptionRepository;
import com.smartclinic.repository.mysql.DoctorRepository;
import com.smartclinic.repository.mysql.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for prescription management operations in MongoDB
 */
@Service
@RequiredArgsConstructor
public class PrescriptionService {
    
    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    
    public Prescription createPrescription(PrescriptionRequest request) {
        // Validate patient and doctor exist
        if (!patientRepository.existsById(request.getPatientId())) {
            throw new RuntimeException("Patient not found");
        }
        
        if (!doctorRepository.existsById(request.getDoctorId())) {
            throw new RuntimeException("Doctor not found");
        }
        
        // Convert DTOs to entities
        List<Prescription.Medicine> medicines = request.getMedicineList().stream()
            .map(dto -> new Prescription.Medicine(
                dto.getName(),
                dto.getDosage(),
                dto.getFrequency(),
                dto.getDuration(),
                dto.getInstructions()
            ))
            .collect(Collectors.toList());
        
        Prescription prescription = new Prescription();
        prescription.setPatientId(request.getPatientId());
        prescription.setDoctorId(request.getDoctorId());
        prescription.setMedicineList(medicines);
        prescription.setNotes(request.getNotes());
        prescription.setCreatedAt(LocalDateTime.now());
        
        return prescriptionRepository.save(prescription);
    }
    
    public List<Prescription> getPatientPrescriptions(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new RuntimeException("Patient not found");
        }
        return prescriptionRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }
    
    public List<Prescription> getDoctorPrescriptions(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new RuntimeException("Doctor not found");
        }
        return prescriptionRepository.findByDoctorId(doctorId);
    }
    
    public Prescription getPrescriptionById(String id) {
        return prescriptionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Prescription not found"));
    }
    
    public Prescription updatePrescription(String id, PrescriptionRequest request) {
        Prescription existingPrescription = prescriptionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Prescription not found"));
        
        // Validate patient and doctor exist
        if (!patientRepository.existsById(request.getPatientId())) {
            throw new RuntimeException("Patient not found");
        }
        
        if (!doctorRepository.existsById(request.getDoctorId())) {
            throw new RuntimeException("Doctor not found");
        }
        
        // Convert DTOs to entities
        List<Prescription.Medicine> medicines = request.getMedicineList().stream()
            .map(dto -> new Prescription.Medicine(
                dto.getName(),
                dto.getDosage(),
                dto.getFrequency(),
                dto.getDuration(),
                dto.getInstructions()
            ))
            .collect(Collectors.toList());
        
        // Update prescription
        existingPrescription.setPatientId(request.getPatientId());
        existingPrescription.setDoctorId(request.getDoctorId());
        existingPrescription.setMedicineList(medicines);
        existingPrescription.setNotes(request.getNotes());
        
        return prescriptionRepository.save(existingPrescription);
    }
    
    public void deletePrescription(String id) {
        if (!prescriptionRepository.existsById(id)) {
            throw new RuntimeException("Prescription not found");
        }
        prescriptionRepository.deleteById(id);
    }
}