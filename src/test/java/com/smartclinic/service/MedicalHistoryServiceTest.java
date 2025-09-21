package com.smartclinic.service;

import com.smartclinic.dto.MedicalHistoryRequest;
import com.smartclinic.model.MedicalHistory;
import com.smartclinic.repository.mongodb.MedicalHistoryRepository;
import com.smartclinic.repository.mysql.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalHistoryServiceTest {

    @Mock
    private MedicalHistoryRepository medicalHistoryRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private MedicalHistoryService medicalHistoryService;

    private MedicalHistoryRequest medicalHistoryRequest;
    private MedicalHistory medicalHistory;

    @BeforeEach
    void setUp() {
        // Create medical record DTO
        MedicalHistoryRequest.MedicalRecordDto recordDto = new MedicalHistoryRequest.MedicalRecordDto();
        recordDto.setRecordType("DIAGNOSIS");
        recordDto.setDescription("High blood pressure");
        recordDto.setDoctorNotes("Monitor blood pressure regularly");
        recordDto.setDoctorId(1L);
        recordDto.setAttachments(Arrays.asList("attachment1.pdf"));

        medicalHistoryRequest = new MedicalHistoryRequest();
        medicalHistoryRequest.setPatientId(1L);
        medicalHistoryRequest.setRecord(recordDto);

        // Create medical record
        MedicalHistory.MedicalRecord record = new MedicalHistory.MedicalRecord();
        record.setRecordType("DIAGNOSIS");
        record.setDescription("High blood pressure");
        record.setDoctorNotes("Monitor blood pressure regularly");
        record.setDoctorId(1L);
        record.setRecordDate(LocalDateTime.now());
        record.setAttachments(Arrays.asList("attachment1.pdf"));

        medicalHistory = new MedicalHistory();
        medicalHistory.setId("history123");
        medicalHistory.setPatientId(1L);
        medicalHistory.setRecords(new ArrayList<>());
        medicalHistory.getRecords().add(record);
        medicalHistory.setCreatedAt(LocalDateTime.now());
        medicalHistory.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void addMedicalRecord_ShouldReturnMedicalHistory_WhenValidRequestAndPatientExists() {
        // Arrange
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(medicalHistoryRepository.findByPatientId(1L)).thenReturn(Optional.empty());
        when(medicalHistoryRepository.save(any(MedicalHistory.class))).thenReturn(medicalHistory);

        // Act
        MedicalHistory result = medicalHistoryService.addMedicalRecord(medicalHistoryRequest);

        // Assert
        assertNotNull(result);
        assertEquals(medicalHistory.getId(), result.getId());
        assertEquals(medicalHistory.getPatientId(), result.getPatientId());
        assertNotNull(result.getRecords());
        assertEquals(1, result.getRecords().size());
        
        verify(patientRepository, times(1)).existsById(1L);
        verify(medicalHistoryRepository, times(1)).findByPatientId(1L);
        verify(medicalHistoryRepository, times(1)).save(any(MedicalHistory.class));
    }

    @Test
    void addMedicalRecord_ShouldAddToExistingHistory_WhenPatientHistoryExists() {
        // Arrange
        MedicalHistory existingHistory = new MedicalHistory();
        existingHistory.setId("existing123");
        existingHistory.setPatientId(1L);
        existingHistory.setRecords(new ArrayList<>());
        
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(medicalHistoryRepository.findByPatientId(1L)).thenReturn(Optional.of(existingHistory));
        when(medicalHistoryRepository.save(any(MedicalHistory.class))).thenReturn(existingHistory);

        // Act
        MedicalHistory result = medicalHistoryService.addMedicalRecord(medicalHistoryRequest);

        // Assert
        assertNotNull(result);
        assertEquals(existingHistory.getId(), result.getId());
        assertEquals(existingHistory.getPatientId(), result.getPatientId());
        
        verify(patientRepository, times(1)).existsById(1L);
        verify(medicalHistoryRepository, times(1)).findByPatientId(1L);
        verify(medicalHistoryRepository, times(1)).save(any(MedicalHistory.class));
    }

    @Test
    void addMedicalRecord_ShouldThrowException_WhenPatientNotFound() {
        // Arrange
        when(patientRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            medicalHistoryService.addMedicalRecord(medicalHistoryRequest);
        });

        assertEquals("Patient not found", exception.getMessage());
        verify(patientRepository, times(1)).existsById(1L);
        verify(medicalHistoryRepository, never()).findByPatientId(any());
        verify(medicalHistoryRepository, never()).save(any());
    }

    @Test
    void getPatientMedicalHistory_ShouldReturnMedicalHistory_WhenPatientExists() {
        // Arrange
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(medicalHistoryRepository.findByPatientId(1L)).thenReturn(Optional.of(medicalHistory));

        // Act
        Optional<MedicalHistory> result = medicalHistoryService.getPatientMedicalHistory(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(medicalHistory.getId(), result.get().getId());
        assertEquals(medicalHistory.getPatientId(), result.get().getPatientId());
        
        verify(patientRepository, times(1)).existsById(1L);
        verify(medicalHistoryRepository, times(1)).findByPatientId(1L);
    }

    @Test
    void getPatientMedicalHistory_ShouldReturnEmpty_WhenNoHistoryExists() {
        // Arrange
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(medicalHistoryRepository.findByPatientId(1L)).thenReturn(Optional.empty());

        // Act
        Optional<MedicalHistory> result = medicalHistoryService.getPatientMedicalHistory(1L);

        // Assert
        assertFalse(result.isPresent());
        
        verify(patientRepository, times(1)).existsById(1L);
        verify(medicalHistoryRepository, times(1)).findByPatientId(1L);
    }

    @Test
    void getPatientMedicalHistory_ShouldThrowException_WhenPatientNotFound() {
        // Arrange
        when(patientRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            medicalHistoryService.getPatientMedicalHistory(1L);
        });

        assertEquals("Patient not found", exception.getMessage());
        verify(patientRepository, times(1)).existsById(1L);
        verify(medicalHistoryRepository, never()).findByPatientId(any());
    }
}