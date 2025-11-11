# 🚀 Job Board Microservices Platform

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.1-blue?style=flat-square)](https://spring.io/projects/spring-cloud)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.13-FF6600?style=flat-square&logo=rabbitmq)](https://www.rabbitmq.com/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=flat-square&logo=docker)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)

> A production-grade microservices platform demonstrating modern backend architecture with Spring Boot, Spring Cloud, JWT authentication, RabbitMQ messaging, and user subscription system.

[🔗 Live Demo](#) | [📖 API Docs](./docs/API_DOCUMENTATION.md) | [🏗️ Architecture](./docs/ARCHITECTURE.md) | [🚀 Deployment](./docs/DEPLOYMENT.md)

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Deployment](#deployment)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

---

## 🎯 Overview

This project is a **full-featured job board backend** built with microservices architecture, demonstrating production-ready patterns and modern cloud-native development practices. The platform enables employers to post and manage jobs while allowing job seekers to search, filter, and apply for positions with real-time notifications.

### Key Highlights

- ✅ **4 Microservices** - Independently deployable and scalable
- ✅ **Service Discovery** - Eureka for dynamic service registration
- ✅ **API Gateway** - Centralized routing and load balancing
- ✅ **JWT Authentication** - Stateless, secure authentication
- ✅ **Role-Based Authorization** - USER, EMPLOYER, ADMIN roles
- ✅ **Event-Driven Architecture** - Asynchronous messaging with RabbitMQ
- ✅ **User Subscriptions** - Category-based job notifications
- ✅ **Docker Support** - One-command deployment
- ✅ **RESTful APIs** - Complete CRUD operations with pagination

---

## ✨ Features

### For Employers 💼

- Register as an employer with secure authentication
- Post job listings with detailed requirements
- Manage job postings (update, delete, view applications)
- Review applications from job seekers
- Update application status (shortlist, interview, accept, reject)
- Receive email notifications for new applications

### For Job Seekers 🔍

- Register and create user profile
- Browse all available jobs with pagination
- Search jobs by keywords (title, company, location)
- Filter by category, job type, salary range, experience level
- Subscribe to job categories of interest
- Apply for jobs with cover letter
- Track application status in real-time
- Receive targeted email notifications
- Withdraw applications if needed

### System Features 🛠️

- JWT-based stateless authentication
- Role-based access control (RBAC)
- Asynchronous email notifications
- Service discovery and health monitoring
- Centralized API gateway
- Event-driven communication
- Comprehensive error handling
- Input validation
- Database per service pattern

---

## 🏗️ Architecture

### High-Level System Design
```
┌─────────────────────────────────────────────────────────┐
│                    Client Layer                         │
│            (Web/Mobile/Postman/API Consumers)           │
└────────────────────────┬────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────┐
│               API Gateway (Port 8080)                   │
│         Single Entry Point • Load Balancing             │
│              Request Routing • Security                 │
└───────┬──────────────────────┬──────────────────────────┘
        │                      │
        ↓                      ↓
┌──────────────┐      ┌──────────────────┐
│ Auth Service │      │   Job Service    │
│   (8081)     │◄────►│     (8082)       │
│              │      │                  │
│ - Register   │      │ - Job CRUD       │
│ - Login      │      │ - Applications   │
│ - JWT Tokens │      │ - Search/Filter  │
│ - User Prefs │      │ - Subscriptions  │
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
                      │                 │
                      │ ┌─────────────┐ │
                      │ │ Job Posted  │ │
                      │ │ Application │ │
                      │ │ Status Chg  │ │
                      │ └─────────────┘ │
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
                      │ - SMTP Integration  │
                      └─────────────────────┘

All Services Register & Discover via:
┌─────────────────────────────────────┐
│       Eureka Server (8761)          │
│       Service Registry              │
└─────────────────────────────────────┘
```

### Communication Patterns

**Synchronous Communication:**
- Client → API Gateway → Services (REST/HTTP)
- Job Service → Auth Service (Feign Client)

**Asynchronous Communication:**
- Job Service → RabbitMQ → Notification Service (AMQP)
- Event-driven notifications for non-blocking operations

---

## 🛠️ Technologies

### Core Framework
- **Java 17** - Programming language
- **Spring Boot 3.2.5** - Application framework
- **Spring Cloud 2023.0.1** - Microservices ecosystem
- **Maven 3.8+** - Dependency management

### Microservices Components
- **Spring Cloud Netflix Eureka** - Service discovery and registration
- **Spring Cloud Gateway** - API gateway and intelligent routing
- **Spring Cloud OpenFeign** - Declarative REST client

### Security
- **Spring Security 6** - Authentication and authorization framework
- **JWT (JSON Web Tokens)** - Stateless authentication mechanism
- **BCrypt** - Password hashing algorithm

### Messaging & Events
- **RabbitMQ 3.13** - Message broker for async communication
- **Spring AMQP** - RabbitMQ integration and message handling

### Database & Persistence
- **Spring Data JPA** - Data access abstraction
- **Hibernate** - ORM framework
- **H2 Database** - In-memory database (Development)
- **Database-per-Service** - Each service owns its data

### DevOps & Deployment
- **Docker** - Containerization platform
- **Docker Compose** - Multi-container orchestration
- **Multi-stage Docker Builds** - Optimized container images

### Development Tools
- **Lombok** - Reduce boilerplate code
- **Bean Validation** - Request validation
- **Spring Boot Actuator** - Production-ready features
- **SLF4J + Logback** - Logging framework

---

## 🚀 Quick Start

### Prerequisites

Ensure you have the following installed:

- **Java 17+** - [Download](https://adoptium.net/)
- **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
- **Docker & Docker Compose** - [Download](https://www.docker.com/products/docker-desktop)
- **Git** - [Download](https://git-scm.com/downloads)
- **Postman** (Optional) - [Download](https://www.postman.com/downloads/)

### Option 1: Docker Compose (Recommended) 🐳

**One-command startup:**
```bash
# Clone the repository
git clone https://github.com/yourusername/job-board-microservices.git
cd job-board-microservices

# Start all services
docker-compose up -d

# Wait ~60 seconds for services to be healthy

# Check status
docker-compose ps
```

**Access Points:**
- **Eureka Dashboard:** http://localhost:8761
- **API Gateway:** http://localhost:8080
- **RabbitMQ Management:** http://localhost:15672 (guest/guest)
- **Auth Service:** http://localhost:8081
- **Job Service:** http://localhost:8082
- **Notification Service:** http://localhost:8083

**Stop services:**
```bash
docker-compose down
```

---

### Option 2: Local Development Setup

**1. Clone and Build:**
```bash
git clone https://github.com/yourusername/job-board-microservices.git
cd job-board-microservices

# Build all services
mvn clean install -DskipTests
```

**2. Start RabbitMQ:**
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

**3. Start Services (In Order):**

**Terminal 1 - Eureka Server:**
```bash
cd eureka-server
mvn spring-boot:run
```
Wait for: `Started EurekaServerApplication`

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

**4. Verify:**
- Open http://localhost:8761
- All 4 services should be registered

---

## 📖 API Documentation

### Base URL
```
http://localhost:8080
```

### Authentication Endpoints

#### Register User
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

**Roles:** `USER` (job seeker), `EMPLOYER` (company), `ADMIN` (platform admin)

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "role": "USER",
  "message": "User registered successfully"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "role": "USER"
}
```

### Job Endpoints

#### Create Job (EMPLOYER only)
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
  "skillsRequired": "Java, Spring Boot, Microservices",
  "applicationDeadline": "2025-12-31T23:59:59"
}
```

#### Get All Jobs (Public)
```http
GET /api/jobs?page=0&size=10&sortBy=createdAt&sortDir=DESC
```

#### Search Jobs (Public)
```http
GET /api/jobs/search?keyword=java&page=0&size=10
```

#### Filter by Category
```http
GET /api/jobs/category/SOFTWARE_DEVELOPMENT?page=0&size=10
```

#### Apply for Job (USER only)
```http
POST /api/jobs/1/apply
Authorization: Bearer <token>
Content-Type: application/json

{
  "coverLetter": "I am very interested in this position...",
  "resumeUrl": "https://example.com/resume.pdf"
}
```

#### Get My Applications (USER)
```http
GET /api/jobs/applications/my-applications
Authorization: Bearer <token>
```

#### Update Application Status (EMPLOYER)
```http
PATCH /api/jobs/applications/1/status?status=SHORTLISTED
Authorization: Bearer <token>
```

**Status Values:** `PENDING`, `REVIEWED`, `SHORTLISTED`, `INTERVIEWED`, `ACCEPTED`, `REJECTED`, `WITHDRAWN`

### User Preference Endpoints

#### Subscribe to Job Categories (USER)
```http
PUT /api/auth/preferences
Authorization: Bearer <token>
Content-Type: application/json

{
  "subscribedCategories": ["SOFTWARE_DEVELOPMENT", "DATA_SCIENCE"],
  "emailNotificationsEnabled": true
}
```

#### Get My Preferences
```http
GET /api/auth/preferences
Authorization: Bearer <token>
```

**📚 Complete API Documentation:** [API_DOCUMENTATION.md](./docs/API_DOCUMENTATION.md)

---

## 🧪 Testing

### Manual Testing with Postman

1. **Import Collection:**
    - Import `postman/Job-Board-APIs.postman_collection.json`

2. **Set Environment Variables:**
    - `baseUrl`: `http://localhost:8080`
    - `token`: (auto-updated after login)

3. **Test Flow:**
    - Register as EMPLOYER
    - Create a job
    - Register as USER
    - Subscribe to categories
    - Apply for the job
    - Check notification service logs

### Testing with cURL

**Register User:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User",
    "role": "USER"
  }'
```

**Get All Jobs:**
```bash
curl http://localhost:8080/api/jobs?page=0&size=10
```

---

## 🚀 Deployment

### Docker Deployment
```bash
# Build and start
docker-compose up --build -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Production Considerations

- Use PostgreSQL/MySQL instead of H2
- Configure real SMTP server for emails
- Use environment variables for sensitive data
- Enable HTTPS/TLS
- Implement API rate limiting
- Add monitoring (Prometheus, Grafana)
- Configure distributed tracing (Zipkin, Jaeger)

**📚 Detailed Deployment Guide:** [DEPLOYMENT.md](./docs/DEPLOYMENT.md)

---

## 📁 Project Structure
```
job-board-microservices/
├── eureka-server/              # Service Discovery
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── api-gateway/                # API Gateway
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── auth-service/               # Authentication & Authorization
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── job-service/                # Job & Application Management
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── notification-service/       # Email Notifications
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── docs/                       # Documentation
│   ├── API_DOCUMENTATION.md
│   ├── ARCHITECTURE.md
│   └── DEPLOYMENT.md
├── postman/                    # API Collection
│   └── Job-Board-APIs.postman_collection.json
├── docker-compose.yml          # Docker orchestration
├── pom.xml                     # Parent POM
├── README.md                   # This file
└── LICENSE                     # MIT License
```

---

## 🔮 Future Enhancements

- [ ] Implement Redis for caching
- [ ] Add Kafka for high-throughput messaging
- [ ] Integrate Elasticsearch for advanced search
- [ ] Add API rate limiting with Redis
- [ ] Implement distributed tracing (Zipkin/Jaeger)
- [ ] Add circuit breaker pattern (Resilience4j)
- [ ] Create admin dashboard
- [ ] Add resume upload feature (AWS S3)
- [ ] Implement real-time notifications (WebSocket)
- [ ] Add comprehensive test coverage
- [ ] Setup CI/CD pipeline (GitHub Actions)
- [ ] Implement monitoring (Prometheus/Grafana)

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Thanseer Jelani**

- 💼 LinkedIn: [Thanseer Jelani](https://www.linkedin.com/in/thanseer-jelani-520768255/)
- 🐱 GitHub: [@thanseerjelani](https://github.com/thanseerjelani)
- 📧 Email: thanseerjelani@gmail.com
- 🌐 Portfolio: [thanseerjelani.com](https://thanseerjelani-portfolio.netlify.app/)

---

## 🙏 Acknowledgments

- Spring Boot and Spring Cloud teams for excellent documentation
- RabbitMQ community for robust messaging platform
- Docker for making deployment seamless
- All open-source contributors

---

## 📊 Project Statistics

- **Lines of Code:** ~5,000+
- **Microservices:** 5 (Eureka, Gateway, Auth, Job, Notification)
- **REST Endpoints:** 15+
- **Docker Containers:** 6
- **Message Queues:** 3
- **Technologies Used:** 20+

---

<div align="center">

### ⭐ If you found this project helpful, please give it a star!

**Built with ❤️ using Spring Boot & Microservices**

</div>