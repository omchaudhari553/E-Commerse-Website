# Microservices Architecture Documentation

## System Architecture

This document describes the microservices architecture for the AI-Powered Shopping Platform.

## Design Principles

1. **Single Responsibility**: Each service has a single, well-defined responsibility
2. **Database per Service**: Each service owns its data
3. **API Gateway**: Single entry point for all client requests
4. **Service Discovery**: Eureka for dynamic service registration
5. **Centralized Configuration**: Config Server for externalized configuration
6. **Event-Driven**: Asynchronous communication via Kafka
7. **Containerization**: Docker for deployment consistency

## Service Descriptions

### Infrastructure Services

#### Eureka Server (Port 8761)
- **Purpose**: Service Discovery
- **Technology**: Spring Cloud Netflix Eureka
- **Responsibilities**:
  - Service registration
  - Service discovery
  - Health checking

#### Config Server (Port 8888)
- **Purpose**: Centralized Configuration
- **Technology**: Spring Cloud Config
- **Responsibilities**:
  - Serve configuration files
  - Version control for configuration
  - Environment-specific configurations

### Core Services

#### API Gateway (Port 8080)
- **Purpose**: Single Entry Point
- **Technology**: Spring Cloud Gateway
- **Responsibilities**:
  - Request routing
  - JWT validation
  - Load balancing
  - CORS handling

#### Auth Service (Port 8081)
- **Purpose**: Authentication & Authorization
- **Technology**: Spring Security + JWT
- **Database**: auth_db
- **Responsibilities**:
  - User registration
  - User login
  - JWT token generation
  - Password reset
  - Role management (USER, ADMIN)

#### User Service (Port 8082)
- **Purpose**: User Profile Management
- **Technology**: Spring Data JPA
- **Database**: user_db
- **Responsibilities**:
  - User profile CRUD
  - User role management
  - User information retrieval

#### Product Service (Port 8083)
- **Purpose**: Product Catalog Management
- **Technology**: Spring Data JPA + Redis
- **Database**: product_db
- **Cache**: Redis
- **Responsibilities**:
  - Product CRUD operations
  - Product search and filtering
  - Category-based retrieval
  - Price range filtering
  - Redis caching for performance

#### Cart Service (Port 8084)
- **Purpose**: Shopping Cart Management
- **Technology**: Spring Data JPA
- **Database**: cart_db
- **Responsibilities**:
  - Cart creation and management
  - Add/update/remove items
  - Cart total calculation
  - Stock validation

#### Order Service (Port 8085)
- **Purpose**: Order Processing
- **Technology**: Spring Data JPA + Kafka Producer
- **Database**: order_db
- **Kafka Topics**: order-created, order-cancelled, order-delivered
- **Responsibilities**:
  - Order creation
  - Order status management
  - Stock management
  - Event publishing to Kafka

#### Recommendation Service (Port 8086)
- **Purpose**: AI-Powered Recommendations
- **Technology**: Spring AI (OpenAI)
- **Responsibilities**:
  - Product recommendations
  - Category-based suggestions
  - AI-powered personalization

#### Notification Service (Port 8087)
- **Purpose**: Notification Management
- **Technology**: Spring Data JPA + Kafka Consumer
- **Database**: notification_db
- **Kafka Topics**: order-created, order-cancelled, order-delivered
- **Responsibilities**:
  - Kafka event consumption
  - Notification creation
  - Notification delivery
  - Notification history

## Data Flow

### User Authentication Flow
1. Client sends login request to API Gateway
2. API Gateway routes to Auth Service
3. Auth Service validates credentials
4. Auth Service generates JWT token
5. Token returned to client
6. Client includes token in subsequent requests
7. API Gateway validates token before routing

### Order Creation Flow
1. Client sends order request to API Gateway
2. API Gateway validates JWT and routes to Order Service
3. Order Service validates product stock
4. Order Service creates order in database
5. Order Service publishes event to Kafka (order-created)
6. Notification Service consumes event
7. Notification Service creates notification
8. Order confirmation returned to client

### Product Recommendation Flow
1. Client requests recommendations
2. API Gateway routes to Recommendation Service
3. Recommendation Service fetches products from Product Service
4. Recommendation Service uses AI for personalization
5. Recommendations returned to client

## Communication Patterns

### Synchronous Communication
- REST API calls between services
- Used for request-response patterns
- Examples: Cart Service calling Product Service for stock validation

### Asynchronous Communication
- Kafka-based event streaming
- Used for decoupled communication
- Examples: Order Service publishing events, Notification Service consuming events

## Security Architecture

### JWT Authentication
- Token-based authentication
- Tokens contain user ID and role
- Token expiration: 24 hours
- Secret key for signing

### Role-Based Access Control
- USER role for regular users
- ADMIN role for administrative functions
- Role validation in services

### CORS Configuration
- Allowed origins: Production URLs, localhost
- Allowed methods: GET, POST, PUT, DELETE, OPTIONS
- Credentials support enabled

## Database Design

### Database per Service Pattern
Each service has its own database:
- **auth_db**: User authentication credentials
- **user_db**: User profile information
- **product_db**: Product catalog
- **cart_db**: Shopping cart data
- **order_db**: Order and order items
- **notification_db**: Notification history

### Entity Relationships
- User (Auth Service) - Authentication data
- User (User Service) - Profile data
- Product (Product Service) - Product catalog
- Cart (Cart Service) - Shopping cart
- Order (Order Service) - Order management
- Notification (Notification Service) - Notification history

## Caching Strategy

### Redis Caching (Product Service)
- Cache product data for 10 minutes
- Cache keys: product ID, category, search terms, price ranges
- Cache eviction on product updates
- Improves read performance

## Event-Driven Architecture

### Kafka Topics
- **order-created**: Published when order is created
- **order-cancelled**: Published when order is cancelled
- **order-delivered**: Published when order is delivered

### Event Payload
```json
{
  "eventType": "ORDER_CREATED",
  "orderId": 123,
  "userId": 456,
  "userEmail": "user@example.com",
  "timestamp": "2024-01-01T10:00:00",
  "status": "PENDING"
}
```

### Consumer Configuration
- Group ID: notification-group
- Auto offset reset: earliest
- Retry mechanism: Built-in Kafka retry
- Dead letter topic: For failed events

## Deployment Architecture

### Docker Containerization
- Each service packaged as Docker image
- Multi-stage builds for optimization
- Consistent runtime environment

### Docker Compose
- Orchestrates all services
- Manages dependencies
- Network isolation
- Volume persistence for databases

### CI/CD Pipeline
- Build: Maven compilation and testing
- Package: JAR creation
- Docker: Image building
- Push: Docker Hub registry
- Deploy: Production deployment

## Monitoring and Observability

### Service Discovery
- Eureka Server for service registration
- Health checks for service monitoring
- Dynamic service lookup

### Logging
- Structured logging in each service
- Log levels: INFO, WARN, ERROR
- Request/response logging

## Scalability Considerations

### Horizontal Scaling
- Stateless services can be scaled horizontally
- Load balancing via Eureka
- Session state not maintained

### Database Scaling
- Database per service allows independent scaling
- Read replicas can be added
- Connection pooling configured

## Fault Tolerance

### Circuit Breaker
- Resilience4j for circuit breaking
- Fallback mechanisms
- Timeout configurations

### Retry Mechanism
- Kafka consumer retry
- HTTP request retry
- Exponential backoff

## Migration Strategy

### Phase 1: Infrastructure Setup
- Deploy Eureka Server
- Deploy Config Server
- Configure service discovery

### Phase 2: Core Services
- Deploy Auth Service
- Deploy User Service
- Deploy Product Service

### Phase 3: Business Services
- Deploy Cart Service
- Deploy Order Service
- Deploy Notification Service

### Phase 4: AI Services
- Deploy Recommendation Service
- Configure Spring AI integration

### Phase 5: Gateway Integration
- Deploy API Gateway
- Configure routing
- Update frontend URLs

### Phase 6: Decommission
- Monitor system stability
- Decommission monolithic application
- Clean up legacy code

## Performance Optimization

### Caching
- Redis for product data
- Application-level caching
- Cache invalidation strategy

### Database Optimization
- Connection pooling
- Query optimization
- Indexing strategy

### Network Optimization
- Service-to-service communication
- Load balancing
- Request batching

## Future Enhancements

### Potential Improvements
- Add API rate limiting
- Implement distributed tracing
- Add metrics collection (Prometheus)
- Implement service mesh (Istio)
- Add API documentation (Swagger)
- Implement blue-green deployment
