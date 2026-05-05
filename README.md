# Bookstore Microservices Project

A comprehensive Spring Boot 3.x microservices architecture for a bookstore management system with event-driven communication, service discovery, and containerized deployment.

## Architecture Overview

This project implements a full-featured bookstore microservices system with the following components:

### Infrastructure Services
- **Eureka Server** (8761) - Service discovery and registration
- **Config Server** (8888) - Centralized configuration management
- **API Gateway** (8080) - Single entry point with routing

### Core Business Services
- **User Service** (8081) - User management and authentication
- **Admin Service** (8082) - Administrative operations with audit logging
- **Product Service** (8083) - Product catalog management
- **Cart Service** (8084) - Shopping cart functionality (Redis)
- **Wishlist Service** (8085) - User wishlist management
- **Customer Details Service** (8086) - Extended customer information
- **Order Service** (8087) - Order processing and management
- **Feedback Service** (8088) - Product reviews and ratings
- **Notification Service** (8089) - Event-driven email notifications

### Supporting Infrastructure
- **Apache Kafka** (9092) - Event streaming and message queuing
- **Redis** (6379) - Caching and session storage
- **PostgreSQL** (Multiple databases) - Persistent data storage

## Prerequisites

- **Java 17** - Required for Spring Boot 3.x
- **Maven 3.8+** - Build tool
- **Docker Desktop** - Container orchestration
- **Git** - Version control

## Quick Start

### 1. Clone and Build

```bash
git clone <repository-url>
cd bookstore-microservices

# Build all services
mvn clean package -DskipTests
```

### 2. Start All Services

```bash
# Start infrastructure and all services
docker-compose up --build

# Or start in detached mode
docker-compose up --build -d
```

### 3. Verify Services

```bash
# Check service health
docker-compose ps

# View logs
docker-compose logs -f
```

## API Endpoints

### Gateway Routes
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **Config Server**: http://localhost:8888

### Service Documentation (Swagger UI)
- **User Service**: http://localhost:8081/swagger-ui.html
- **Admin Service**: http://localhost:8082/swagger-ui.html
- **Product Service**: http://localhost:8083/swagger-ui.html
- **Cart Service**: http://localhost:8084/swagger-ui.html
- **Wishlist Service**: http://localhost:8085/swagger-ui.html
- **Order Service**: http://localhost:8087/swagger-ui.html
- **Feedback Service**: http://localhost:8088/swagger-ui.html

## Testing Guide

### 1. User Registration and Authentication

```bash
# Register a new user
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'

# Login and get JWT token
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### 2. Product Management

```bash
# Get all products
curl -X GET http://localhost:8080/api/products

# Add product to cart (requires JWT)
JWT_TOKEN="your-jwt-token-here"
curl -X POST http://localhost:8080/api/cart/items \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

### 3. Order Processing

```bash
# Place order from cart
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $JWT_TOKEN"

# Get order details
curl -X GET http://localhost:8080/api/orders/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

### 4. Feedback System

```bash
# Submit product review
curl -X POST http://localhost:8080/api/feedback \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "rating": 5,
    "comment": "Excellent book!"
  }'

# Get product reviews
curl -X GET http://localhost:8080/api/feedback/product/1
```

### 5. Admin Operations

```bash
# Create admin account (requires SUPER_ADMIN)
curl -X POST http://localhost:8080/api/admin/register \
  -H "Authorization: Bearer $ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123",
    "role": "ADMIN"
  }'

# Get all users (ADMIN only)
curl -X GET http://localhost:8080/api/admin/all-users \
  -H "Authorization: Bearer $ADMIN_JWT_TOKEN"
```

## Database Configuration

### PostgreSQL Databases
- **user-db**: localhost:5433 - User management
- **admin-db**: localhost:5434 - Admin operations and audit logs
- **product-db**: localhost:5435 - Product catalog
- **wishlist-db**: localhost:5436 - Wishlist data
- **customer-db**: localhost:5437 - Customer details
- **order-db**: localhost:5438 - Order management
- **feedback-db**: localhost:5439 - Reviews and ratings

**Default Credentials:**
- Username: `admin`
- Password: `secret`

### Redis Configuration
- **Host**: localhost:6379
- **Used by**: Cart Service for session and cart data

## Kafka Topics

The system uses Kafka for event-driven communication:

### Order Events (`order-events`)
- `ORDER_PLACED` - New order created
- `ORDER_SHIPPED` - Order shipped to customer
- `ORDER_DELIVERED` - Order delivered successfully
- `ORDER_CANCELLED` - Order cancelled by user

### Review Events (`review-events`)
- `REVIEW_SUBMITTED` - New review posted
- `REVIEW_UPDATED` - Review modified
- `REVIEW_DELETED` - Review removed

### User Events (`user-events`)
- `USER_REGISTERED` - New user registration

## Security

### JWT Authentication
- **Secret**: Configurable via `JWT_SECRET` environment variable
- **Token Expiration**: 24 hours (configurable)
- **Roles**: USER, ADMIN, SUPER_ADMIN

### Service Security
- All services use JWT-based authentication
- Admin services require elevated permissions
- Circuit breaker pattern for resilience

## Monitoring and Health Checks

### Health Endpoints
All services expose health endpoints:
```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
# ... etc for all services
```

### Eureka Dashboard
- **URL**: http://localhost:8761
- View registered services and their status

## Development

### Local Development

For local development without Docker:

1. Start infrastructure services:
```bash
docker-compose up -d zookeeper kafka redis user-db admin-db product-db wishlist-db customer-db order-db feedback-db
```

2. Start Spring Boot services individually:
```bash
cd eureka-server && mvn spring-boot:run
cd config-server && mvn spring-boot:run
# ... start other services
```

### Environment Variables

Key environment variables for configuration:

```bash
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/service_db
DB_USER=admin
DB_PASS=secret

# Service Discovery
EUREKA_URL=http://localhost:8761/eureka/

# Message Broker
KAFKA_BOOTSTRAP=localhost:9092

# Security
JWT_SECRET=your-secret-key-here
```

## Troubleshooting

### Common Issues

1. **Port Conflicts**: Ensure ports 8080-8089 are available
2. **Database Connection**: Verify PostgreSQL containers are running
3. **Service Registration**: Check Eureka dashboard for service registration
4. **Kafka Connection**: Verify Kafka and Zookeeper are healthy

### Logs

View logs for specific services:
```bash
docker-compose logs -f user-service
docker-compose logs -f order-service
docker-compose logs -f kafka
```

### Reset System

To reset the entire system:
```bash
docker-compose down -v
docker-compose up --build
```

## Architecture Patterns

### Design Patterns Implemented
- **Microservices Architecture**: Service decomposition by business domain
- **API Gateway Pattern**: Single entry point with routing
- **Service Discovery**: Eureka for dynamic service registration
- **Circuit Breaker**: Resilience4j for fault tolerance
- **Event-Driven Architecture**: Kafka for asynchronous communication
- **CQRS Pattern**: Separate read/write operations where applicable
- **Audit Logging**: AOP-based audit trail for admin operations

### Communication Patterns
- **Synchronous**: REST APIs with Feign clients
- **Asynchronous**: Kafka event streaming
- **Caching**: Redis for cart and session data

## Production Considerations

### Scaling
- Horizontal scaling of stateless services
- Database connection pooling
- Kafka partitioning for high throughput

### Security
- Environment variable management for secrets
- Network segmentation between services
- SSL/TLS termination at gateway

### Monitoring
- Centralized logging (ELK stack recommended)
- Metrics collection (Prometheus/Grafana)
- Distributed tracing (Zipkin/Sleuth)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.