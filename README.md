# 🛒 AI-Powered Shopping Platform

### E-Commerce Platform — Microservices Architecture

> A full-scale, cloud-native AI-Powered Shopping Platform built using Spring Boot Microservices. Demonstrates distributed systems design, event-driven architecture, AI-powered recommendations, caching, service discovery, centralized configuration, and modern DevOps practices.

<br/>

<img src="https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring_Boot-2.7.18-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring_Cloud-2021.0.8-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
<img src="https://img.shields.io/badge/Apache_Kafka-3.x-231F20?style=for-the-badge&logo=apachekafka&logoColor=white"/>
<img src="https://img.shields.io/badge/MySQL-8-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
<img src="https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white"/>
<img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white"/>
<img src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge"/>

<br/><br/>

</div>

---

## 📌 Repository Description

> **AI-Powered Shopping Platform** is a microservices-based e-commerce platform built using Java 17, Spring Boot, and Spring Cloud. The system features independent services communicating via REST APIs (synchronous) and Apache Kafka (asynchronous), with full JWT authentication, Redis caching, centralized configuration, service discovery, AI-powered product recommendations, and Docker-based deployment.

---

# 🏗 Architecture Overview

The system consists of the following microservices:

## Infrastructure Services

| Service | Port | Responsibility |
|----------|--------|----------------|
| Eureka Server | 8761 | Service Discovery |
| Config Server | 8888 | Centralized Configuration |

## Core Services

| Service | Port | Responsibility |
|----------|--------|----------------|
| API Gateway | 8080 | API Gateway, JWT Validation & Routing |
| Auth Service | 8081 | Authentication & Authorization |
| User Service | 8082 | User Profile Management |
| Product Service | 8083 | Product Management + Redis Cache |
| Cart Service | 8084 | Shopping Cart Management |
| Order Service | 8085 | Order Processing + Kafka Producer |
| Recommendation Service | 8086 | AI-Powered Recommendations |
| Notification Service | 8087 | Notification Management + Kafka Consumer |

---

## 🛠 Tech Stack

<table>
<tr><td><strong>Core</strong></td><td>Java 17, Spring Boot 2.7.18, Maven</td></tr>
<tr><td><strong>Cloud</strong></td><td>Spring Cloud 2021.0.8, Eureka, Config Server, Gateway</td></tr>
<tr><td><strong>Security</strong></td><td>Spring Security, JWT Authentication, RBAC</td></tr>
<tr><td><strong>Messaging</strong></td><td>Apache Kafka</td></tr>
<tr><td><strong>Persistence</strong></td><td>Spring Data JPA, Hibernate, MySQL</td></tr>
<tr><td><strong>Caching</strong></td><td>Redis</td></tr>
<tr><td><strong>AI</strong></td><td>Spring AI</td></tr>
<tr><td><strong>Documentation</strong></td><td>Swagger / OpenAPI</td></tr>
<tr><td><strong>Testing</strong></td><td>JUnit, Mockito</td></tr>
<tr><td><strong>DevOps</strong></td><td>Docker, Docker Compose, GitHub Actions</td></tr>
</table>

---

## 🗃 Database Design

Each service owns its own database (Database per Service Pattern):

```text
auth_db
└── users
└── roles

user_db
└── user_profiles

product_db
└── products
└── categories

cart_db
└── carts
└── cart_items

order_db
└── orders
└── order_items

notification_db
└── notifications
```

---

## 🌐 Service URLs

| Portal | URL |
|---------|------|
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| Auth Service | http://localhost:8081 |
| User Service | http://localhost:8082 |
| Product Service | http://localhost:8083 |
| Cart Service | http://localhost:8084 |
| Order Service | http://localhost:8085 |
| Recommendation Service | http://localhost:8086 |
| Notification Service | http://localhost:8087 |

---

# 🔐 Authentication & Authorization

## Roles

| Role | Permissions |
|--------|-------------|
| USER | Browse Products, Manage Cart, Place Orders |
| ADMIN | Full Platform Access |

## Auth Flow

```bash
# Register User
POST /api/auth/register

# Login
POST /api/auth/login

# Admin Login
POST /api/auth/admin/login

# Logout
POST /api/auth/logout

# Reset Password
POST /api/auth/reset-password
```

JWT tokens are issued by Auth Service and validated by API Gateway.

---

# 🔄 Service Communication

## Synchronous Communication

- API Gateway routes requests to appropriate services
- Services communicate via REST APIs using RestTemplate
- All services register themselves with Eureka

## Asynchronous Communication (Kafka)

### Order Service publishes events

```text
order-created
order-cancelled
order-delivered
```

### Notification Service consumes events

```text
order-created
order-cancelled
order-delivered
```

Notifications are generated automatically based on order events.

---

# 📖 API Documentation

## Auth Service

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/admin/login
POST /api/auth/logout
POST /api/auth/reset-password
```

---

## User Service

```text
GET    /api/users
GET    /api/users/{id}
GET    /api/users/email/{email}
POST   /api/users
PUT    /api/users/{id}
DELETE /api/users/{id}
```

---

## Product Service

```text
GET    /api/products
GET    /api/products/{id}
GET    /api/products/category/{category}
GET    /api/products/search
GET    /api/products/price-range
POST   /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}
```

---

## Cart Service

```text
GET    /api/carts/user/{userId}
POST   /api/carts/user/{userId}/add
PUT    /api/carts/user/{userId}/update
DELETE /api/carts/user/{userId}/remove
DELETE /api/carts/user/{userId}/clear
```

---

## Order Service

```text
POST   /api/orders
GET    /api/orders/user/{userEmail}
GET    /api/orders/{id}
PUT    /api/orders/{id}/status
DELETE /api/orders/{id}
```

---

## Recommendation Service

```text
GET /api/recommendations?userEmail={email}
GET /api/recommendations?userEmail={email}&category={category}
```

---

## Notification Service

```text
GET    /api/notifications
GET    /api/notifications/{id}
GET    /api/notifications/user/{userId}
PUT    /api/notifications/{id}/read
DELETE /api/notifications/{id}
```

---

# 🚀 Running the Project

## Prerequisites

```text
Java 17
Maven 3.x
Docker
Docker Compose
MySQL
Redis
Apache Kafka
```

---

## Build All Services

```bash
mvn clean install
```

---

## Run Individual Service

```bash
cd eureka-server
mvn spring-boot:run
```

---

## Run Entire Platform

```bash
cd microservices
docker-compose up -d
```

---

# 🧪 Testing

```bash
# Run all tests
mvn test

# Generate build artifacts
mvn clean package
```

---

# 🏛 Architecture Patterns

| Pattern | Implementation |
|----------|---------------|
| Database per Service | Separate database for each microservice |
| API Gateway | Centralized routing and JWT validation |
| Service Discovery | Eureka Server |
| Centralized Configuration | Config Server |
| Event-Driven Architecture | Kafka |
| Caching | Redis |
| Repository Pattern | Spring Data JPA |
| DTO Pattern | Request/Response DTOs |
| RBAC | USER / ADMIN roles |

---

# 📊 Infrastructure

```text
Eureka Server        : 8761
Config Server        : 8888
API Gateway          : 8080

Auth Service         : 8081
User Service         : 8082
Product Service      : 8083
Cart Service         : 8084
Order Service        : 8085
Recommendation Svc   : 8086
Notification Service : 8087

Kafka                : 9092
Redis                : 6379

MySQL Databases
auth_db
user_db
product_db
cart_db
order_db
notification_db
```

---

# 🚢 Deployment

## Docker Deployment

```bash
mvn clean package

docker-compose up -d
```

---

## CI/CD

```text
GitHub Actions
```

Workflow file:

```text
.github/workflows/ci-cd.yml
```

---

# 🔄 Migration Strategy

### Phase 1
Deploy Infrastructure Services

```text
Eureka Server
Config Server
```

### Phase 2
Deploy Core Services

```text
Auth Service
User Service
Product Service
```

### Phase 3
Deploy Business Services

```text
Cart Service
Order Service
```

### Phase 4
Deploy Supporting Services

```text
Recommendation Service
Notification Service
```

### Phase 5
Switch Frontend Traffic to API Gateway

### Phase 6
Decommission Monolithic Application

---

# 📝 Notes

- All existing APIs, business logic, and functionality are preserved
- Role-based access control (USER, ADMIN) is maintained
- Database-per-service pattern is implemented
- Kafka communication is strictly asynchronous between Order and Notification services
- Redis caching is implemented in Product Service
- Spring AI is used for product recommendations
- API Gateway performs JWT validation and request routing

---

# 🤝 Contributing

```bash
# Fork repository

# Create branch
git checkout -b feature/your-feature

# Commit changes
git commit -m "feat: add your feature"

# Push changes
git push origin feature/your-feature

# Create Pull Request
```

---

## 📄 License

```text
MIT License
```
