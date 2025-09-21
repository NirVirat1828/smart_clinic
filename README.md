# Smart Health Clinic Backend

A comprehensive backend system for managing a smart health clinic with dual database architecture (MySQL + MongoDB) built with Spring Boot 3 and Java 21.

## 🏗️ Architecture Overview

### Technology Stack
- **Java**: 21 (LTS)
- **Spring Boot**: 3.5.6
- **Databases**: MySQL (clinic_db) + MongoDB (prescriptionsdb)
- **Security**: Spring Security (Initially Disabled)
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven

### Database Architecture
- **MySQL (clinic_db)**: Stores relational data (Users, Doctors, Patients, Appointments)
- **MongoDB (prescriptionsdb)**: Stores document-based data (Prescriptions, Medical History)

## 📁 Project Structure

```
com.smartclinic/
├── config/          # Configuration classes
├── controller/      # REST Controllers
├── dto/            # Data Transfer Objects
├── exception/      # Global exception handling
├── model/          # JPA Entities & MongoDB Documents
├── repository/     # Repository interfaces
│   ├── mysql/      # JPA Repositories
│   └── mongodb/    # MongoDB Repositories
└── service/        # Business logic layer
```

## 🗄️ Database Schema

### MySQL Tables (clinic_db)
- **users**: User authentication and basic info
- **doctors**: Doctor-specific information
- **patients**: Patient-specific information  
- **appointments**: Appointment scheduling

### MongoDB Collections (prescriptionsdb)
- **prescriptions**: Medicine prescriptions with detailed information
- **medical_history**: Patient medical records and history

## 🚀 API Endpoints

### Authentication (Security Disabled)
- `POST /api/auth/register` - Register new user (ADMIN/DOCTOR/PATIENT)
- `POST /api/auth/login` - Login (returns dummy token)

### Appointment Management
- `POST /api/appointments` - Book new appointment
- `GET /api/appointments/patient/{id}` - Get patient appointments
- `GET /api/appointments/doctor/{id}` - Get doctor appointments
- `PUT /api/appointments/{id}/status` - Update appointment status

### Prescription Management (MongoDB)
- `POST /api/prescriptions` - Create new prescription
- `GET /api/prescriptions/patient/{id}` - Get patient prescriptions

### Medical History (MongoDB)
- `POST /api/medical-history` - Add medical record
- `GET /api/medical-history/patient/{id}` - Get patient medical history

## 🛠️ Setup Instructions

### Prerequisites
- Java 21+
- MySQL 8.0+
- MongoDB 4.4+
- Maven 3.6+

### Database Setup

#### MySQL Setup
```sql
CREATE DATABASE clinic_db;
-- Tables will be auto-created by Hibernate
```

#### MongoDB Setup
```bash
# Start MongoDB service
# Databases and collections will be auto-created
```

### Application Configuration

1. **Clone the repository**
2. **Update database credentials** in `application.properties`:
   ```properties
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   ```

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access Swagger UI** (when running):
   ```
   http://localhost:8080/swagger-ui.html
   ```

## 🧪 Testing Workflow (Without Security)

### 1. Register Users
```bash
# Register a Doctor
POST /api/auth/register
{
    "name": "Dr. John Smith",
    "email": "doctor@clinic.com",
    "password": "password123",
    "role": "DOCTOR",
    "specialization": "Cardiology"
}

# Register a Patient
POST /api/auth/register
{
    "name": "Jane Doe",
    "email": "patient@clinic.com", 
    "password": "password123",
    "role": "PATIENT",
    "age": 30
}
```

### 2. Login
```bash
POST /api/auth/login
{
    "email": "doctor@clinic.com",
    "password": "password123"
}
```

### 3. Book Appointment
```bash
POST /api/appointments
{
    "patientId": 1,
    "doctorId": 1,
    "date": "2024-12-25T10:00:00"
}
```

### 4. Create Prescription
```bash
POST /api/prescriptions
{
    "patientId": 1,
    "doctorId": 1,
    "medicineList": [
        {
            "name": "Amoxicillin",
            "dosage": "500mg",
            "frequency": "3 times daily",
            "duration": 7,
            "instructions": "Take with food"
        }
    ],
    "notes": "Complete the full course"
}
```

## 🔐 Security (Future Implementation)

Currently, security is **disabled** for development and testing. Future implementation will include:

- JWT-based authentication
- Role-based authorization:
  - **ADMIN**: Manage users and system settings
  - **DOCTOR**: Manage prescriptions and appointments
  - **PATIENT**: View appointments and medical history

## 📝 API Response Format

All APIs return a standard response format:

```json
{
    "status": "success|error",
    "message": "Description message",
    "data": { /* Response data */ }
}
```

## 🔧 Development Features

- **Automatic table creation** via Hibernate DDL
- **Input validation** with custom error messages  
- **Global exception handling** with proper HTTP status codes
- **CORS enabled** for frontend integration
- **Detailed logging** for debugging
- **Connection pooling** with HikariCP

## 🚀 Next Steps

1. Enable JWT authentication and role-based security
2. Add unit and integration tests
3. Implement file upload for medical attachments
4. Add notification system for appointments
5. Create audit logging for all operations
6. Add caching layer with Redis
7. Implement API rate limiting

## 📊 Monitoring

- **Health Check**: `/actuator/health`
- **Application Info**: `/actuator/info`
- **Swagger Documentation**: `/swagger-ui.html`

---

**Note**: This application is ready for immediate testing with security disabled. Enable security features in production environments.