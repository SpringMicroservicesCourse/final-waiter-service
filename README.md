# final-waiter-service

> SpringBucks waiter service - Complete microservice integration with distributed tracing, Redis caching, rate limiting, and bidirectional messaging

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.0.2-blue.svg)](https://spring.io/projects/spring-cloud)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Zipkin](https://img.shields.io/badge/Zipkin-HTTP-blue.svg)](https://zipkin.io/)
[![Redis](https://img.shields.io/badge/Redis-Cache-red.svg)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

The final integrated version of SpringBucks waiter service, demonstrating a complete production-ready microservice with distributed tracing (Zipkin HTTP reporter), service discovery (Consul + Config), Redis caching, Resilience4j rate limiting, bidirectional messaging (RabbitMQ), and Docker Compose deployment.

## Features

- **Complete REST API**: Coffee and order management with full CRUD operations
- **Distributed Tracing**: HTTP-based Zipkin tracing (Micrometer Tracing replaces Sleuth)
- **Service Discovery**: Consul registration and discovery
- **Consul Config**: External configuration management (enabled)
- **Redis Caching**: High-performance caching with `@CacheConfig` annotation
- **Rate Limiting**: Resilience4j rate limiter for coffee (5/30s) and order (3/30s) endpoints
- **Bidirectional Messaging**: Spring Cloud Stream consumer + producer
- **Dynamic Routing**: Routing key generation from message headers
- **Docker Compose Ready**: Complete containerization with dockerfile-maven-plugin
- **Health Monitoring**: Custom health indicators + 35+ Actuator endpoints
- **Performance Monitoring**: Prometheus metrics exportation
- **Custom Order Counter**: MeterBinder-based metrics

## Tech Stack

- Spring Boot 3.4.5
- Spring Cloud 2024.0.2
- Spring Cloud Consul Discovery + Config
- Spring Cloud Stream 4.x
- Spring Data JPA + MariaDB 11.8.3
- Spring Data Redis (Caching)
- RabbitMQ 4.1.4 (Message broker)
- Micrometer Tracing + Brave Bridge
- Zipkin (HTTP reporter)
- Resilience4j Spring Boot 3
- Java 21
- Joda Money 2.0.2
- Vavr 0.10.4
- Jackson Hibernate6 Module
- Apache Commons Lang3
- Lombok
- Maven 3.8+

## Getting Started

### Prerequisites

- JDK 21 or higher
- Maven 3.8+ (or use included Maven Wrapper)
- Docker + Docker Compose (for complete system deployment)

### Quick Start (Docker Compose - Recommended)

**Step 1: Build All Docker Images**

```bash
# Navigate to Chapter 16 directory
cd "200-AREA/arch/coding/java/spring/Spring微服務架構實戰/Code/Chapter 16 服務鏈路追蹤"

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

cd ..
```

**Step 2: Start Complete System (9 Containers)**

```bash
# Start all services
docker-compose up -d

# Check container status
docker-compose ps

# Expected containers:
# - final-waiter-service          (Up - port 8080)
# - final-barista-service         (Up)
# - final-customer-service-8090   (Up - port 8090)
# - final-customer-service-9090   (Up - port 9090)
# - mariadb-final-spring-course   (Up)
# - redis-final-spring-course     (Up)
# - consul-final-spring-course    (Up - port 8500)
# - rabbitmq-final-spring-course  (Up - port 15672)
# - zipkin-final-spring-course    (Up - port 9411)
```

**Step 3: Verify Services**

```bash
# Check Consul UI (service registration)
open http://localhost:8500

# Check RabbitMQ Management
open http://localhost:15672
# Login: spring / spring

# Check Zipkin UI (distributed tracing)
open http://localhost:9411

# Check waiter-service health
curl http://localhost:8080/actuator/health | jq
```

**Step 4: Test Complete Workflow**

```bash
# Create order from customer-service
curl -X POST http://localhost:8090/customer/order | jq

# Check waiter-service logs
docker logs -f final-spring-course-final-waiter-service-1

# Expected log sequence:
# 1. "Receive new Order NewOrderRequest(customer=spring-8090, ...)"
# 2. "New Order: CoffeeOrder(id=1, state=INIT, ...)"
# 3. "Updated Order: CoffeeOrder(id=1, state=PAID, ...)"
# 4. "We've finished an order [1]."
# 5. "Notify the customer: spring-8090"
# 6. "Updated Order: CoffeeOrder(id=1, state=TAKEN, ...)"
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

# Create database tables
docker cp src/main/resources/schema.sql mariadb:/tmp/
docker exec -it mariadb \
  mariadb -u springbucks -pspringbucks springbucks < /tmp/schema.sql

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

**Update Configuration for Localhost**

```properties
# application.properties (change container names to localhost)
spring.datasource.url=jdbc:mariadb://localhost:3306/springbucks
spring.data.redis.host=localhost
spring.rabbitmq.host=localhost
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans
```

**Run Application**

```bash
./mvnw spring-boot:run
```

## Configuration

### Application Properties

```properties
# Server configuration
server.port=8080

# MariaDB connection
spring.datasource.url=jdbc:mariadb://mariadb-final-spring-course:3306/springbucks
spring.datasource.username=springbucks
spring.datasource.password=springbucks
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JPA/Hibernate configuration
spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true

# SQL initialization
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.data-locations=classpath:data.sql

# Redis cache configuration
spring.cache.type=redis
spring.cache.cache-names=coffee  # Note: Code uses CoffeeCache
spring.cache.redis.time-to-live=60000  # 60s TTL
spring.cache.redis.cache-null-values=false
spring.data.redis.host=redis-final-spring-course
spring.data.redis.port=6379

# Zipkin distributed tracing (HTTP reporter)
management.tracing.enabled=true
management.tracing.web.enabled=true
management.zipkin.tracing.endpoint=http://zipkin-final-spring-course:9411/api/v2/spans
management.tracing.sampling.probability=1.0  # 100% sampling (dev)

# Logging pattern with trace ID
logging.pattern.level=%5p [${spring.zipkin.service.name:${spring.application.name:}},%X{traceId:-},%X{spanId:-}]

# Resilience4j rate limiting - Coffee endpoint
resilience4j.ratelimiter.instances.coffee.limit-for-period=5
resilience4j.ratelimiter.instances.coffee.limit-refresh-period=30000
resilience4j.ratelimiter.instances.coffee.timeout-duration=5000

# Resilience4j rate limiting - Order endpoint
resilience4j.ratelimiter.instances.order.limit-for-period=3
resilience4j.ratelimiter.instances.order.limit-refresh-period=30000
resilience4j.ratelimiter.instances.order.timeout-duration=1000

# RabbitMQ connection
spring.rabbitmq.host=rabbitmq-final-spring-course
spring.rabbitmq.port=5672
spring.rabbitmq.username=spring
spring.rabbitmq.password=spring

# Spring Cloud Stream function definition
spring.cloud.function.definition=finishedOrders

# Input binding - receive order completion from barista
spring.cloud.stream.bindings.finishedOrders-in-0.destination=finishedOrders
spring.cloud.stream.bindings.finishedOrders-in-0.group=waiter-service

# Output binding - send new orders to barista
spring.cloud.stream.bindings.newOrders-out-0.destination=newOrders
stream.bindings.new-orders-binding=newOrders-out-0

# Output binding - notify customers with routing key
spring.cloud.stream.bindings.notifyOrders-out-0.destination=notifyOrders
spring.cloud.stream.rabbit.bindings.notifyOrders-out-0.producer.routing-key-expression=headers.customer
stream.bindings.notify-orders-binding=notifyOrders-out-0

# Order configuration
order.discount=95  # 95% = 5% off
```

### Bootstrap Properties

```properties
spring.application.name=waiter-service

spring.cloud.consul.host=consul-final-spring-course
spring.cloud.consul.port=8500
spring.cloud.consul.discovery.prefer-ip-address=true

spring.cloud.consul.config.enabled=true  # ← Consul Config enabled!
spring.cloud.consul.config.format=yaml
```

## API Endpoints

### Coffee Management

| Method | Endpoint | Description | Rate Limit |
|--------|----------|-------------|------------|
| GET | `/coffee/` | Get all coffees | 5/30s |
| GET | `/coffee/{id}` | Get coffee by ID | 5/30s |
| GET | `/coffee/?name={name}` | Get coffee by name | 5/30s |
| POST | `/coffee/` | Create new coffee | 5/30s |

**Examples:**

```bash
# Get all coffees (cached in Redis)
curl http://localhost:8080/coffee/ | jq

# Create new coffee
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/json" \
  -d '{"name":"americano","price":120.0}' | jq
```

### Order Management

| Method | Endpoint | Description | Rate Limit |
|--------|----------|-------------|------------|
| GET | `/order/{id}` | Get order by ID | 3/30s |
| POST | `/order/` | Create new order | 3/30s |
| PUT | `/order/{id}` | Update order state | No limit |

**Examples:**

```bash
# Create order
curl -X POST http://localhost:8080/order/ \
  -H "Content-Type: application/json" \
  -d '{
    "customer": "spring-8090",
    "items": ["capuccino"]
  }' | jq

# Update order to PAID (triggers message to barista)
curl -X PUT http://localhost:8080/order/1 \
  -H "Content-Type: application/json" \
  -d '{"state":"PAID"}' | jq

# Get order details
curl http://localhost:8080/order/1 | jq
```

## Architecture

### Complete System Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                   final-waiter-service                        │
├──────────────────────────────────────────────────────────────┤
│                                                                │
│  ┌────────────────────────────────────────────────────────┐  │
│  │         REST API Controllers                           │  │
│  │  - CoffeeController: Coffee CRUD                       │  │
│  │    @RateLimiter("coffee") - 5 requests/30s            │  │
│  │  - CoffeeOrderController: Order management             │  │
│  │    @RateLimiter("order") - 3 requests/30s             │  │
│  └─────────────┬──────────────────────────────────────────┘  │
│                │                                               │
│  ┌─────────────▼──────────────────────────────────────────┐  │
│  │         Business Services                              │  │
│  │  - CoffeeService:                                      │  │
│  │    @CacheConfig(cacheNames = "CoffeeCache")           │  │
│  │    @Cacheable - getAllCoffee() → Redis 60s TTL        │  │
│  │  - CoffeeOrderService:                                 │  │
│  │    updateState(PAID) → StreamBridge.send()            │  │
│  │    MeterBinder - Custom order counter                  │  │
│  └─────────────┬──────────────────────────────────────────┘  │
│                │                                               │
│  ┌─────────────▼──────────────────────────────────────────┐  │
│  │         Message Integration                            │  │
│  │  Consumer: finishedOrders                              │  │
│  │    ← Receives completion from barista-service          │  │
│  │  Producer: newOrders (via StreamBridge)                │  │
│  │    → Sends to barista-service when state=PAID          │  │
│  │  Producer: notifyOrders (via StreamBridge)             │  │
│  │    → Sends to customer-service with routing key        │  │
│  └────────────────────────────────────────────────────────┘  │
│                                                                │
│  ┌────────────────────────────────────────────────────────┐  │
│  │           Infrastructure Dependencies                  │  │
│  ├────────────┬────────────┬──────────────┬──────────────┤  │
│  │  Consul    │  MariaDB   │    Redis     │  RabbitMQ    │  │
│  │  (8500)    │  (3306)    │   (6379)     │  (5672)      │  │
│  ├────────────┴────────────┴──────────────┴──────────────┤  │
│  │           Zipkin (9411) - HTTP Reporter                 │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

### Key Features Demonstrated

| Feature | Implementation | Purpose |
|---------|---------------|---------|
| **Redis Caching** | `@CacheConfig("CoffeeCache")` + `@Cacheable` | Reduce database load |
| **Rate Limiting** | `@RateLimiter("coffee")`, `@RateLimiter("order")` | Prevent API abuse |
| **Bidirectional Messaging** | Consumer + Producer (StreamBridge) | Async communication |
| **Dynamic Routing** | Routing key from message header | Precise message delivery |
| **Distributed Tracing** | Micrometer Tracing + Zipkin (HTTP) | End-to-end visibility |
| **Service Discovery** | Consul (discovery + config) | Dynamic service location |
| **Health Monitoring** | Custom CoffeeIndicator | Business-level health check |
| **Docker Packaging** | dockerfile-maven-plugin | Automated containerization |

## Configuration Highlights

### Redis Caching Strategy

**Configuration:**

```properties
spring.cache.type=redis
spring.cache.cache-names=coffee  # Declared but not used
spring.cache.redis.time-to-live=60000  # 60s TTL (applied to all caches)
spring.cache.redis.cache-null-values=false
```

**Code Configuration (Higher Priority):**

```java
@Service
@CacheConfig(cacheNames = "CoffeeCache")  // ← Actual cache name used
public class CoffeeService {
    @Cacheable  // getAllCoffee() cached in Redis
    public List<Coffee> getAllCoffee() {
        return coffeeRepository.findAll(Sort.by("id"));
    }
    
    @CacheEvict(allEntries = true)  // Clear cache on coffee save
    public Coffee saveCoffee(String name, Money price) {
        return coffeeRepository.save(Coffee.builder().name(name).price(price).build());
    }
}
```

**Actual Redis Key:**

```
CoffeeCache::SimpleKey []  ← Not "coffee::" (code takes precedence)
```

**Verify Caching:**

```bash
# First request - from database (SQL log appears)
curl http://localhost:8080/coffee/

# Second request - from Redis (no SQL log)
curl http://localhost:8080/coffee/

# Check Redis
docker exec -it final-spring-course-redis-final-spring-course-1 redis-cli
127.0.0.1:6379> KEYS CoffeeCache::*
1) "CoffeeCache::SimpleKey []"

127.0.0.1:6379> TTL "CoffeeCache::SimpleKey []"
(integer) 59  # Seconds remaining

# Delete cache (force reload from database)
127.0.0.1:6379> DEL "CoffeeCache::SimpleKey []"
```

**Health Check SQL Queries:**

```log
# Note: Consul health check triggers COUNT(*) every 10 seconds
Hibernate: select count(*) from t_coffee c1_0
Hibernate: select count(*) from t_coffee c1_0
... (repeats every 10s)
```

**Reason**: `CoffeeIndicator` calls `coffeeService.getCoffeeCount()` on each health check.

**Optimization**:

```java
// Option 1: Disable SQL logging
spring.jpa.properties.hibernate.show_sql=false

// Option 2: Cache health check result
@Cacheable(value = "coffeeHealthCheck", unless = "#result == null")
public Health health() {
    // ...
}
```

### Distributed Tracing (Zipkin HTTP Reporter)

**Configuration:**

```properties
# HTTP-based reporter (simple and synchronous)
management.zipkin.tracing.endpoint=http://zipkin-final-spring-course:9411/api/v2/spans
management.tracing.sampling.probability=1.0  # 100% sampling

# Logging pattern with trace ID
logging.pattern.level=%5p [${spring.zipkin.service.name:${spring.application.name:}},%X{traceId:-},%X{spanId:-}]
```

**Why HTTP vs RabbitMQ?**

| Service | Reporter Type | Reasoning |
|---------|--------------|-----------|
| **waiter-service** | HTTP | Moderate load, simple REST API |
| **customer-service** | HTTP | Low load, user-facing UI |
| **barista-service** | RabbitMQ | High throughput, pure async processing |

**Trace Example:**

```bash
# View trace in Zipkin UI
open http://localhost:9411

# Search for specific trace ID
curl "http://localhost:9411/api/v2/trace/68f73d0e8dc7498268107478980a37de" | jq

# Filter waiter-service traces
curl "http://localhost:9411/api/v2/traces?serviceName=waiter-service&limit=10" | jq
```

### Resilience4j Rate Limiting

**Coffee Endpoint:**

```properties
resilience4j.ratelimiter.instances.coffee.limit-for-period=5
# Allow 5 requests per period

resilience4j.ratelimiter.instances.coffee.limit-refresh-period=30000
# Period: 30 seconds

resilience4j.ratelimiter.instances.coffee.timeout-duration=5000
# Wait maximum 5 seconds for permission
```

**Order Endpoint:**

```properties
resilience4j.ratelimiter.instances.order.limit-for-period=3
# Allow 3 requests per 30 seconds (more restrictive)

resilience4j.ratelimiter.instances.order.timeout-duration=1000
# Wait maximum 1 second
```

**Test Rate Limiting:**

```bash
# Test coffee endpoint (will fail after 5 requests in 30s)
for i in {1..7}; do
  echo "Request $i:"
  curl http://localhost:8080/coffee/
  echo ""
done

# Expected:
# Requests 1-5: Success (200 OK)
# Request 6-7: Rate limited (waits up to 5s for permission)

# Check rate limiter metrics
curl http://localhost:8080/actuator/metrics/resilience4j.ratelimiter.available.permissions | jq
```

### Spring Cloud Stream Bindings

**Consumer Binding (finishedOrders):**

```properties
spring.cloud.function.definition=finishedOrders
spring.cloud.stream.bindings.finishedOrders-in-0.destination=finishedOrders
spring.cloud.stream.bindings.finishedOrders-in-0.group=waiter-service
spring.cloud.stream.rabbit.bindings.finishedOrders-in-0.consumer.durable-subscription=true
```

**Producer Bindings (newOrders, notifyOrders):**

```properties
# Send to barista (no routing key)
spring.cloud.stream.bindings.newOrders-out-0.destination=newOrders
stream.bindings.new-orders-binding=newOrders-out-0

# Send to customer (with routing key)
spring.cloud.stream.bindings.notifyOrders-out-0.destination=notifyOrders
spring.cloud.stream.rabbit.bindings.notifyOrders-out-0.producer.routing-key-expression=headers.customer
stream.bindings.notify-orders-binding=notifyOrders-out-0
```

**Code Implementation:**

```java
@Component
@Slf4j
public class OrderListener {
    @Autowired
    private StreamBridge streamBridge;
    
    @Autowired
    private CoffeeOrderService orderService;
    
    @Value("${stream.bindings.notify-orders-binding}")
    private String notifyOrdersBindingFromConfig;

    @Bean
    public Consumer<Long> finishedOrders() {
        return id -> {
            log.info("We've finished an order [{}].", id);
            
            // Get order details (includes customer name)
            CoffeeOrder order = orderService.get(id);
            
            // Build message with routing key in header
            Message<Long> message = MessageBuilder.withPayload(id)
                    .setHeader("customer", order.getCustomer())  // ← Routing key!
                    .build();
            
            log.info("Notify the customer: {}", order.getCustomer());
            streamBridge.send(notifyOrdersBindingFromConfig, message);
        };
    }
}
```

## Message Flow

### Complete Workflow

```
1. Customer Service (POST /customer/order)
   ↓ Feign HTTP
2. Waiter Service (POST /order/)
   - Create order: state = INIT
   ↓
3. Customer Service (PUT /order/{id} state=PAID via Feign)
   ↓
4. Waiter Service (CoffeeOrderService.updateState)
   - Update state: INIT → PAID
   - StreamBridge.send("newOrders-out-0", orderId)
   ↓
5. RabbitMQ (newOrders Exchange)
   - Queue: newOrders.barista-service
   ↓
6. Barista Service (Consumer: newOrders)
   - Process order (~30ms)
   - Update state: PAID → BREWED
   - Set barista ID
   - StreamBridge.send("finishedOrders-out-0", orderId)
   ↓
7. RabbitMQ (finishedOrders Exchange)
   - Queue: finishedOrders.waiter-service
   ↓
8. Waiter Service (Consumer: finishedOrders) ← YOU ARE HERE
   - Receive orderId
   - Query order details
   - Build message with customer header
   - StreamBridge.send("notifyOrders-out-0", message)
   ↓
9. RabbitMQ (notifyOrders Exchange)
   - Routing key: spring-8090 (from header)
   - Routes to: notifyOrders.customer-service-8090
   ↓
10. Customer Service (Consumer: notifyOrders)
    - Receive notification (only if routing key matches)
    - Update state: BREWED → TAKEN
```

**Processing Time Breakdown:**

| Step | Service | Operation | Time |
|------|---------|-----------|------|
| 1-3 | customer → waiter | HTTP calls (create + pay) | ~320ms |
| 4 | waiter | StreamBridge send | ~50ms |
| 5-6 | barista | Order processing | ~250ms |
| 7-8 | waiter | Receive + notify customer | ~40ms |
| 9-10 | customer | Receive + update TAKEN | ~80ms |
| **Total** | **End-to-end** | **Complete workflow** | **~740ms** |

## Distributed Tracing

### Zipkin Trace Analysis

**View Complete Trace:**

```bash
# Open Zipkin UI
open http://localhost:9411

# Example Trace ID: 68f73d0e8dc7498268107478980a37de
# Total Spans: 20
# Services: 3 (customer, waiter, barista)
```

**Span Breakdown:**

```
Span #1:  customer-service  POST /customer/order               [411ms - ROOT]
Span #2:  waiter-service    POST /order/                       [192ms - HTTP]
Span #3:  waiter-service    PUT /order/{id}                    [129ms - HTTP]
Span #4:  waiter-service    stream-bridge process              [7.6ms]
Span #5:  waiter-service    newOrders-out-0 send               [38ms]
Span #6:  waiter-service    neworders/neworders send           [3.7ms]
Span #7:  barista-service   neworders.barista-service receive  [219ms]
Span #8:  barista-service   new-orders process                 [196ms]
Span #9:  barista-service   stream-bridge process              [1ms]
Span #10: barista-service   finishedOrders-out-0 send          [16ms]
Span #11: barista-service   finishedorders/finished.orders send [2.6ms]
Span #12: waiter-service    finishedorders.waiter-service receive [23ms]
Span #13: waiter-service    finished-orders process            [17ms]
Span #14: waiter-service    stream-bridge process              [0.5ms]
Span #15: waiter-service    notifyOrders-out-0 send            [2.7ms]
Span #16: waiter-service    notifyorders/spring-8090 send      [0.8ms]
Span #17: customer-service  notifyorders.customer-service-8090 receive [62ms]
Span #18: customer-service  notify-orders process              [45ms]
Span #19: waiter-service    GET /order/{id}                    [15ms]
Span #20: waiter-service    PUT /order/{id}                    [18ms]
```

**Key Observations:**

- ✅ Complete end-to-end tracing (20 spans)
- ⏱️ Barista processing is the bottleneck (196ms)
- 🚀 RabbitMQ message sending is very fast (<5ms)
- 📊 Each message send has 2 spans (Spring Integration + RabbitMQ)
- 🔄 HTTP calls: 6 total (2 create/update + 4 query/update)

**Log Example with Trace ID:**

```log
INFO  [waiter-service,68f73d0e8dc7498268107478980a37de,abc] - Receive new Order
INFO  [waiter-service,68f73d0e8dc7498268107478980a37de,def] - Updated Order: state=PAID
INFO  [barista-service,68f73d0e8dc7498268107478980a37de,ghi] - Receive a new Order 1
INFO  [waiter-service,68f73d0e8dc7498268107478980a37de,jkl] - We've finished an order [1]
INFO  [customer-service,68f73d0e8dc7498268107478980a37de,mno] - Order 1 is READY
```

## Monitoring

### Actuator Endpoints

```bash
# Health check (includes custom coffee indicator)
curl http://localhost:8080/actuator/health | jq

# Custom coffee health indicator
curl http://localhost:8080/actuator/health | jq '.components.coffee'
# Expected:
# {
#   "status": "UP",
#   "details": {
#     "count": 5,
#     "message": "We have enough coffee."
#   }
# }

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus | grep order_count

# Custom order counter
curl http://localhost:8080/actuator/metrics/order.count | jq
# Expected:
# {
#   "name": "order.count",
#   "measurements": [{"statistic": "COUNT", "value": 5.0}]
# }
```

### Stream Bindings Monitoring

```bash
# View all bindings
curl http://localhost:8080/actuator/bindings | jq

# Expected output:
# {
#   "finishedOrders-in-0": {
#     "group": "waiter-service",
#     "bindingDestination": "finishedOrders"
#   },
#   "newOrders-out-0": {
#     "bindingDestination": "newOrders"
#   },
#   "notifyOrders-out-0": {
#     "bindingDestination": "notifyOrders"
#   }
# }
```

### RabbitMQ Monitoring

```bash
# Check exchanges
curl -u spring:spring http://localhost:15672/api/exchanges | \
  jq '.[] | select(.name | contains("Orders")) | {name, type}'

# Check queues
curl -u spring:spring http://localhost:15672/api/queues | \
  jq '.[] | select(.name | contains("waiter")) | {name, messages, consumers}'
```

### Consul Monitoring

```bash
# Check waiter-service registration
curl -s http://localhost:8500/v1/catalog/service/waiter-service | \
  jq '.[] | {ID: .ServiceID, Port: .ServicePort, Address: .ServiceAddress}'

# Expected output:
# {
#   "ID": "waiter-service-0",
#   "Port": 8080,
#   "Address": "172.18.0.7"  ← Docker network IP
# }

# Check health status
curl -s http://localhost:8500/v1/health/service/waiter-service | \
  jq '.[] | {ServiceID: .Service.ID, Status: .Checks[0].Status}'
# Expected: "Status": "passing"
```

## Docker Configuration

### Dockerfile

```dockerfile
FROM openjdk:21

ARG JAR_FILE

ADD target/${JAR_FILE} /final-waiter-service.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Taipei", "-jar", "/final-waiter-service.jar"]
```

### Maven Docker Plugin

```xml
<plugin>
    <groupId>com.spotify</groupId>
    <artifactId>dockerfile-maven-plugin</artifactId>
    <version>1.4.6</version>
    <dependencies>
        <!-- Apple Silicon + Intel compatibility -->
        <dependency>
            <groupId>com.github.jnr</groupId>
            <artifactId>jnr-unixsocket</artifactId>
            <version>0.38.14</version>
        </dependency>
    </dependencies>
    <configuration>
        <repository>${docker.image.prefix}/${project.artifactId}</repository>
        <tag>${project.version}</tag>
        <buildArgs>
            <JAR_FILE>${project.build.finalName}.jar</JAR_FILE>
        </buildArgs>
    </configuration>
</plugin>
```

**Build Docker Image:**

```bash
# Build project and Docker image
mvn clean package -DskipTests

# Verify image
docker images | grep final-waiter-service
# springbucks/final-waiter-service  0.0.1-SNAPSHOT  xxx  500MB
```

## Common Issues

### Issue 1: Redis Cache Not Working

**Symptom:**
```
SQL query appears on every request
```

**Solutions:**

```bash
# 1. Verify Redis connection
docker exec -it final-spring-course-redis-final-spring-course-1 redis-cli PING
# Expected: PONG

# 2. Check cache type configuration
curl http://localhost:8080/actuator/env | \
  jq '.propertySources[] | select(.name | contains("application.properties")) | .properties."spring.cache.type"'
# Expected: "redis"

# 3. Check cache keys
docker exec -it final-spring-course-redis-final-spring-course-1 redis-cli KEYS '*'
# Expected: CoffeeCache::SimpleKey []
```

### Issue 2: Rate Limiter Not Triggering

**Symptom:**
```
Can send more than 5 coffee requests in 30 seconds
```

**Solutions:**

```bash
# Check rate limiter configuration
curl http://localhost:8080/actuator/env | \
  grep -A5 "resilience4j.ratelimiter.instances.coffee"

# Check available permissions
curl http://localhost:8080/actuator/metrics/resilience4j.ratelimiter.available.permissions | jq

# Test rapidly (no sleep)
for i in {1..7}; do curl http://localhost:8080/coffee/; done
```

### Issue 3: Message Not Sent to Barista

**Symptom:**
```
Order updated to PAID but barista doesn't receive message
```

**Solutions:**

```bash
# 1. Verify StreamBridge binding configuration
# application.properties should have:
stream.bindings.new-orders-binding=newOrders-out-0

# 2. Check RabbitMQ exchange exists
curl -u spring:spring http://localhost:15672/api/exchanges/%2F/newOrders

# 3. Check barista-service is listening
curl -u spring:spring http://localhost:15672/api/queues/%2F/newOrders.barista-service | \
  jq '.consumers'
# Expected: consumers > 0

# 4. Check waiter-service logs for message sending confirmation
docker logs final-spring-course-final-waiter-service-1 | grep "newOrders-out-0"
# Expected: "Channel 'waiter-service-1.newOrders-out-0' has 1 subscriber(s)."
```

### Issue 4: Routing Key Not Working

**Symptom:**
```
All customer instances receive same notification
```

**Root Cause:** Routing key expression not configured

**Solutions:**

```bash
# ✅ Verify producer configuration (waiter-service)
spring.cloud.stream.rabbit.bindings.notifyOrders-out-0.producer.routing-key-expression=headers.customer

# ✅ Verify message has customer header (waiter-service OrderListener.java)
Message<Long> message = MessageBuilder.withPayload(id)
        .setHeader("customer", order.getCustomer())  // Must set this!
        .build();

# ✅ Verify consumer routing key binding (customer-service)
spring.cloud.stream.rabbit.bindings.notifyOrders-in-0.consumer.binding-routing-key=${customer.name}
```

## Best Practices

### 1. Cache Configuration Precedence

```java
// Priority (High → Low):
// 1. @CacheConfig(cacheNames = "CoffeeCache")  ← Code level (HIGHEST)
// 2. @Cacheable(cacheNames = "...")            ← Method level
// 3. spring.cache.cache-names=coffee           ← Config level (LOWEST)

// Recommendation: Use @CacheConfig for consistency
```

### 2. Rate Limiting Strategy

| Endpoint Type | limit-for-period | limit-refresh-period | Reasoning |
|---------------|------------------|---------------------|-----------|
| **Public API** | 10 | 60s | Prevent abuse |
| **Internal API** | 100 | 30s | Higher throughput |
| **Admin API** | 5 | 60s | Protect sensitive ops |

### 3. Zipkin Sampling

```properties
# Development: 100% sampling
management.tracing.sampling.probability=1.0

# Production: 5-10% sampling (reduce performance impact)
management.tracing.sampling.probability=0.1
```

### 4. Message Persistence

**✅ Recommended: Enable persistence**

```properties
# Producer: Persistent delivery
spring.cloud.stream.rabbit.bindings.newOrders-out-0.producer.delivery-mode=persistent

# Consumer: Durable subscription
spring.cloud.stream.rabbit.bindings.finishedOrders-in-0.consumer.durable-subscription=true
```

### 5. Consul Config Best Practices

```properties
# Enable Consul Config for centralized configuration
spring.cloud.consul.config.enabled=true
spring.cloud.consul.config.format=yaml

# Store configs in Consul KV:
# /config/waiter-service/data  ← Service-specific config
# /config/application/data     ← Shared config
```

## Performance Optimization

### Database Query Optimization

**Issue**: Health check triggers frequent COUNT queries

**Solutions:**

```java
// Option 1: Cache count result
@Cacheable(value = "coffeeCount", unless = "#result == null")
public long getCoffeeCount() {
    return coffeeRepository.count();
}

// Option 2: Disable SQL logging
spring.jpa.properties.hibernate.show_sql=false  # Production setting
```

### Connection Pool Tuning

```properties
# HikariCP configuration (default pool)
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

## Testing

### Manual Testing

```bash
# Test coffee CRUD
curl http://localhost:8080/coffee/ | jq
curl -X POST http://localhost:8080/coffee/ \
  -H "Content-Type: application/json" \
  -d '{"name":"flat white","price":130.0}' | jq

# Test order workflow
curl -X POST http://localhost:8080/order/ \
  -H "Content-Type: application/json" \
  -d '{"customer":"test-user","items":["espresso"]}' | jq '.id'

ORDER_ID=1
curl -X PUT http://localhost:8080/order/$ORDER_ID \
  -H "Content-Type: application/json" \
  -d '{"state":"PAID"}' | jq

# Wait and check final state
sleep 2
curl http://localhost:8080/order/$ORDER_ID | jq '.state'
# Expected: "TAKEN"
```

### Automated Testing

```bash
# Run unit tests
./mvnw test

# Run with coverage
./mvnw clean test jacoco:report
```

## Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| **Rate Limiter rejects requests** | Wait 30s for quota reset or increase `limit-for-period` |
| **Redis connection fails** | Check Redis is running: `docker ps \| grep redis` |
| **Database connection fails** | Verify MariaDB is running and credentials are correct |
| **RabbitMQ message not received** | Check exchange bindings in RabbitMQ UI (port 15672) |
| **Zipkin spans not visible** | Verify Zipkin endpoint: `http://localhost:9411` |

### Rate Limiter 限流檢查

**查詢 Rate Limiter 狀態**：
```bash
# 檢查 order endpoint 限流器
curl http://localhost:8080/actuator/ratelimiters | jq '.rateLimiters.order'

# 預期輸出：
{
  "availablePermissions": 3,      # ← 可用配額
  "numberOfWaitingThreads": 0
}

# 檢查 coffee endpoint 限流器
curl http://localhost:8080/actuator/ratelimiters | jq '.rateLimiters.coffee'
```

**Rate Limiter 配置**：
```properties
# order endpoint: 每30秒最多3個請求
resilience4j.ratelimiter.instances.order.limit-for-period=3
resilience4j.ratelimiter.instances.order.limit-refresh-period=30000

# coffee endpoint: 每30秒最多5個請求
resilience4j.ratelimiter.instances.coffee.limit-for-period=5
resilience4j.ratelimiter.instances.coffee.limit-refresh-period=30000
```

**觸發限流時的錯誤**：
```log
io.github.resilience4j.ratelimiter.RequestNotPermitted: 
RateLimiter 'order' does not permit further calls
```

**解決方案**：
- 等待30秒讓配額重置
- 或增加 `limit-for-period` 值（開發環境）

### Redis 快取檢查

```bash
# 連接 Redis
docker exec -it final-spring-course-redis-final-spring-course-1 redis-cli

# 查看快取的咖啡資料
127.0.0.1:6379> KEYS CoffeeCache::*
1) "CoffeeCache::SimpleKey []"

# 查看TTL
127.0.0.1:6379> TTL "CoffeeCache::SimpleKey []"
(integer) 59  # 剩餘秒數

# 刪除快取（強制重新載入）
127.0.0.1:6379> DEL "CoffeeCache::SimpleKey []"
```

### 日誌分析

```bash
# 查看完整日誌
docker logs -f final-spring-course-final-waiter-service-1

# 過濾訂單處理
docker logs final-spring-course-final-waiter-service-1 | grep "Order"

# 過濾限流事件
docker logs final-spring-course-final-waiter-service-1 | grep "RateLimiter"

# 過濾 SQL 查詢（檢查快取是否生效）
docker logs final-spring-course-final-waiter-service-1 | grep "Hibernate:"
```

## References

- [Micrometer Tracing Documentation](https://micrometer.io/docs/tracing)
- [Spring Boot 3.x Observability](https://docs.spring.io/spring-boot/reference/actuator/tracing.html)
- [Zipkin Official Docs](https://zipkin.io/)
- [Spring Cloud Consul](https://docs.spring.io/spring-cloud-consul/docs/current/reference/html/)
- [Resilience4j Rate Limiter](https://resilience4j.readme.io/docs/ratelimiter)
- [Spring Cloud Stream](https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/)
- [Spring Cache Abstraction](https://docs.spring.io/spring-framework/reference/integration/cache.html)

## License

MIT License - see [LICENSE](LICENSE) file for details.

## About Us

我們主要專注在敏捷專案管理、物聯網（IoT）應用開發和領域驅動設計（DDD）。喜歡把先進技術和實務經驗結合，打造好用又靈活的軟體解決方案。近來也積極結合 AI 技術，推動自動化工作流，讓開發與運維更有效率、更智慧。持續學習與分享，希望能一起推動軟體開發的創新和進步。

## Contact

**風清雲談** - 專注於敏捷專案管理、物聯網（IoT）應用開發和領域驅動設計（DDD）。

- 🌐 官方網站：[風清雲談部落格](https://blog.fengqing.tw/)
- 📘 Facebook：[風清雲談粉絲頁](https://www.facebook.com/profile.php?id=61576838896062)
- 💼 LinkedIn：[Chu Kuo-Lung](https://www.linkedin.com/in/chu-kuo-lung)
- 📺 YouTube：[雲談風清頻道](https://www.youtube.com/channel/UCXDqLTdCMiCJ1j8xGRfwEig)
- 📧 Email：[fengqing.tw@gmail.com](mailto:fengqing.tw@gmail.com)

---

**⭐ If this project helps you, please give it a Star!**
