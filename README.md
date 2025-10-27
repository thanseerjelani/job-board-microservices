# 🚀 Job Board Microservices Platform

A full-featured job board application built with Spring Boot microservices architecture, demonstrating modern cloud-native development practices.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.1-blue)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.13-orange)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Features](#features)
- [Microservices](#microservices)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Deployment](#deployment)
- [Screenshots](#screenshots)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

This project is a production-grade job board platform that demonstrates the implementation of microservices architecture using Spring Cloud ecosystem. The platform allows employers to post jobs and manage applications, while job seekers can search for jobs and apply to positions.

### Key Highlights

- ✅ **Microservices Architecture** - Independently deployable services
- ✅ **Service Discovery** - Dynamic service registration with Eureka
- ✅ **API Gateway** - Centralized routing and load balancing
- ✅ **Authentication & Authorization** - JWT-based stateless authentication
- ✅ **Asynchronous Messaging** - Event-driven communication with RabbitMQ
- ✅ **Email Notifications** - Automated notifications for key events
- ✅ **RESTful APIs** - Well-designed REST endpoints
- ✅ **Database Per Service** - Separate databases for data isolation

## 🏗️ Architecture

### High-Level Architecture
```
┌─────────────────────────────────────────────────────────┐
│                      Client Layer                       │
│           (Web Browser / Mobile App / Postman)          │
└────────────────────────┬────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────┐
│                   API Gateway (8080)                    │
│              Single Entry Point / Load Balancer         │
└───────┬──────────────────────┬──────────────────────────┘
        │                      │
        ↓                      ↓
┌──────────────┐      ┌──────────────────┐
│ Auth Service │      │   Job Service    │
│    (8081)    │◄────►│     (8082)       │
│              │      │                  │
│ - Register   │      │ - Create Jobs    │
│ - Login      │      │ - Apply for Jobs │
│ - JWT Auth   │      │ - Search         │
│              │      │                  │
│ H2: authdb   │      │ H2: jobdb        │
└──────────────┘      └────────┬─────────┘
                               │
                               │ Publish Events
                               ↓
                      ┌─────────────────┐
                      │    RabbitMQ     │
                      │     (5672)      │
                      │  Message Broker │
                      └────────┬────────┘
                               │
                               │ Consume Events
                               ↓
                      ┌─────────────────────┐
                      │ Notification Service│
                      │       (8083)        │
                      │                     │
                      │ - Email Alerts      │
                      │ - Event Processing  │
                      └─────────────────────┘

All Services Register with:
┌─────────────────────────────────────┐
│       Eureka Server (8761)          │
│       Service Discovery             │
└─────────────────────────────────────┘
```

### Communication Patterns

1. **Synchronous Communication**
    - Client → API Gateway → Services (REST/HTTP)
    - Job Service → Auth Service (Feign Client)

2. **Asynchronous Communication**
    - Job Service → RabbitMQ → Notification Service (AMQP)

## 🛠️ Technologies

### Core Framework
- **Spring Boot 3.2.5** - Application framework
- **Spring Cloud 2023.0.1** - Microservices ecosystem
- **Java 17** - Programming language

### Microservices Components
- **Spring Cloud Netflix Eureka** - Service discovery
- **Spring Cloud Gateway** - API gateway and routing
- **Spring Cloud OpenFeign** - Declarative REST client

### Security
- **Spring Security 6** - Authentication and authorization
- **JWT (JSON Web Tokens)** - Stateless authentication
- **BCrypt** - Password hashing

### Messaging
- **RabbitMQ** - Message broker
- **Spring AMQP** - RabbitMQ integration

### Database
- **Spring Data JPA** - Data access layer
- **Hibernate** - ORM framework
- **H2 Database** - In-memory database (Development)

### Other
- **Lombok** - Reduce boilerplate code
- **Bean Validation** - Input validation
- **Spring Boot Actuator** - Health monitoring

## ✨ Features

### For Employers
- ✅ Register as an employer
- ✅ Post job listings with detailed information
- ✅ Manage job postings (update, delete)
- ✅ View all applications for their jobs
- ✅ Update application status (shortlist, interview, accept, reject)
- ✅ Receive email notifications for new applications

### For Job Seekers
- ✅ Register as a user/job seeker
- ✅ Browse all available jobs with pagination
- ✅ Search jobs by keywords (title, company, location)
- ✅ Filter jobs by category, job type, salary range
- ✅ Apply for jobs with cover letter
- ✅ Track application status
- ✅ Receive email notifications for application updates
- ✅ Withdraw applications

### System Features
- ✅ JWT-based authentication (stateless)
- ✅ Role-based access control (USER, EMPLOYER, ADMIN)
- ✅ Asynchronous email notifications
- ✅ Service discovery and registration
- ✅ Centralized API gateway
- ✅ Health monitoring endpoints
- ✅ Comprehensive error handling

## 🔧 Microservices

### 1. Eureka Server (Service Discovery)
- **Port:** 8761
- **Purpose:** Service registry and discovery
- **Dashboard:** http://localhost:8761

### 2. API Gateway
- **Port:** 8080
- **Purpose:** Single entry point, request routing
- **Routes:**
    - `/api/auth/**` → Auth Service
    - `/api/jobs/**` → Job Service

### 3. Auth Service
- **Port:** 8081
- **Database:** H2 (authdb)
- **Endpoints:**
    - `POST /api/auth/register` - User registration
    - `POST /api/auth/login` - User login
    - `GET /api/auth/me` - Get current user

### 4. Job Service
- **Port:** 8082
- **Database:** H2 (jobdb)
- **Endpoints:**
    - Job Management (CRUD)
    - Job Search & Filtering
    - Application Management

### 5. Notification Service
- **Port:** 8083
- **Purpose:** Process events and send notifications
- **Events:**
    - Job posted
    - Application submitted
    - Application status changed

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker (for RabbitMQ)
- Git
- Postman (optional, for testing)

### Installation

1. **Clone the repository**
```bash
   git clone https://github.com/yourusername/job-board-microservices.git
   cd job-board-microservices
```

2. **Start RabbitMQ**
```bash
   docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

3. **Build all services**
```bash
   mvn clean install -DskipTests
```

4. **Start services in order**

   **Terminal 1 - Eureka Server:**
```bash
   cd eureka-server
   mvn spring-boot:run
```

**Terminal 2 - API Gateway:**
```bash
   cd api-gateway
   mvn spring-boot:run
```

**Terminal 3 - Auth Service:**
```bash
   cd auth-service
   mvn spring-boot:run
```

**Terminal 4 - Job Service:**
```bash
   cd job-service
   mvn spring-boot:run
```

**Terminal 5 - Notification Service:**
```bash
   cd notification-service
   mvn spring-boot:run
```

5. **Verify all services are up**
    - Eureka Dashboard: http://localhost:8761
    - All 4 services should be registered

### Quick Start with Docker Compose (Coming Soon)
```bash
docker-compose up -d
```

## 📖 API Documentation

### Base URL
```
http://localhost:8080
```

### Authentication

All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

### Endpoints Overview

#### Auth Service Endpoints

**Register User**
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "role": "USER"
}
```

**Login**
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "role": "USER"
}
```

#### Job Service Endpoints

**Create Job (EMPLOYER only)**
```http
POST /api/jobs
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Senior Java Developer",
  "description": "Looking for experienced Java developer...",
  "companyName": "Tech Corp",
  "location": "Bangalore, India",
  "jobType": "FULL_TIME",
  "category": "SOFTWARE_DEVELOPMENT",
  "experienceLevel": "SENIOR",
  "salaryMin": 1500000,
  "salaryMax": 2500000,
  "skillsRequired": "Java, Spring Boot, Microservices"
}
```

**Get All Jobs (Public)**
```http
GET /api/jobs?page=0&size=10&sortBy=createdAt&sortDir=DESC
```

**Search Jobs (Public)**
```http
GET /api/jobs/search?keyword=java&page=0&size=10
```

**Apply for Job (USER only)**
```http
POST /api/jobs/1/apply
Authorization: Bearer <user-token>
Content-Type: application/json

{
  "coverLetter": "I am very interested in this position...",
  "resumeUrl": "https://example.com/resume.pdf"
}
```

**Update Application Status (EMPLOYER only)**
```http
PATCH /api/jobs/applications/1/status?status=SHORTLISTED
Authorization: Bearer <employer-token>
```

[Complete API documentation](./docs/API_DOCUMENTATION.md)

## 🧪 Testing

### Manual Testing with Postman

1. Import the Postman collection: `postman/Job-Board-APIs.postman_collection.json`
2. Set up environment variables
3. Run the collection

### Testing Workflow

1. Register as EMPLOYER
2. Create a job
3. Register as USER
4. Apply for the job
5. Switch to EMPLOYER
6. View applications
7. Update application status
8. Check notification service logs

## 🐳 Deployment

### Using Docker Compose
```yaml
# docker-compose.yml
version: '3.8'

services:
  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"

  eureka-server:
    build: ./eureka-server
    ports:
      - "8761:8761"

  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    depends_on:
      - eureka-server

  auth-service:
    build: ./auth-service
    ports:
      - "8081:8081"
    depends_on:
      - eureka-server

  job-service:
    build: ./job-service
    ports:
      - "8082:8082"
    depends_on:
      - eureka-server
      - rabbitmq

  notification-service:
    build: ./notification-service
    ports:
      - "8083:8083"
    depends_on:
      - rabbitmq
      - eureka-server
```

### Deploy to Cloud

[Deployment Guide](./docs/DEPLOYMENT_GUIDE.md)

## 📸 Screenshots

### Eureka Dashboard
![Eureka Dashboard](./screenshots/eureka-dashboard.png)

### RabbitMQ Management
![RabbitMQ](./screenshots/rabbitmq-dashboard.png)

### Postman Testing
![Postman](./screenshots/postman-testing.png)

## 🔮 Future Enhancements

- [ ] Add Redis for caching
- [ ] Implement Kafka for high-throughput messaging
- [ ] Add Elasticsearch for advanced job search
- [ ] Implement API rate limiting
- [ ] Add distributed tracing (Zipkin/Jaeger)
- [ ] Implement circuit breaker (Resilience4j)
- [ ] Add comprehensive unit and integration tests
- [ ] Implement resume upload feature (AWS S3)
- [ ] Add real-time notifications (WebSocket)
- [ ] Create admin dashboard
- [ ] Add metrics and monitoring (Prometheus/Grafana)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Your Name**
- LinkedIn: [Thanseer Jelani](https://www.linkedin.com/in/thanseer-jelani-520768255/)
- GitHub: [@thanseerjelani](https://github.com/thanseerjelani)
- Email: thanseerjelani@gmail.com

## 🙏 Acknowledgments

- Spring Boot and Spring Cloud teams
- RabbitMQ community
- All open-source contributors

---

⭐ If you found this project helpful, please give it a star!