package com.smartclinic.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclinic.dto.AppointmentRequest;
import com.smartclinic.dto.MedicalHistoryRequest;
import com.smartclinic.dto.PrescriptionRequest;
import com.smartclinic.dto.UserRegistrationRequest;
import com.smartclinic.repository.mysql.DoctorRepository;
import com.smartclinic.repository.mysql.PatientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.smartclinic.smart_clinic.SmartClinicApplication;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = SmartClinicApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmartClinicSmokeTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

        // Ensure unique emails per test run to avoid duplicate registration errors
        private final String runId = String.valueOf(System.currentTimeMillis());
        private String doctorEmail = "doc+" + runId + "@example.com";
        private String patientEmail = "pat+" + runId + "@example.com";

    private String doctorToken;
    private String patientToken;

    private Long doctorId; // MySQL Doctor.id
    private Long patientId; // MySQL Patient.id

    @Test
    @Order(1)
    void registerAndLoginUsers() throws Exception {
        // Register Doctor
        UserRegistrationRequest docReq = new UserRegistrationRequest(
                "Dr. Strange",
                doctorEmail,
                "password123",
                "DOCTOR",
                "Cardiology",
                null
        );
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(docReq)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Register Patient
        UserRegistrationRequest patReq = new UserRegistrationRequest(
                "Peter Parker",
                patientEmail,
                "password123",
                "PATIENT",
                null,
                21
        );
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patReq)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Login Doctor
        String docLoginJson = "{\"email\":\"" + doctorEmail + "\",\"password\":\"password123\"}";
        MvcResult docLogin = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(docLoginJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JsonNode docNode = objectMapper.readTree(docLogin.getResponse().getContentAsString());
        doctorToken = docNode.path("data").path("token").asText();
        Assertions.assertNotNull(doctorToken);

        // Login Patient
        String patLoginJson = "{\"email\":\"" + patientEmail + "\",\"password\":\"password123\"}";
        MvcResult patLogin = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patLoginJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JsonNode patNode = objectMapper.readTree(patLogin.getResponse().getContentAsString());
        patientToken = patNode.path("data").path("token").asText();
        Assertions.assertNotNull(patientToken);

        // Resolve Doctor/Patient IDs via repositories
        doctorId = doctorRepository.findByUserEmail(doctorEmail).orElseThrow().getId();
        patientId = patientRepository.findByUserEmail(patientEmail).orElseThrow().getId();

        Assertions.assertTrue(doctorId > 0);
        Assertions.assertTrue(patientId > 0);
    }

    @Test
    @Order(2)
    void bookAppointment() throws Exception {
        // Ensure tokens and IDs are available (login if needed)
        if (doctorToken == null || patientToken == null || doctorId == null || patientId == null) {
            registerAndLoginUsers();
        }

        AppointmentRequest req = new AppointmentRequest(
                patientId,
                doctorId,
                LocalDateTime.now().plusDays(1)
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/appointments")
                        .header("Authorization", "Bearer " + patientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").exists());
    }

    @Test
    @Order(3)
    void createAndFetchPrescription() throws Exception {
        if (doctorToken == null || patientToken == null || doctorId == null || patientId == null) {
            registerAndLoginUsers();
        }

        PrescriptionRequest.MedicineDto med = new PrescriptionRequest.MedicineDto(
                "Amoxicillin", "500mg", "BID", 5, "After meals"
        );
        PrescriptionRequest preq = new PrescriptionRequest(
                patientId,
                doctorId,
                List.of(med),
                "Take as directed"
        );

        MvcResult createRes = mockMvc.perform(MockMvcRequestBuilders.post("/api/prescriptions")
                        .header("Authorization", "Bearer " + doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(preq)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andReturn();

        JsonNode node = objectMapper.readTree(createRes.getResponse().getContentAsString());
        String prescriptionId = node.path("data").path("id").asText();
        Assertions.assertNotNull(prescriptionId);

        // Fetch by id with patient token
        mockMvc.perform(MockMvcRequestBuilders.get("/api/prescriptions/" + prescriptionId)
                        .header("Authorization", "Bearer " + patientToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(prescriptionId));
    }

    @Test
    @Order(4)
    void addAndFetchMedicalHistory() throws Exception {
        if (doctorToken == null || patientToken == null || doctorId == null || patientId == null) {
            registerAndLoginUsers();
        }

        MedicalHistoryRequest.MedicalRecordDto rec = new MedicalHistoryRequest.MedicalRecordDto(
                "DIAGNOSIS",
                "Mild fever",
                "Rest and hydration",
                doctorId,
                List.of()
        );
        MedicalHistoryRequest hreq = new MedicalHistoryRequest(
                patientId,
                rec
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/medical-history")
                        .header("Authorization", "Bearer " + doctorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hreq)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/medical-history/patient/" + patientId)
                        .header("Authorization", "Bearer " + patientToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"));
    }
}
