package com.smartclinic.service;

import com.smartclinic.dto.AppointmentRequest;
import com.smartclinic.model.Appointment;
import com.smartclinic.model.Doctor;
import com.smartclinic.model.Patient;
import com.smartclinic.model.User;
import com.smartclinic.repository.mysql.AppointmentRepository;
import com.smartclinic.repository.mysql.DoctorRepository;
import com.smartclinic.repository.mysql.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AppointmentService
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private AppointmentRequest appointmentRequest;
    private Patient patient;
    private Doctor doctor;
    private User patientUser;
    private User doctorUser;

    @BeforeEach
    void setUp() {
        appointmentRequest = new AppointmentRequest();
        appointmentRequest.setPatientId(1L);
        appointmentRequest.setDoctorId(2L);
        appointmentRequest.setDate(LocalDateTime.now().plusDays(1));

        patientUser = new User();
        patientUser.setId(1L);
        patientUser.setName("Jane Doe");
        patientUser.setEmail("patient@test.com");
        patientUser.setRole(User.Role.PATIENT);

        doctorUser = new User();
        doctorUser.setId(2L);
        doctorUser.setName("Dr. Smith");
        doctorUser.setEmail("doctor@test.com");
        doctorUser.setRole(User.Role.DOCTOR);

        patient = new Patient();
        patient.setId(1L);
        patient.setAge(30);
        patient.setUser(patientUser);

        doctor = new Doctor();
        doctor.setId(2L);
        doctor.setSpecialization("Cardiology");
        doctor.setUser(doctorUser);
    }

    @Test
    void bookAppointment_ShouldCreateAppointmentSuccessfully() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        
        Appointment savedAppointment = new Appointment();
        savedAppointment.setId(1L);
        savedAppointment.setDate(appointmentRequest.getDate());
        savedAppointment.setStatus(Appointment.AppointmentStatus.PENDING);
        savedAppointment.setPatient(patient);
        savedAppointment.setDoctor(doctor);
        
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // When
        Appointment result = appointmentService.bookAppointment(appointmentRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(appointmentRequest.getDate(), result.getDate());
        assertEquals(Appointment.AppointmentStatus.PENDING, result.getStatus());
        assertEquals(patient, result.getPatient());
        assertEquals(doctor, result.getDoctor());

        verify(patientRepository).findById(1L);
        verify(doctorRepository).findById(2L);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_ShouldThrowExceptionWhenPatientNotFound() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> appointmentService.bookAppointment(appointmentRequest));
        
        assertEquals("Patient not found", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_ShouldThrowExceptionWhenDoctorNotFound() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> appointmentService.bookAppointment(appointmentRequest));
        
        assertEquals("Doctor not found", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_ShouldThrowExceptionWhenDateIsInPast() {
        // Given
        appointmentRequest.setDate(LocalDateTime.now().minusDays(1));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> appointmentService.bookAppointment(appointmentRequest));
        
        assertEquals("Appointment date must be in the future", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }
}