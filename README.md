# Shopping Platform Microservices Architecture

This is a microservices-based implementation of an AI-Powered Shopping Platform, converted from a monolithic Spring Boot application.

## Architecture Overview

The system consists of the following microservices:

### Infrastructure Services
- **Eureka Server** (Port 8761) - Service Discovery
- **Config Server** (Port 8888) - Centralized Configuration

### Core Services
- **API Gateway** (Port 8080) - API Gateway with JWT validation and routing
- **Auth Service** (Port 8081) - Authentication and Authorization with JWT
- **User Service** (Port 8082) - User Profile Management
- **Product Service** (Port 8083) - Product Management with Redis caching
- **Cart Service** (Port 8084) - Shopping Cart Management
- **Order Service** (Port 8085) - Order Processing with Kafka Producer
- **Recommendation Service** (Port 8086) - AI-powered Product Recommendations
- **Notification Service** (Port 8087) - Notification Management with Kafka Consumer

## Technology Stack

- **Java**: 17
- **Spring Boot**: 2.7.18
- **Spring Cloud**: 2021.0.8
- **Spring Security**: JWT-based authentication
- **Spring Data JPA**: Database access
- **MySQL**: Database per service pattern
- **Redis**: Caching for Product Service
- **Apache Kafka**: Event-driven communication
- **Spring AI**: AI-powered recommendations
- **Docker**: Containerization
- **GitHub Actions**: CI/CD

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway (8080)                       │
│                   JWT Validation & Routing                    │
└─────────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
┌───────▼────────┐  ┌────────▼────────┐  ┌──────▼────────┐
│ Auth Service  │  │  User Service   │  │ Product Svc  │
│   (8081)      │  │    (8082)       │  │   (8083)      │
│   + JWT       │  │  + MySQL        │  │  + MySQL      │
│   + MySQL     │  │                 │  │  + Redis      │
└───────────────┘  └─────────────────┘  └───────────────┘
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
┌───────▼────────┐  ┌────────▼────────┐  ┌──────▼────────┐
│ Cart Service   │  │  Order Service  │  │ Recom. Svc   │
│   (8084)       │  │    (8085)       │  │   (8086)      │
│   + MySQL      │  │  + MySQL        │  │  + Spring AI  │
└───────────────┘  │  + Kafka Prod.  │  └───────────────┘
                   └─────────────────┘
                              │
                   ┌──────────▼──────────┐
                   │ Notification Service │
                   │      (8087)         │
                   │  + MySQL            │
                   │  + Kafka Consumer   │
                   └─────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                        Infrastructure                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │ Eureka Server│  │ Config Server │  │   Kafka      │        │
│  │   (8761)     │  │   (8888)      │  │   (9092)     │        │
│  └──────────────┘  └──────────────┘  └──────────────┘        │
│  ┌──────────────┐  ┌──────────────┐                         │
│  │   Redis      │  │  MySQL (x6)   │                         │
│  │   (6379)     │  │  (3307-3312)  │                         │
│  └──────────────┘  └──────────────┘                         │
└─────────────────────────────────────────────────────────────────┘
```

## Service Communication

### Synchronous Communication
- API Gateway routes requests to appropriate services
- Services communicate via REST API (using RestTemplate)
- All services register with Eureka for service discovery

### Asynchronous Communication (Kafka)
- **Order Service** produces events to Kafka topics:
  - `order-created` - When a new order is created
  - `order-cancelled` - When an order is cancelled
  - `order-delivered` - When an order is delivered

- **Notification Service** consumes events from Kafka topics:
  - Listens to all order events
  - Creates notifications for users
  - Handles retry and error scenarios

## Database Schema

Each service has its own database:
- **auth_db**: User authentication data
- **user_db**: User profile data
- **product_db**: Product catalog
- **cart_db**: Shopping cart data
- **order_db**: Order management
- **notification_db**: Notification history

## Security

- JWT-based authentication
- Role-based access control (USER, ADMIN)
- API Gateway validates JWT tokens
- CORS configuration for frontend integration

## Getting Started

### Prerequisites
- Java 17
- Maven 3.x
- Docker & Docker Compose
- MySQL
- Redis
- Apache Kafka

### Running with Docker Compose

```bash
cd microservices
docker-compose up -d
```

This will start all services along with MySQL, Redis, and Kafka.

### Building Individual Services

```bash
mvn clean install
```

### Running Individual Services

Each service can be run independently:
```bash
cd eureka-server
mvn spring-boot:run
```

## API Endpoints

### API Gateway (Port 8080)
- Routes all requests to appropriate services
- JWT validation for protected endpoints

### Auth Service (Port 8081)
- POST `/api/auth/register` - User registration
- POST `/api/auth/login` - User login
- POST `/api/auth/admin/login` - Admin login
- POST `/api/auth/logout` - Logout
- POST `/api/auth/reset-password` - Password reset

### User Service (Port 8082)
- GET `/api/users` - Get all users
- GET `/api/users/{id}` - Get user by ID
- GET `/api/users/email/{email}` - Get user by email
- POST `/api/users` - Create user
- PUT `/api/users/{id}` - Update user
- DELETE `/api/users/{id}` - Delete user

### Product Service (Port 8083)
- GET `/api/products` - Get all products
- GET `/api/products/{id}` - Get product by ID
- GET `/api/products/category/{category}` - Get products by category
- GET `/api/products/search?name={name}` - Search products
- GET `/api/products/price-range?min={min}&max={max}` - Filter by price
- POST `/api/products` - Create product
- PUT `/api/products/{id}` - Update product
- DELETE `/api/products/{id}` - Delete product

### Cart Service (Port 8084)
- GET `/api/carts/user/{userId}` - Get user cart
- POST `/api/carts/user/{userId}/add` - Add item to cart
- PUT `/api/carts/user/{userId}/update` - Update item quantity
- DELETE `/api/carts/user/{userId}/remove` - Remove item from cart
- DELETE `/api/carts/user/{userId}/clear` - Clear cart

### Order Service (Port 8085)
- POST `/api/orders` - Create order
- GET `/api/orders/user/{userEmail}` - Get user orders
- GET `/api/orders/{id}` - Get order by ID
- PUT `/api/orders/{id}/status` - Update order status
- DELETE `/api/orders/{id}` - Cancel order

### Recommendation Service (Port 8086)
- GET `/api/recommendations?userEmail={email}` - Get recommendations
- GET `/api/recommendations?userEmail={email}&category={cat}` - Get category recommendations

### Notification Service (Port 8087)
- GET `/api/notifications` - Get all notifications
- GET `/api/notifications/{id}` - Get notification by ID
- GET `/api/notifications/user/{userId}` - Get user notifications
- PUT `/api/notifications/{id}/read` - Mark as read
- DELETE `/api/notifications/{id}` - Delete notification

## Configuration

All service configurations are managed by the Config Server. Configuration files are located in:
- `config-server/src/main/resources/config/`

## Deployment

### Docker Deployment
```bash
# Build all services
mvn clean package

# Run with Docker Compose
docker-compose up -d
```

### CI/CD
GitHub Actions workflow is configured in `.github/workflows/ci-cd.yml`

## Migration Strategy

1. **Phase 1**: Deploy infrastructure services (Eureka, Config Server)
2. **Phase 2**: Deploy core services (Auth, User, Product)
3. **Phase 3**: Deploy business services (Cart, Order)
4. **Phase 4**: Deploy AI and Notification services
5. **Phase 5**: Switch frontend to use API Gateway
6. **Phase 6**: Decommission monolithic application

## Notes

- All existing APIs, business logic, and functionality are preserved
- Role-based access control (USER, ADMIN) is maintained
- Database per service pattern is implemented
- Kafka communication is strictly asynchronous between Order and Notification services
- Redis caching is implemented in Product Service
- Spring AI is used for product recommendations
