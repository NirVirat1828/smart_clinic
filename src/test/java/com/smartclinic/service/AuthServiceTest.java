package com.smartclinic.service;

import com.smartclinic.dto.LoginRequest;
import com.smartclinic.dto.UserRegistrationRequest;
import com.smartclinic.model.Doctor;
import com.smartclinic.model.Patient;
import com.smartclinic.model.User;
import com.smartclinic.repository.mysql.DoctorRepository;
import com.smartclinic.repository.mysql.PatientRepository;
import com.smartclinic.repository.mysql.UserRepository;
import com.smartclinic.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private UserRegistrationRequest doctorRegistrationRequest;
    private UserRegistrationRequest patientRegistrationRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        doctorRegistrationRequest = new UserRegistrationRequest();
        doctorRegistrationRequest.setName("Dr. John Smith");
        doctorRegistrationRequest.setEmail("doctor@test.com");
        doctorRegistrationRequest.setPassword("password123");
        doctorRegistrationRequest.setRole("DOCTOR");
        doctorRegistrationRequest.setSpecialization("Cardiology");

        patientRegistrationRequest = new UserRegistrationRequest();
        patientRegistrationRequest.setName("Jane Doe");
        patientRegistrationRequest.setEmail("patient@test.com");
        patientRegistrationRequest.setPassword("password123");
        patientRegistrationRequest.setRole("PATIENT");
        patientRegistrationRequest.setAge(30);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");
        user.setRole(User.Role.DOCTOR);
    }

    @Test
    void registerUser_ShouldCreateDoctorSuccessfully() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(new Doctor());

        // When
        Map<String, Object> result = authService.registerUser(doctorRegistrationRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("userId"));
        assertEquals("test@test.com", result.get("email"));
        assertEquals("DOCTOR", result.get("role"));
        assertEquals("Test User", result.get("name"));

        verify(userRepository).existsByEmail("doctor@test.com");
        verify(userRepository).save(any(User.class));
        verify(doctorRepository).save(any(Doctor.class));
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void registerUser_ShouldCreatePatientSuccessfully() {
        // Given
        User patientUser = new User();
        patientUser.setId(2L);
        patientUser.setName("Jane Doe");
        patientUser.setEmail("patient@test.com");
        patientUser.setRole(User.Role.PATIENT);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(patientUser);
        when(patientRepository.save(any(Patient.class))).thenReturn(new Patient());

        // When
        Map<String, Object> result = authService.registerUser(patientRegistrationRequest);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.get("userId"));
        assertEquals("patient@test.com", result.get("email"));
        assertEquals("PATIENT", result.get("role"));
        assertEquals("Jane Doe", result.get("name"));

        verify(userRepository).existsByEmail("patient@test.com");
        verify(userRepository).save(any(User.class));
        verify(patientRepository).save(any(Patient.class));
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void registerUser_ShouldThrowExceptionWhenUserAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.registerUser(doctorRegistrationRequest));
        
        assertEquals("User with email doctor@test.com already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnJwtTokenSuccessfully() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // When
        Map<String, Object> result = authService.login(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals("jwt-token", result.get("token"));
        assertEquals("Bearer", result.get("type"));
        assertEquals(1L, result.get("userId"));
        assertEquals("test@test.com", result.get("email"));
        assertEquals("DOCTOR", result.get("role"));
        assertEquals("Test User", result.get("name"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateJwtToken(authentication);
        verify(userRepository).findByEmail("test@test.com");
    }

    @Test
    void login_ShouldThrowExceptionWhenUserNotFound() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.login(loginRequest));
        
        assertEquals("User not found", exception.getMessage());
    }
}