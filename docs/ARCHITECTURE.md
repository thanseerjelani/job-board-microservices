# 🏗️ Architecture Guide - Job Board Microservices

Comprehensive documentation of the system architecture, design patterns, and technical decisions.

---

## 📋 Table of Contents

- [System Overview](#system-overview)
- [Architectural Style](#architectural-style)
- [Service Architecture](#service-architecture)
- [Communication Patterns](#communication-patterns)
- [Data Architecture](#data-architecture)
- [Security Architecture](#security-architecture)
- [Design Patterns](#design-patterns)
- [Technology Stack](#technology-stack)
- [Scalability Considerations](#scalability-considerations)
- [Trade-offs and Decisions](#trade-offs-and-decisions)

---

## 🎯 System Overview

The Job Board platform is built using **microservices architecture** following modern cloud-native principles. The system enables employers to post jobs and job seekers to discover and apply for positions with real-time notifications.

### Key Architectural Characteristics

- **Distributed System** - Multiple independent services working together
- **Event-Driven** - Asynchronous communication via message queues
- **Resilient** - Service discovery and health monitoring
- **Scalable** - Horizontally scalable individual services
- **Secure** - JWT-based stateless authentication
- **Observable** - Health checks and actuator endpoints

---

## 🏛️ Architectural Style

### Microservices Architecture

The system follows microservices architecture with the following principles:

#### 1. **Service Independence**
- Each service is independently deployable
- Services own their data (database per service pattern)
- Services can be scaled independently
- Technology stack can vary per service

#### 2. **Domain-Driven Design (DDD)**
- Services are organized around business capabilities
- Each service represents a bounded context
- Clear service boundaries and responsibilities

#### 3. **API-First Design**
- Well-defined REST APIs
- Centralized API gateway for routing
- Consistent API patterns across services

---

## 🔧 Service Architecture

### High-Level System Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     Client Applications                      │
│          (Web Browser, Mobile App, API Consumers)            │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            │ HTTPS
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   API Gateway (Port 8080)                    │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ • Request Routing      • Load Balancing                │ │
│  │ • Authentication       • Rate Limiting                 │ │
│  │ • CORS Handling        • Request/Response Logging      │ │
│  └────────────────────────────────────────────────────────┘ │
└───────────┬──────────────────────────────┬──────────────────┘
            │                              │
            │                              │
    ┌───────┴────────┐            ┌────────┴───────────┐
    │                │            │                    │
    ↓                ↓            ↓                    ↓
┌──────────────┐  ┌──────────────────┐         ┌─────────────┐
│Auth Service  │  │  Job Service     │         │   Future    │
│  (Port 8081) │  │  (Port 8082)     │         │  Services   │
├──────────────┤  ├──────────────────┤         └─────────────┘
│ • Register   │  │ • Job CRUD       │
│ • Login      │  │ • Applications   │
│ • JWT Issue  │  │ • Search/Filter  │
│ • User Mgmt  │  │ • Subscriptions  │
│ • Preferences│◄─┤ • Verification   │ (Feign Client)
│              │  │                  │
│ Database:    │  │ Database:        │
│ H2 (authdb)  │  │ H2 (jobdb)       │
└──────────────┘  └────────┬─────────┘
                           │
                           │ Publish Events
                           ↓
                  ┌──────────────────────┐
                  │      RabbitMQ        │
                  │    (Port 5672)       │
                  ├──────────────────────┤
                  │ Exchange: job.events │
                  │                      │
                  │ Queues:              │
                  │ • job.posted         │
                  │ • application.new    │
                  │ • status.changed     │
                  └──────────┬───────────┘
                             │
                             │ Consume Events
                             ↓
                  ┌──────────────────────┐
                  │ Notification Service │
                  │    (Port 8083)       │
                  ├──────────────────────┤
                  │ • Email Sending      │
                  │ • Event Processing   │
                  │ • Template Rendering │
                  │ • SMTP Integration   │
                  └──────────────────────┘

            All Services Register & Discover:
        ┌────────────────────────────────────┐
        │    Eureka Server (Port 8761)       │
        │      Service Registry              │
        │                                    │
        │ Registered Services:               │
        │ • API-GATEWAY                      │
        │ • AUTH-SERVICE                     │
        │ • JOB-SERVICE                      │
        │ • NOTIFICATION-SERVICE             │
        └────────────────────────────────────┘
```

---

## 🔌 Service Details

### 1. Eureka Server (Service Registry)

**Purpose:** Service discovery and registration

**Port:** 8761

**Responsibilities:**
- Maintain registry of all microservices
- Provide service locations to clients
- Health monitoring of registered services
- Enable dynamic service discovery

**Technology:**
- Spring Cloud Netflix Eureka Server

**Key Configuration:**
```yaml
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

---

### 2. API Gateway

**Purpose:** Single entry point for all client requests

**Port:** 8080

**Responsibilities:**
- Route requests to appropriate services
- Load balancing across service instances
- Request/response transformation
- CORS handling
- Centralized logging
- Future: Rate limiting, authentication

**Technology:**
- Spring Cloud Gateway
- WebFlux (Reactive)

**Routing Configuration:**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://AUTH-SERVICE
          predicates:
            - Path=/api/auth/**
        
        - id: job-service
          uri: lb://JOB-SERVICE
          predicates:
            - Path=/api/jobs/**
```

**Key Features:**
- Load Balancer: `lb://` prefix enables client-side load balancing
- Path-based routing
- Service discovery integration

---

### 3. Auth Service

**Purpose:** Authentication, authorization, and user management

**Port:** 8081

**Database:** H2 (authdb)

**Responsibilities:**
- User registration (USER, EMPLOYER, ADMIN roles)
- User authentication
- JWT token generation and validation
- Password encryption (BCrypt)
- User profile management
- User preference management (subscriptions)
- Provide user verification endpoint for other services

**REST Endpoints:**
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/auth/me` - Get current user
- `PUT /api/auth/preferences` - Update preferences
- `GET /api/auth/preferences` - Get preferences
- `GET /api/auth/verify/{userId}` - Internal verification (Feign)

**Security Implementation:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // JWT filter chain
    // Public endpoints: /api/auth/register, /api/auth/login
    // Protected endpoints: All others
}
```

**Database Schema:**
```
users
├── id (BIGINT, PK)
├── username (VARCHAR, UNIQUE)
├── email (VARCHAR, UNIQUE)
├── password (VARCHAR, hashed)
├── full_name (VARCHAR)
├── role (ENUM: USER, EMPLOYER, ADMIN)
├── created_at (TIMESTAMP)
└── updated_at (TIMESTAMP)

user_preferences
├── id (BIGINT, PK)
├── user_id (BIGINT, FK → users.id)
├── email_notifications_enabled (BOOLEAN)
└── subscribed_categories (VARCHAR, comma-separated)
```

---

### 4. Job Service

**Purpose:** Core business logic for jobs and applications

**Port:** 8082

**Database:** H2 (jobdb)

**Responsibilities:**
- Job posting management (CRUD)
- Job search and filtering
- Application management
- Application status tracking
- User verification via Auth Service (Feign Client)
- Publish events to RabbitMQ
- Enforce business rules

**REST Endpoints:**
- `POST /api/jobs` - Create job (EMPLOYER)
- `GET /api/jobs` - Get all jobs (public, paginated)
- `GET /api/jobs/{id}` - Get job by ID
- `PUT /api/jobs/{id}` - Update job (EMPLOYER, own jobs only)
- `DELETE /api/jobs/{id}` - Delete job (EMPLOYER, own jobs only)
- `GET /api/jobs/my-jobs` - Get employer's jobs
- `GET /api/jobs/search` - Search jobs
- `GET /api/jobs/category/{category}` - Filter by category
- `GET /api/jobs/type/{type}` - Filter by job type
- `GET /api/jobs/salary-range` - Filter by salary
- `POST /api/jobs/{jobId}/apply` - Apply for job (USER)
- `GET /api/jobs/{jobId}/applications` - Get job applications (EMPLOYER)
- `GET /api/jobs/applications/my-applications` - Get user's applications
- `PATCH /api/jobs/applications/{id}/status` - Update status (EMPLOYER)
- `DELETE /api/jobs/{jobId}/applications/withdraw` - Withdraw application (USER)

**Database Schema:**
```
jobs
├── id (BIGINT, PK)
├── title (VARCHAR)
├── description (TEXT)
├── company_name (VARCHAR)
├── location (VARCHAR)
├── job_type (ENUM: FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP)
├── category (ENUM: SOFTWARE_DEVELOPMENT, DATA_SCIENCE, etc.)
├── experience_level (ENUM: ENTRY, JUNIOR, MID, SENIOR, LEAD)
├── salary_min (BIGINT)
├── salary_max (BIGINT)
├── skills_required (VARCHAR)
├── application_deadline (TIMESTAMP)
├── posted_by (BIGINT, employer user ID)
├── created_at (TIMESTAMP)
└── updated_at (TIMESTAMP)

applications
├── id (BIGINT, PK)
├── job_id (BIGINT, FK → jobs.id)
├── user_id (BIGINT, applicant user ID)
├── cover_letter (TEXT)
├── resume_url (VARCHAR)
├── status (ENUM: PENDING, REVIEWED, SHORTLISTED, INTERVIEWED, 
│            ACCEPTED, REJECTED, WITHDRAWN)
├── applied_at (TIMESTAMP)
└── updated_at (TIMESTAMP)
```

**Feign Client Integration:**
```java
@FeignClient(name = "AUTH-SERVICE")
public interface AuthServiceClient {
    @GetMapping("/api/auth/verify/{userId}")
    UserVerificationResponse verifyUser(@PathVariable Long userId);
}
```

**Event Publishing:**
```java
// When job is posted
rabbitTemplate.convertAndSend("job.exchange", "job.posted", jobEvent);

// When application is submitted
rabbitTemplate.convertAndSend("job.exchange", "application.new", applicationEvent);

// When status changes
rabbitTemplate.convertAndSend("job.exchange", "status.changed", statusEvent);
```

---

### 5. Notification Service

**Purpose:** Asynchronous email notifications

**Port:** 8083

**Database:** None (stateless)

**Responsibilities:**
- Consume events from RabbitMQ
- Send email notifications via SMTP
- Render email templates
- Handle notification failures gracefully
- Log notification activities

**Message Consumers:**
```java
@RabbitListener(queues = "job.posted.queue")
public void handleJobPosted(JobPostedEvent event) {
    // Notify subscribed users
}

@RabbitListener(queues = "application.new.queue")
public void handleNewApplication(ApplicationEvent event) {
    // Notify employer
}

@RabbitListener(queues = "application.status.queue")
public void handleStatusChange(StatusChangeEvent event) {
    // Notify applicant
}
```

**Email Types:**
1. **Job Posted Notification** - Sent to users subscribed to job category
2. **New Application** - Sent to employer when someone applies
3. **Status Update** - Sent to applicant when status changes

**SMTP Configuration:**
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${SMTP_USERNAME}
    password: ${SMTP_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

---

## 📡 Communication Patterns

### 1. Synchronous Communication (REST)

**Used For:** Request-response operations requiring immediate feedback

**Pattern:** Client → API Gateway → Service

**Examples:**
- User login
- Job creation
- Fetching job details
- Submitting application

**Technology:** REST over HTTP, Spring Cloud OpenFeign

**Advantages:**
- Simple to implement
- Immediate response
- Easy to debug

**Disadvantages:**
- Tight coupling
- Blocking operations
- Network latency impact

---

### 2. Asynchronous Communication (Messaging)

**Used For:** Non-critical operations that don't need immediate response

**Pattern:** Service → RabbitMQ → Service

**Examples:**
- Email notifications
- Event logging
- Background processing

**Technology:** RabbitMQ with Spring AMQP

**Message Flow:**
```
Job Service                 RabbitMQ                 Notification Service
    │                          │                            │
    │  1. Publish Event        │                            │
    ├─────────────────────────>│                            │
    │  (job.posted)            │                            │
    │                          │                            │
    │  2. Return Immediately   │                            │
    │<─────────────────────────┤                            │
    │                          │                            │
    │                          │  3. Route to Queue         │
    │                          │  (job.posted.queue)        │
    │                          │                            │
    │                          │  4. Consume Event          │
    │                          ├───────────────────────────>│
    │                          │                            │
    │                          │                            │  5. Send Email
    │                          │                            ├──────────> SMTP
    │                          │                            │
    │                          │  6. Acknowledge            │
    │                          │<───────────────────────────┤
```

**Exchange Configuration:**
```java
@Bean
public TopicExchange jobExchange() {
    return new TopicExchange("job.exchange");
}

@Bean
public Queue jobPostedQueue() {
    return new Queue("job.posted.queue");
}

@Bean
public Binding jobPostedBinding(Queue jobPostedQueue, TopicExchange jobExchange) {
    return BindingBuilder
        .bind(jobPostedQueue)
        .to(jobExchange)
        .with("job.posted");
}
```

**Advantages:**
- Loose coupling
- Non-blocking
- Fault tolerance (message persistence)
- Scalability

**Disadvantages:**
- Eventual consistency
- Complex debugging
- Message ordering challenges

---

### 3. Service Discovery Pattern

**Purpose:** Dynamic service location resolution

**Flow:**
```
1. Service Startup
   Service → Register with Eureka → Eureka Server

2. Service Lookup
   Client → Query Eureka → Get Service Location → Call Service

3. Health Monitoring
   Eureka → Periodic Health Check → Service
```

**Configuration:**
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

---

## 💾 Data Architecture

### Database Per Service Pattern

Each microservice owns its database, ensuring loose coupling and independence.

**Services and Databases:**
- **Auth Service** → `authdb` (User data, preferences)
- **Job Service** → `jobdb` (Jobs, applications)
- **Notification Service** → No database (stateless)

**Benefits:**
- Service independence
- Technology flexibility
- Easier scaling
- Clear boundaries

**Challenges:**
- Data consistency (eventual consistency model)
- Distributed transactions (avoided in design)
- Data duplication (minimal, user ID only)

---

### Data Consistency Strategy

**Approach:** Eventual Consistency

**Implementation:**
1. **No Distributed Transactions** - Each service transaction is independent
2. **Event-Driven Updates** - Services communicate state changes via events
3. **Idempotent Operations** - Event consumers handle duplicate messages
4. **Compensating Transactions** - Rollback via reverse operations if needed

**Example Scenario:**
```
User applies for job:
1. Job Service validates and creates application
2. Job Service commits to database
3. Job Service publishes "application.new" event
4. Notification Service processes event asynchronously
5. Email sent (eventually)

If email fails:
- Application still exists (primary operation succeeded)
- Notification can be retried
- User can check application status
```

---

## 🔐 Security Architecture

### Authentication Flow

```
┌──────────┐                                  ┌──────────────┐
│  Client  │                                  │ Auth Service │
└────┬─────┘                                  └──────┬───────┘
     │                                               │
     │  1. POST /api/auth/login                     │
     │  { username, password }                      │
     ├──────────────────────────────────────────────>│
     │                                               │
     │                                               │  2. Validate
     │                                               │     credentials
     │                                               │
     │  3. Return JWT Token                         │
     │<──────────────────────────────────────────────┤
     │  { token: "eyJhbG...", role: "USER" }        │
     │                                               │
     │                                               │
     │  4. Subsequent requests with token           │
     │  GET /api/jobs/1                             │
     │  Header: Authorization: Bearer eyJhbG...     │
     ├──────────────────────────────────────────────>│
     │                                               │
     │                                               │  5. Validate JWT
     │                                               │
     │  6. Return protected resource                │
     │<──────────────────────────────────────────────┤
     │                                               │
```

### JWT Token Structure

```
Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload:
{
  "sub": "username",
  "userId": 1,
  "role": "USER",
  "iat": 1234567890,
  "exp": 1234571490
}

Signature:
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret_key
)
```

### Authorization Rules

**Role-Based Access Control (RBAC):**

| Endpoint | USER | EMPLOYER | ADMIN |
|----------|------|----------|-------|
| POST /api/jobs | ❌ | ✅ | ✅ |
| GET /api/jobs | ✅ | ✅ | ✅ |
| POST /api/jobs/{id}/apply | ✅ | ❌ | ✅ |
| GET /api/jobs/my-jobs | ❌ | ✅ | ✅ |
| PATCH /api/jobs/applications/{id}/status | ❌ | ✅ | ✅ |
| GET /api/jobs/applications/my-applications | ✅ | ❌ | ✅ |

**Implementation:**
```java
@PreAuthorize("hasRole('EMPLOYER')")
public JobResponse createJob(JobRequest request) {
    // Only employers can create jobs
}

@PreAuthorize("hasRole('USER')")
public ApplicationResponse applyForJob(Long jobId, ApplicationRequest request) {
    // Only users can apply
}
```

---

## 🎨 Design Patterns

### 1. API Gateway Pattern

**Problem:** Multiple clients need to interact with multiple services

**Solution:** Single entry point that routes requests to appropriate services

**Benefits:**
- Simplified client code
- Centralized cross-cutting concerns
- Protocol translation
- Request aggregation (future)

---

### 2. Service Registry Pattern

**Problem:** Services need to discover each other dynamically

**Solution:** Eureka server maintains registry of all services

**Benefits:**
- Dynamic service discovery
- Load balancing
- Health monitoring
- Fault tolerance

---

### 3. Database Per Service Pattern

**Problem:** Shared database creates tight coupling

**Solution:** Each service owns its database

**Benefits:**
- Service independence
- Technology diversity
- Easier scaling
- Clear boundaries

---

### 4. Event-Driven Pattern

**Problem:** Services need to communicate without tight coupling

**Solution:** Publish-subscribe messaging via RabbitMQ

**Benefits:**
- Loose coupling
- Asynchronous processing
- Scalability
- Resilience

---

### 5. Circuit Breaker Pattern (Future)

**Problem:** Cascading failures in distributed systems

**Solution:** Resilience4j for fault tolerance

**Benefits:**
- Prevent cascading failures
- Fallback mechanisms
- System resilience

---

## 🛠️ Technology Stack

### Backend Framework
- **Spring Boot 3.2.5** - Core application framework
- **Spring Cloud 2023.0.1** - Microservices infrastructure

### Microservices Components
- **Spring Cloud Netflix Eureka** - Service discovery
- **Spring Cloud Gateway** - API gateway (WebFlux)
- **Spring Cloud OpenFeign** - Declarative REST client

### Security
- **Spring Security 6** - Authentication & authorization
- **JWT (jjwt 0.12.3)** - Token-based auth
- **BCrypt** - Password hashing

### Messaging
- **RabbitMQ 3.13** - Message broker
- **Spring AMQP** - Messaging integration

### Data & Persistence
- **Spring Data JPA** - Data access layer
- **Hibernate** - ORM
- **H2 Database** - Development database
- **Database-per-Service** - Architecture pattern

### Development Tools
- **Lombok** - Reduce boilerplate
- **Bean Validation** - Input validation
- **Spring Boot Actuator** - Metrics & health checks

### Build & Deployment
- **Maven 3.8+** - Build tool
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration

---

## 📈 Scalability Considerations

### Horizontal Scaling

**Services that can scale:**
- API Gateway (multiple instances)
- Auth Service (stateless)
- Job Service (stateless)
- Notification Service (stateless)

**How to scale:**
```bash
# Docker Compose
docker-compose up --scale job-service=3

# Kubernetes
kubectl scale deployment job-service --replicas=3
```

**Load Balancing:**
- Eureka provides client-side load balancing
- API Gateway distributes requests across instances

---

### Database Scaling

**Current:** H2 in-memory (not production-ready)

**Production Strategies:**

1. **Read Replicas** - Separate read and write operations
2. **Sharding** - Partition data across databases
3. **Caching** - Redis for frequently accessed data
4. **Connection Pooling** - HikariCP (included in Spring Boot)

---

### Message Queue Scaling

**RabbitMQ Scaling:**
- Multiple consumers per queue
- Queue clustering
- Message persistence
- Acknowledgment modes

---

## ⚖️ Trade-offs and Decisions

### 1. H2 vs PostgreSQL

**Decision:** H2 for development

**Reasons:**
- Quick setup
- No external dependencies
- Embedded database
- Easy testing

**Production:** Use PostgreSQL/MySQL

---

### 2. Synchronous vs Asynchronous

**Decision:** Hybrid approach

**Synchronous (REST):**
- User operations requiring immediate feedback
- Data queries
- Critical business operations

**Asynchronous (Messaging):**
- Email notifications
- Event logging
- Background processing

---

### 3. Monolith vs Microservices

**Decision:** Microservices

**Reasons:**
- Demonstrate modern architecture
- Independent scaling
- Technology flexibility
- Clear service boundaries

**Trade-offs:**
- Increased complexity
- Distributed system challenges
- Network latency
- Eventual consistency

---

### 4. JWT vs Session-Based Auth

**Decision:** JWT (Stateless)

**Reasons:**
- No session storage needed
- Scalable across instances
- Microservices-friendly
- Reduced database load

**Trade-offs:**
- Cannot revoke tokens easily
- Token size larger than session ID
- Need to manage token expiration

---

## 📊 Quality Attributes

### Performance
- API Gateway routing: < 10ms overhead
- Database queries: Indexed on common filters
- Asynchronous notifications: Non-blocking

### Reliability
- Health checks on all services
- Message persistence in RabbitMQ
- Graceful degradation

### Maintainability
- Clear service boundaries
- Consistent code structure
- Comprehensive documentation
- Standardized error handling

### Security
- JWT authentication
- Role-based authorization
- Password encryption
- Input validation

---

## 🔗 Related Documentation

- [Main README](../README.md)
- [API Documentation](./API_DOCUMENTATION.md)
- [Deployment Guide](./DEPLOYMENT.md)

---

<div align="center">

**Enterprise-Grade Microservices Architecture** 🏗️

*Built with Spring Boot, Spring Cloud, and Modern Design Patterns*