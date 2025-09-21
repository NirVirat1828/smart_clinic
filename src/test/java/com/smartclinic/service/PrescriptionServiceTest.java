package com.smartclinic.service;

import com.smartclinic.dto.PrescriptionRequest;
import com.smartclinic.model.Prescription;
import com.smartclinic.repository.mongodb.PrescriptionRepository;
import com.smartclinic.repository.mysql.DoctorRepository;
import com.smartclinic.repository.mysql.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private PrescriptionService prescriptionService;

    private PrescriptionRequest prescriptionRequest;
    private Prescription prescription;

    @BeforeEach
    void setUp() {
        // Create medicine DTOs
        PrescriptionRequest.MedicineDto medicine1 = new PrescriptionRequest.MedicineDto();
        medicine1.setName("Medication A");
        medicine1.setDosage("10mg");
        medicine1.setFrequency("Twice daily");
        medicine1.setDuration(7);
        medicine1.setInstructions("Take with food");

        PrescriptionRequest.MedicineDto medicine2 = new PrescriptionRequest.MedicineDto();
        medicine2.setName("Medication B");
        medicine2.setDosage("5mg");
        medicine2.setFrequency("Once daily");
        medicine2.setDuration(14);
        medicine2.setInstructions("Take before sleep");

        prescriptionRequest = new PrescriptionRequest();
        prescriptionRequest.setPatientId(1L);
        prescriptionRequest.setDoctorId(1L);
        prescriptionRequest.setMedicineList(Arrays.asList(medicine1, medicine2));
        prescriptionRequest.setNotes("Follow up in 2 weeks");

        // Create prescription medicines
        Prescription.Medicine prescMedicine1 = new Prescription.Medicine();
        prescMedicine1.setName("Medication A");
        prescMedicine1.setDosage("10mg");
        prescMedicine1.setFrequency("Twice daily");
        prescMedicine1.setDuration(7);
        prescMedicine1.setInstructions("Take with food");

        Prescription.Medicine prescMedicine2 = new Prescription.Medicine();
        prescMedicine2.setName("Medication B");
        prescMedicine2.setDosage("5mg");
        prescMedicine2.setFrequency("Once daily");
        prescMedicine2.setDuration(14);
        prescMedicine2.setInstructions("Take before sleep");

        prescription = new Prescription();
        prescription.setId("prescription123");
        prescription.setPatientId(1L);
        prescription.setDoctorId(1L);
        prescription.setMedicineList(Arrays.asList(prescMedicine1, prescMedicine2));
        prescription.setNotes("Follow up in 2 weeks");
        prescription.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createPrescription_ShouldReturnPrescription_WhenValidRequest() {
        // Arrange
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(doctorRepository.existsById(1L)).thenReturn(true);
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(prescription);

        // Act
        Prescription result = prescriptionService.createPrescription(prescriptionRequest);

        // Assert
        assertNotNull(result);
        assertEquals(prescription.getId(), result.getId());
        assertEquals(prescription.getPatientId(), result.getPatientId());
        assertEquals(prescription.getDoctorId(), result.getDoctorId());
        assertEquals(prescription.getMedicineList().size(), result.getMedicineList().size());
        assertEquals(prescription.getNotes(), result.getNotes());
        
        verify(patientRepository, times(1)).existsById(1L);
        verify(doctorRepository, times(1)).existsById(1L);
        verify(prescriptionRepository, times(1)).save(any(Prescription.class));
    }

    @Test
    void createPrescription_ShouldThrowException_WhenPatientNotFound() {
        // Arrange
        when(patientRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            prescriptionService.createPrescription(prescriptionRequest);
        });

        assertEquals("Patient not found", exception.getMessage());
        verify(patientRepository, times(1)).existsById(1L);
        verify(doctorRepository, never()).existsById(any());
        verify(prescriptionRepository, never()).save(any());
    }

    @Test
    void createPrescription_ShouldThrowException_WhenDoctorNotFound() {
        // Arrange
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(doctorRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            prescriptionService.createPrescription(prescriptionRequest);
        });

        assertEquals("Doctor not found", exception.getMessage());
        verify(patientRepository, times(1)).existsById(1L);
        verify(doctorRepository, times(1)).existsById(1L);
        verify(prescriptionRepository, never()).save(any());
    }

    @Test
    void getPatientPrescriptions_ShouldReturnPrescriptions() {
        // Arrange
        List<Prescription> prescriptions = Arrays.asList(prescription);
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(prescriptionRepository.findByPatientIdOrderByCreatedAtDesc(1L)).thenReturn(prescriptions);

        // Act
        List<Prescription> result = prescriptionService.getPatientPrescriptions(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(prescription.getId(), result.get(0).getId());
        verify(patientRepository, times(1)).existsById(1L);
        verify(prescriptionRepository, times(1)).findByPatientIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getPatientPrescriptions_ShouldThrowException_WhenPatientNotFound() {
        // Arrange
        when(patientRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            prescriptionService.getPatientPrescriptions(1L);
        });

        assertEquals("Patient not found", exception.getMessage());
        verify(patientRepository, times(1)).existsById(1L);
        verify(prescriptionRepository, never()).findByPatientIdOrderByCreatedAtDesc(any());
    }

    @Test
    void getDoctorPrescriptions_ShouldReturnPrescriptions() {
        // Arrange
        List<Prescription> prescriptions = Arrays.asList(prescription);
        when(doctorRepository.existsById(1L)).thenReturn(true);
        when(prescriptionRepository.findByDoctorId(1L)).thenReturn(prescriptions);

        // Act
        List<Prescription> result = prescriptionService.getDoctorPrescriptions(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(prescription.getId(), result.get(0).getId());
        verify(doctorRepository, times(1)).existsById(1L);
        verify(prescriptionRepository, times(1)).findByDoctorId(1L);
    }

    @Test
    void getDoctorPrescriptions_ShouldThrowException_WhenDoctorNotFound() {
        // Arrange
        when(doctorRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            prescriptionService.getDoctorPrescriptions(1L);
        });

        assertEquals("Doctor not found", exception.getMessage());
        verify(doctorRepository, times(1)).existsById(1L);
        verify(prescriptionRepository, never()).findByDoctorId(any());
    }
}