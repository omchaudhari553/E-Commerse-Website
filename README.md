<div align="center">

# 🛒 AI-Powered Shopping Platform

### E-Commerce Platform — Microservices Architecture

> A full-scale, cloud-native AI-Powered Shopping Platform built using **Spring Boot Microservices**. Demonstrates distributed systems design, event-driven architecture, AI-powered recommendations, service discovery, centralized configuration, caching, and modern DevOps practices.

<br/>

<img src="https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring_Boot-2.7.18-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring_Cloud-2021.0.8-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
<img src="https://img.shields.io/badge/Apache_Kafka-3.x-231F20?style=for-the-badge&logo=apachekafka&logoColor=white"/>
<img src="https://img.shields.io/badge/MySQL-8-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
<img src="https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white"/>
<img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white"/>
<img src="https://img.shields.io/badge/GitHub_Actions-CI/CD-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"/>
<img src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge"/>

<br/><br/>

</div>

---

## 📌 Repository Description

> **AI-Powered Shopping Platform** is a microservices-based e-commerce platform built using **Java 17**, **Spring Boot 2.7.18**, and **Spring Cloud 2021.0.8**. The system features independent services communicating via **REST APIs** (synchronous) and **Apache Kafka** (asynchronous), with full **JWT authentication**, **Redis caching**, **centralized configuration**, **service discovery**, **AI-powered product recommendations**, and **Docker-based deployment**.

---

## 📦 Microservices

| # | Service | Port | Responsibility | Key Tech |
|---|---------|------|----------------|----------|
| 1 | **API Gateway** | 8080 | JWT Validation, Routing, Entry Point | Spring Cloud Gateway |
| 2 | **Config Server** | 8888 | Centralized Configuration | Spring Cloud Config |
| 3 | **Eureka Server** | 8761 | Service Discovery | Netflix Eureka |
| 4 | **Auth Service** | 8081 | Authentication & Authorization | Spring Security, JWT |
| 5 | **User Service** | 8082 | User Profile Management | JPA, MySQL |
| 6 | **Product Service** | 8083 | Product Management & Caching | JPA, Redis |
| 7 | **Cart Service** | 8084 | Shopping Cart Management | JPA |
| 8 | **Order Service** | 8085 | Order Processing | Kafka Producer |
| 9 | **Recommendation Service** | 8086 | AI Product Recommendations | Spring AI |
| 10 | **Notification Service** | 8087 | User Notifications | Kafka Consumer |

---

## 🛠 Tech Stack

<table>
<tr><td><strong>Core</strong></td><td>Java 17, Spring Boot 2.7.18, Maven</td></tr>
<tr><td><strong>Cloud</strong></td><td>Spring Cloud 2021.0.8, Eureka, Config Server, Gateway</td></tr>
<tr><td><strong>Security</strong></td><td>Spring Security, JWT Authentication, RBAC (USER / ADMIN)</td></tr>
<tr><td><strong>Messaging</strong></td><td>Apache Kafka (Async Communication)</td></tr>
<tr><td><strong>Persistence</strong></td><td>Spring Data JPA, Hibernate, MySQL</td></tr>
<tr><td><strong>Caching</strong></td><td>Redis</td></tr>
<tr><td><strong>AI</strong></td><td>Spring AI</td></tr>
<tr><td><strong>Documentation</strong></td><td>Swagger / OpenAPI</td></tr>
<tr><td><strong>Testing</strong></td><td>JUnit 5, Mockito</td></tr>
<tr><td><strong>DevOps</strong></td><td>Docker, Docker Compose, GitHub Actions</td></tr>
</table>

---

## 🏗 Architecture Overview

```text
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway (8080)                      │
│                  JWT Validation & Routing                      │
└─────────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
┌───────▼────────┐  ┌────────▼────────┐  ┌──────▼────────┐
│ Auth Service   │  │ User Service    │  │ Product Svc   │
│    8081        │  │    8082         │  │    8083       │
└───────────────┘  └─────────────────┘  └───────────────┘

        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
┌───────▼────────┐  ┌────────▼────────┐  ┌──────▼────────┐
│ Cart Service   │  │ Order Service   │  │ Recom. Svc    │
│    8084        │  │    8085         │  │    8086       │
└───────────────┘  └─────────────────┘  └───────────────┘
                              │
                   ┌──────────▼──────────┐
                   │ Notification Service│
                   │       8087          │
                   └─────────────────────┘
```

---

## 🗃 Database Design

Each service owns its own MySQL database (Database-per-Service Pattern):

```text
auth_db
user_db
product_db
cart_db
order_db
notification_db
```

---

## 🌐 Service URLs

| Portal | URL |
|--------|-----|
| 🔀 API Gateway | http://localhost:8080 |
| 🗂 Eureka Dashboard | http://localhost:8761 |
| 🔐 Auth Service | http://localhost:8081 |
| 👤 User Service | http://localhost:8082 |
| 📦 Product Service | http://localhost:8083 |
| 🛒 Cart Service | http://localhost:8084 |
| 📋 Order Service | http://localhost:8085 |
| 🤖 Recommendation Service | http://localhost:8086 |
| 🔔 Notification Service | http://localhost:8087 |

---

## 🔐 Authentication & Authorization

### Roles

| Role | Permissions |
|------|-------------|
| `USER` | Browse Products, Manage Cart, Place Orders |
| `ADMIN` | Full Platform Access |

### Auth Flow

```bash
# Register
POST /api/auth/register

# Login
POST /api/auth/login

# Admin Login
POST /api/auth/admin/login

# Logout
POST /api/auth/logout
```

---

## 📖 API Documentation

### Auth Service

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/admin/login
POST /api/auth/logout
POST /api/auth/reset-password
```

### User Service

```text
GET    /api/users
GET    /api/users/{id}
GET    /api/users/email/{email}
POST   /api/users
PUT    /api/users/{id}
DELETE /api/users/{id}
```

### Product Service

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

### Cart Service

```text
GET    /api/carts/user/{userId}
POST   /api/carts/user/{userId}/add
PUT    /api/carts/user/{userId}/update
DELETE /api/carts/user/{userId}/remove
DELETE /api/carts/user/{userId}/clear
```

### Order Service

```text
POST   /api/orders
GET    /api/orders/user/{userEmail}
GET    /api/orders/{id}
PUT    /api/orders/{id}/status
DELETE /api/orders/{id}
```

### Recommendation Service

```text
GET /api/recommendations?userEmail={email}
GET /api/recommendations?userEmail={email}&category={cat}
```

### Notification Service

```text
GET    /api/notifications
GET    /api/notifications/{id}
GET    /api/notifications/user/{userId}
PUT    /api/notifications/{id}/read
DELETE /api/notifications/{id}
```

---

## 🏛 Architecture Patterns

| Pattern | Implementation |
|---------|----------------|
| Database per Service | Separate DB for each microservice |
| API Gateway | Single entry point |
| Service Discovery | Eureka Server |
| Centralized Configuration | Config Server |
| Event-Driven Architecture | Kafka |
| Caching | Redis |
| Repository Pattern | Spring Data JPA |
| DTO Pattern | Request/Response DTOs |
| RBAC | USER / ADMIN |

---

## 🚀 Deployment

### Docker Deployment

```bash
mvn clean package

docker-compose up -d
```

### CI/CD

```text
GitHub Actions
```

---

## 🔄 Migration Strategy

### Phase 1
Deploy Infrastructure Services

### Phase 2
Deploy Core Services

### Phase 3
Deploy Business Services

### Phase 4
Deploy AI & Notification Services

### Phase 5
Switch Frontend to API Gateway

### Phase 6
Decommission Monolithic Application

---

## 📝 Notes

- All existing APIs, business logic, and functionality are preserved
- Role-based access control (USER, ADMIN) is maintained
- Database per service pattern is implemented
- Kafka communication is asynchronous between Order and Notification services
- Redis caching is implemented in Product Service
- Spring AI is used for product recommendations

---
