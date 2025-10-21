# final-waiter-service

> SpringBucks Waiter Service - Complete microservice integration with distributed tracing, service discovery, caching, rate limiting, and message-driven architecture

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.0.2-blue.svg)](https://spring.io/projects/spring-cloud)
[![Zipkin](https://img.shields.io/badge/Zipkin-Enabled-blue.svg)](https://zipkin.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

The final integrated version of SpringBucks waiter service, demonstrating a complete microservice architecture with distributed tracing (Zipkin), service discovery (Consul), caching (Redis), rate limiting (Resilience4j), and message-driven communication (RabbitMQ).

## Features

- **Coffee & Order Management**: Complete CRUD operations for coffee products and orders
- **Distributed Tracing**: HTTP-based Zipkin tracing integration (Micrometer Tracing)
- **Service Discovery**: Automatic service registration with Consul
- **Redis Caching**: High-performance caching with `@CacheConfig` annotation
- **Rate Limiting**: Resilience4j rate limiter for coffee and order endpoints
- **Message-Driven**: Spring Cloud Stream + RabbitMQ for async communication
- **Docker Support**: One-command deployment with Docker Compose
- **Health Monitoring**: Custom health indicators + Actuator endpoints
- **Prometheus Integration**: Metrics exportation for monitoring

## Tech Stack

- **Spring Boot 3.4.5** + **Spring Cloud 2024.0.2**
- **Micrometer Tracing + Brave Bridge** (replacing Spring Cloud Sleuth)
- **Zipkin** (HTTP reporter for distributed tracing)
- **Spring Cloud Consul** (Service discovery and config)
- **Spring Data JPA** + **MariaDB 11.8.3**
- **Spring Data Redis** (Caching layer)
- **Spring Cloud Stream** + **RabbitMQ** (Message-driven)
- **Resilience4j** (Rate limiting)
- **Joda Money 2.0.2** (Monetary calculations)
- **Lombok** + **Java 21**

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                   final-waiter-service                       │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Controller  │  │   Service    │  │  Repository  │      │
│  │  (REST API)  │→ │  (@Cacheable)│→ │    (JPA)     │      │
│  └─────────────┘  └──────────────┘  └──────────────┘      │
│         ↓                ↓                    ↓              │
│  ┌─────────────────────────────────────────────────┐       │
│  │           Infrastructure Dependencies           │       │
│  ├─────────────────────────────────────────────────┤       │
│  │ Consul     │ Redis      │ MariaDB    │ RabbitMQ│       │
│  │ (8500)     │ (6379)     │ (3306)     │ (5672)  │       │
│  ├────────────┴────────────┴────────────┴─────────┤       │
│  │                  Zipkin (9411)                  │       │
│  │          (HTTP Tracing Reporter)                │       │
│  └─────────────────────────────────────────────────┘       │
│                                                               │
│  Key Features:                                               │
│  ✓ @RateLimiter("coffee")   - 5 requests/30s                │
│  ✓ @RateLimiter("order")    - 3 requests/30s                │
│  ✓ @CacheConfig("CoffeeCache") - Redis 60s TTL              │
│  ✓ StreamBridge.send()      - Dynamic message routing       │
│  ✓ Custom Health Indicators - Coffee count monitoring       │
└─────────────────────────────────────────────────────────────┘
```

## Getting Started

### Prerequisites

- **JDK 21** or higher
- **Maven 3.8+** (or use included wrapper)
- **Docker** + **Docker Compose** (for complete system deployment)

### Quick Start (Docker Compose - Recommended)

**Step 1: Build Docker Images**

```bash
# Navigate to Chapter 16 directory
cd "Chapter 16 服務鏈路追蹤"

# Build waiter-service image
cd final-waiter-service
mvn clean package -DskipTests
# Output: springbucks/final-waiter-service:0.0.1-SNAPSHOT

# Build customer-service image
cd ../final-customer-service
mvn clean package -DskipTests

# Build barista-service image
cd ../final-barista-service
mvn clean package -DskipTests

# Return to Chapter 16 directory
cd ..
```

**Step 2: Start Complete System (9 Containers)**

```bash
# Start all services
docker-compose up -d

# Check container status
docker-compose ps

# Expected output:
# - mariadb-final-spring-course          (Up)
# - redis-final-spring-course            (Up)
# - consul-final-spring-course           (Up - port 8500)
# - rabbitmq-final-spring-course         (Up - port 15672)
# - zipkin-final-spring-course           (Up - port 9411)
# - final-waiter-service                 (Up - port 8080)
# - final-barista-service                (Up)
# - final-customer-service-8090          (Up - port 8090)
# - final-customer-service-9090          (Up - port 9090)
```

**Step 3: Verify Services**

```bash
# Check Consul UI
open http://localhost:8500

# Check RabbitMQ Management
open http://localhost:15672
# Login: spring / spring

# Check Zipkin UI
open http://localhost:9411

# Check waiter-service health
curl http://localhost:8080/actuator/health
```

### Standalone Execution (Development)

**Prerequisites: Start Infrastructure**

```bash
# Start Consul
docker run -d --name consul -p 8500:8500 consul:1.4.5

# Start MariaDB
docker run -d --name mariadb \
  -e MYSQL_DATABASE=springbucks \
  -e MYSQL_USER=springbucks \
  -e MYSQL_PASSWORD=springbucks \
  -e MYSQL_ROOT_PASSWORD=root_password \
  -p 3306:3306 mariadb:11.8.3

# Start Redis
docker run -d --name redis -p 6379:6379 redis

# Start RabbitMQ
docker run -d --name rabbitmq \
  -e RABBITMQ_DEFAULT_USER=spring \
  -e RABBITMQ_DEFAULT_PASS=spring \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:4.1.4-management

# Start Zipkin
docker run -d --name zipkin -p 9411:9411 openzipkin/zipkin:3-arm64
```

**Run Application**

```bash
# Update application.properties for localhost connections
# Then run:
./mvnw spring-boot:run
```

## API Documentation

### Coffee Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/coffee/` | List all coffees (Redis cached) |
| GET | `/coffee/{id}` | Get coffee by ID |
| POST | `/coffee` | Create new coffee |
| PUT | `/coffee/{id}` | Update coffee |

**Example: Query Coffees (with caching)**

```bash
# First request - from database
curl http://localhost:8080/coffee/

# Second request - from Redis cache (within 60s)
curl http://localhost:8080/coffee/
```

### Order Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/order/` | List all orders |
| GET | `/order/{id}` | Get order by ID |
| POST | `/order/` | Create new order |
| PUT | `/order/{id}` | Update order state |

**Example: Create Order**

```bash
curl -X POST http://localhost:8080/order/ \
  -H "Content-Type: application/json" \
  -d '{
    "customer": "test-user",
    "items": ["espresso", "latte"]
  }'
```

## Configuration Highlights

### Resilience4j Rate Limiting

```properties
# Coffee endpoint: 5 requests per 30 seconds
resilience4j.ratelimiter.instances.coffee.limit-for-period=5
resilience4j.ratelimiter.instances.coffee.limit-refresh-period-in-millis=30000
resilience4j.ratelimiter.instances.coffee.timeout-in-millis=5000

# Order endpoint: 3 requests per 30 seconds
resilience4j.ratelimiter.instances.order.limit-for-period=3
resilience4j.ratelimiter.instances.order.limit-refresh-period-in-millis=30000
```

### Redis Caching Strategy

```properties
spring.cache.type=redis
spring.cache.redis.time-to-live=60000  # 60 seconds TTL
spring.cache.redis.cache-null-values=false

# Note: Actual cache name is defined by @CacheConfig annotation
# @CacheConfig(cacheNames = "CoffeeCache")
# Redis Key: CoffeeCache::SimpleKey []
```

### Zipkin Tracing (HTTP Reporter)

```properties
# HTTP-based tracing reporter (waiter-service uses HTTP, not RabbitMQ)
management.zipkin.tracing.endpoint=http://zipkin-final-spring-course:9411/api/v2/spans
management.tracing.sampling.probability=1.0  # 100% sampling (dev environment)

# Production recommendation: 0.1 (10% sampling)
```

### Spring Cloud Stream Bindings

```properties
# Consumer: Receive finished orders from barista-service
spring.cloud.stream.bindings.finishedOrders-in-0.destination=finishedOrders
spring.cloud.stream.bindings.finishedOrders-in-0.group=waiter-service

# Producer: Send new orders to barista-service
spring.cloud.stream.bindings.newOrders-out-0.destination=newOrders

# Producer: Notify customers with routing key (SpEL expression)
spring.cloud.stream.bindings.notifyOrders-out-0.destination=notifyOrders
spring.cloud.stream.rabbit.bindings.notifyOrders-out-0.producer.routing-key-expression=headers.customer
```

## Monitoring & Observability

### Distributed Tracing with Zipkin

```bash
# View trace in Zipkin UI
open http://localhost:9411

# Search traces by service name
curl "http://localhost:9411/api/v2/traces?serviceName=waiter-service&limit=10" | jq

# Example trace flow:
# customer-service (POST /customer/order)
#   → waiter-service (POST /order/)        [Trace ID: abc123]
#     → waiter-service (PUT /order/{id})
#       → RabbitMQ (newOrders)
#         → barista-service (process)
#           → RabbitMQ (finishedOrders)
#             → waiter-service (update)
#               → RabbitMQ (notifyOrders)
#                 → customer-service (notify)
```

### Health Check & Actuator

```bash
# Custom health indicator (coffee count)
curl http://localhost:8080/actuator/health | jq '.components.coffee'

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus

# Rate limiter metrics
curl http://localhost:8080/actuator/metrics/resilience4j.ratelimiter.available.permissions
```

### Redis Cache Monitoring

```bash
# Connect to Redis container
docker exec -it final-spring-course-redis-final-spring-course-1 redis-cli

# Check cache keys
127.0.0.1:6379> KEYS CoffeeCache::*
1) "CoffeeCache::SimpleKey []"

# Check TTL
127.0.0.1:6379> TTL "CoffeeCache::SimpleKey []"
(integer) 59

# Delete cache (for testing)
127.0.0.1:6379> DEL "CoffeeCache::SimpleKey []"
```

## Message Flow

```
┌──────────────────┐  HTTP POST    ┌──────────────────┐
│ customer-service │──────────────→│  waiter-service  │
│     (8090)       │               │      (8080)      │
└──────────────────┘               └──────────────────┘
                                            │
                                            │ StreamBridge.send
                                            ↓
                                   ┌──────────────────┐
                                   │    RabbitMQ      │
                                   │  (newOrders)     │
                                   └──────────────────┘
                                            │
                                            │ Consumer
                                            ↓
                                   ┌──────────────────┐
                                   │ barista-service  │
                                   │     (8070)       │
                                   └──────────────────┘
                                            │
                                            │ StreamBridge.send
                                            ↓
                                   ┌──────────────────┐
                                   │    RabbitMQ      │
                                   │ (finishedOrders) │
                                   └──────────────────┘
                                            │
                                            │ Consumer
                                            ↓
                                   ┌──────────────────┐
                                   │  waiter-service  │
                                   │  (update order)  │
                                   └──────────────────┘
                                            │
                                            │ StreamBridge.send
                                            │ routing-key=customer
                                            ↓
                                   ┌──────────────────┐
                                   │    RabbitMQ      │
                                   │  (notifyOrders)  │
                                   └──────────────────┘
                                            │
                                            │ Consumer (filtered)
                                            ↓
                                   ┌──────────────────┐
                                   │ customer-service │
                                   │  (notification)  │
                                   └──────────────────┘
```

## Performance Optimization

### Health Check Optimization

**Issue**: `CoffeeIndicator` triggers `SELECT COUNT(*)` every 10 seconds (Consul health check interval)

**Solutions**:

1. **Disable SQL Logging**:
```properties
spring.jpa.properties.hibernate.show_sql=false
```

2. **Add Caching**:
```java
@Cacheable(value = "coffeeHealthCheck", unless = "#result == null")
public Health health() {
    // Health check logic
}
```

## Best Practices

### Rate Limiting Recommendations

| Environment | coffee.limit | order.limit | Reasoning |
|-------------|--------------|-------------|-----------|
| **Development** | 5/30s | 3/30s | Current setting (easy testing) |
| **Production** | 100/30s | 50/30s | Allow higher throughput |
| **Public API** | 10/60s | 5/60s | Prevent abuse |

### Caching Strategy

```java
// Cache configuration priority:
// 1. @CacheConfig(cacheNames = "CoffeeCache")  ← Highest (Code level)
// 2. @Cacheable(cacheNames = "...")           ← Method level
// 3. spring.cache.cache-names=coffee          ← Lowest (Config level)

// Recommendation: Use @CacheConfig for consistency
```

### Zipkin Sampling

```properties
# Development: 100% sampling
management.tracing.sampling.probability=1.0

# Production: 5-10% sampling (reduce performance impact)
management.tracing.sampling.probability=0.1
```

## Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| **Port 8080 already in use** | `lsof -ti:8080 \| xargs kill -9` |
| **Cannot connect to MariaDB** | Check container: `docker ps \| grep mariadb` |
| **Redis cache not working** | Verify Redis connection: `redis-cli PING` |
| **Zipkin trace not showing** | Check sampling rate & endpoint URL |
| **RabbitMQ message not received** | Verify queue binding in RabbitMQ UI |

### Logs Analysis

```bash
# View full logs (with timestamps)
docker logs -f final-spring-course-final-waiter-service-1

# Filter specific operations
docker logs final-spring-course-final-waiter-service-1 | grep "Receive new Order"

# Check Hibernate SQL queries
docker logs final-spring-course-final-waiter-service-1 | grep "Hibernate:"
```

## Comparison with Other Projects

| Project | Tracing | Discovery | Cache | Rate Limiting | Messages |
|---------|---------|-----------|-------|---------------|----------|
| **final-waiter** | ✅ HTTP | ✅ Consul | ✅ Redis | ✅ RateLimiter | ✅ Producer+Consumer |
| **git-config-waiter** | ❌ | ✅ Consul | ✅ Redis | ✅ RateLimiter | ❌ |
| **busy-waiter** | ❌ | ✅ Consul | ✅ Redis | ✅ RateLimiter | ✅ Producer |

## References

- [Micrometer Tracing Documentation](https://micrometer.io/docs/tracing)
- [Spring Boot 3.x Observability](https://docs.spring.io/spring-boot/reference/actuator/tracing.html)
- [Zipkin Official Docs](https://zipkin.io/)
- [Resilience4j Rate Limiter](https://resilience4j.readme.io/docs/ratelimiter)
- [Spring Cloud Stream](https://spring.io/projects/spring-cloud-stream)

## License

This project is licensed under the MIT License.

## Contact

We focus on Agile Project Management, IoT application development, and Domain-Driven Design (DDD), combining advanced technologies with practical experience to create flexible software solutions.

- **Facebook**: [風清雲談](https://www.facebook.com/profile.php?id=61576838896062)
- **LinkedIn**: [linkedin.com/in/chu-kuo-lung](https://www.linkedin.com/in/chu-kuo-lung)
- **YouTube**: [雲談風清](https://www.youtube.com/channel/UCXDqLTdCMiCJ1j8xGRfwEig)
- **Blog**: [風清雲談](https://blog.fengqing.tw/)
- **Email**: [fengqing.tw@gmail.com](mailto:fengqing.tw@gmail.com)

---

**Last Updated**: 2025-10-21  
**Maintainer**: FengQing Team
