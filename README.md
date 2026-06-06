# 🚀 Centralized Distributed Rate Limiting System

A production-style centralized rate limiting platform designed for microservice architectures.

The system provides **organization-level and user-level request throttling** using the **Token Bucket Algorithm**, backed by **Redis Cluster**, **Lua Scripting**, and **Spring Cloud Gateway**.

Instead of embedding rate limiting logic inside every microservice, a dedicated Rate Limiting Service acts as a centralized control plane, ensuring consistency, scalability, and easier policy management across distributed systems.

---

# 📖 Problem Statement

In a microservice ecosystem, multiple services often need to enforce API usage limits.

Traditional approaches introduce several challenges:

* Duplicate rate limiting logic across services
* Inconsistent enforcement policies
* Difficulty updating limits dynamically
* Poor scalability under heavy traffic
* Race conditions when multiple instances update counters simultaneously

This project addresses these challenges by introducing a centralized and distributed rate limiting architecture.

---

# 🏛️ System Architecture

## High-Level Flow

```text
Client
   │
   ▼
API Gateway
   │
   ▼
Rate Limiting Service
   │
   ▼
Redis Cluster
   │
   ▼
Downstream Microservices
```

---

## Request Lifecycle

### Step 1: User Authentication

A customer organization registers on the platform.

```text
POST /signup
```

Example:

```json
{
  "organizationName":"Netflix"
}
```

After authentication:

```text
POST /login
```

A JWT token is issued containing:

* Organization ID
* Organization Name
* User Information

---

### Step 2: Organization Configures Limits

Organization administrators configure their traffic policy.

```text
POST /api/owner/create
```

Example:

```json
{
  "bucketSize":100,
  "refillRate":20
}
```

Stored Configuration:

```text
Organization A
Bucket Size = 100
Refill Rate = 20 tokens/sec
```

Each organization can have its own independent limits.

---

### Step 3: Client Request Arrives

A request reaches the business service through the API Gateway.

```text
/api/orders
```

Gateway extracts:

* JWT Token
* User ID
* Route Information

and forwards a validation request to the Rate Limiting Service.

---

### Step 4: Unique Distributed Key Generation

The Rate Limiting Service extracts the organization information from JWT.

A globally unique bucket key is generated:

```text
org:{organizationId}:user:{userId}
```

Example:

```text
org:17:user:991
```

This enables:

* Per-user limits
* Per-organization isolation
* Distributed storage across Redis Cluster

---

### Step 5: Redis Cluster Routing

The generated key is automatically hashed by Redis Cluster.

```text
Hash(key) % 16384
```

Redis maps the key into one of the cluster slots:

```text
0 - 16383 slots
```

Current cluster:

```text
redis-7000
redis-7001
redis-7002
```

Benefits:

* Automatic sharding
* Horizontal scalability
* Even load distribution

---

### Step 6: Atomic Token Bucket Execution

The Rate Limiting Service executes a Lua script inside Redis.

The script performs atomically:

1. Read bucket state
2. Calculate elapsed time
3. Refill tokens
4. Check availability
5. Consume token
6. Save updated state

Because the entire operation executes server-side:

* No race conditions
* No lost updates
* No distributed locking required

---

### Step 7: Decision

### Request Allowed

```json
{
  "message":"Request Allowed",
  "statusCode":200
}
```

Gateway forwards request to downstream service.

---

### Request Rejected

```json
{
  "message":"Rate Limit Exceeded",
  "statusCode":429
}
```

Gateway immediately returns:

```http
HTTP/1.1 429 Too Many Requests
```

without burdening downstream services.

---

# 🪣 Token Bucket Algorithm

Each user receives a bucket.

```text
Capacity = Bucket Size
```

Tokens are replenished continuously:

```text
tokens += refillRate × elapsedTime
```

Every request consumes:

```text
1 Token
```

If:

```text
tokens > 0
```

Request is allowed.

Otherwise:

```text
Request Rejected
```

---

## Why Token Bucket?

Compared to Fixed Window:

✅ Allows burst traffic

✅ Smooth traffic control

✅ Better user experience

✅ Commonly used by major API providers

Examples:

* AWS API Gateway
* Stripe
* GitHub APIs
* Cloudflare

---

# ⚡ Redis Cluster Design

## Cluster Nodes

```text
redis-7000
redis-7001
redis-7002
```

Redis Cluster divides keyspace into:

```text
16384 Hash Slots
```

Each node owns a subset of slots.

Benefits:

* Horizontal scaling
* High throughput
* Automatic key distribution
* Reduced bottlenecks

---

# 🔥 Why Lua Scripting?

Without Lua:

```text
GET bucket
Calculate refill
UPDATE bucket
```

Concurrent requests can overwrite each other.

Example:

```text
Thread A -> Reads 5 tokens
Thread B -> Reads 5 tokens

Both consume simultaneously

Result:
Incorrect bucket state
```

Lua executes the entire operation atomically inside Redis.

Benefits:

* Race-condition free
* Single network round trip
* High performance
* Strong consistency

---

# 🌐 API Gateway Integration

The API Gateway acts as the enforcement layer.

Responsibilities:

* JWT validation
* Request interception
* Communication with Limiting Service
* Blocking excessive traffic
* Forwarding approved traffic

Advantages:

* Centralized control
* No duplicated logic
* Easier maintenance
* Better observability

---

# 🏗️ Microservices

### API Gateway

Responsible for:

* Request routing
* Authentication validation
* Rate limit enforcement

---

### Limiting Service

Responsible for:

* Token bucket management
* Redis communication
* Lua execution
* Policy evaluation

---

### Auth Service

Responsible for:

* User registration
* Login
* JWT generation

---

### Analytics Service

Reserved for:

* Usage analytics
* Traffic monitoring
* Future dashboards

---

# ⚙️ Technology Stack

## Backend

* Java
* Spring Boot
* Spring Cloud Gateway

## Distributed Data Layer

* Redis Cluster
* Lua Scripting

## DevOps

* Docker
* Docker Compose

## Build Tools

* Maven
* Git
* GitHub

---

# 📈 Example Use Cases

### SaaS Platforms

Different plans:

```text
Free      → 10 req/sec
Pro       → 100 req/sec
Enterprise→ 1000 req/sec
```

---

### Authentication APIs

Prevent:

* Credential stuffing
* Brute-force attacks

---

### Public APIs

Protect:

* Backend resources
* Database connections
* Infrastructure costs

---

### Multi-Tenant Systems

Each organization gets:

* Separate bucket
* Separate quota
* Isolated traffic control

---

# 📊 Distributed Systems Concepts Demonstrated

This project showcases:

* API Gateway Pattern
* Distributed Caching
* Consistent Hashing
* Redis Cluster Sharding
* Token Bucket Rate Limiting
* Multi-Tenant Architecture
* JWT-Based Context Propagation
* Atomic Operations using Lua
* High-Concurrency Design
* Service Decoupling

---

# 🔮 Future Improvements

* Role-Based Limits (Free / Premium / Enterprise)
* Per-API Route Quotas
* Dynamic Policy Updates
* Admin Dashboard
* Prometheus Metrics
* Grafana Visualization
* Circuit Breaker Integration
* Redis Replication & Failover
* Sliding Window Log Algorithm
* Leaky Bucket Algorithm
* Distributed Tracing (OpenTelemetry)

---

# 💡 Key Learnings

Through this project I gained practical experience with:

* Designing distributed systems
* Building centralized platform services
* Redis Cluster internals
* Consistent hashing and sharding
* Atomic Lua scripting
* API Gateway architectures
* Dockerized microservice deployments
* Multi-tenant SaaS design patterns
* High-concurrency backend systems

---

# 👨‍💻 Author

Manish Kumar

GitHub:
https://github.com/manish123-ui

---

⭐ If you found this project interesting, consider giving the repository a star.
