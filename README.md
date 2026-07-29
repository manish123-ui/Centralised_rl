# 🚀 Centralized Distributed Rate Limiting System

A production-style **Centralized Rate Limiting Platform** built for microservice architectures.

The system uses a dedicated **Rate Limiting Service**, **Redis Cluster**, **Lua Scripting**, and **Spring Cloud Gateway** to provide scalable and consistent API throttling across distributed services.

Instead of implementing rate limiting logic inside every microservice, all traffic policies are enforced centrally through a dedicated service, ensuring consistency, scalability, and easier maintenance.

---

## 📖 Problem Statement

In large-scale microservice systems, multiple services often need to enforce request limits.

Traditional approaches suffer from:

* Duplicate rate limiting logic
* Inconsistent enforcement
* Difficult configuration management
* Race conditions under high concurrency
* Poor scalability

This project solves these challenges through a centralized and distributed architecture.

---

## 🏛️ High-Level Design <img width="2083" height="1298" alt="image" src="https://github.com/user-attachments/assets/b56ee6eb-93a9-44fe-a9df-1613e09ebb55" />

## 🚀 Features

* Centralized Rate Limiting Service
* Token Bucket Algorithm
* Redis Cluster Based Storage
* Lua Script Atomic Operations
* JWT Based Organization Identification
* Multi-Tenant Architecture
* Per User Rate Limiting
* Per Organization Configuration
* Spring Cloud Gateway Integration
* Dockerized Deployment
* Distributed Key Sharding
* High Concurrency Support

---

# 🧠 System Architecture

## Services

### API Gateway

Acts as the single entry point.

Responsibilities:

* Request Routing
* JWT Validation
* Calling Rate Limiting Service
* Blocking Excess Requests
* Forwarding Approved Requests

---

### Authentication Service

Responsible for:

* User Registration
* Login
* JWT Generation
* JWT Validation
* Refresh Token Generation

---

### Limiting Service

Responsible for:

* Token Bucket Management
* Redis Cluster Communication
* Lua Script Execution
* Rate Limit Decision Making

---

### Analytics Service

Future service for:

* Monitoring
* Metrics Collection
* Usage Reporting

---

# 🔄 Complete Workflow

## Step 1 — User Registration

An organization registers on the platform.

```http
POST /signup
```

Example:

```json
{
  "name":"Netflix",
  "email":"admin@netflix.com",
  "password":"******"
}
```

The organization information is stored in the Auth Service.

---

## Step 2 — Login

```http
POST /genrate_token
```

A JWT token is generated.

The JWT contains:

* Organization Id
* Organization Name
* User Information

This token is used for future requests.

---

## Step 3 — Configure Rate Limit Policy

Organization owner configures limits.

```http
POST /api/owner
```

Example:

```json
{
  "bucketSize":100,
  "refillRate":20
}
```

Meaning:

* Maximum Capacity = 100 Tokens
* Refill Speed = 20 Tokens/Second

Each organization can define its own traffic policy.

---

## Step 4 — Request Hits Gateway

```text
Client
   ↓
API Gateway
```

Gateway extracts:

* JWT Token
* User Id
* Request Information

Before forwarding the request, Gateway checks with Limiting Service.

---

## Step 5 — Generate Distributed Bucket Key

Limiting Service extracts organization information from JWT.

Bucket key format:

```text
org:{organizationId}:user:{userId}
```

Example:

```text
org:15:user:987
```

Benefits:

* Tenant Isolation
* User Level Control
* Distributed Storage

---

## Step 6 — Redis Cluster Routing

The generated key is hashed automatically.

Redis Cluster contains:

```text
redis-7000
redis-7001
redis-7002
```

Redis divides keyspace into:

```text
16384 Slots
```

Keys are automatically distributed among nodes.

Benefits:

* Horizontal Scaling
* Better Throughput
* Load Distribution

---

## Step 7 — Atomic Token Bucket Execution

The Limiting Service executes a Lua Script.

The script performs atomically:

1. Read bucket state
2. Calculate elapsed time
3. Refill tokens
4. Check token availability
5. Consume token
6. Save state

Because everything runs inside Redis:

* No Race Conditions
* No Lost Updates
* Single Network Roundtrip

---

## Step 8 — Decision

### Allowed

```json
{
  "message":"Request Allowed",
  "statusCode":200
}
```

Gateway forwards request.

---

### Rejected

```json
{
  "message":"Rate Limit Exceeded",
  "statusCode":429
}
```

Gateway returns:

```http
HTTP/1.1 429 Too Many Requests
```

---

# 🪣 Token Bucket Algorithm

Each user receives a bucket.

```text
Bucket Capacity = Maximum Tokens
```

Tokens are continuously refilled.

Every request consumes one token.

If token exists:

✅ Request Allowed

Otherwise:

❌ Request Rejected

---

## Why Token Bucket?

Compared to Fixed Window:

* Allows burst traffic
* Better user experience
* Smooth rate limiting
* Industry standard solution

Used by:

* AWS
* Stripe
* GitHub
* Cloudflare

---

# ⚡ Redis + Lua Design

## Why Redis?

Redis provides:

* In-Memory Performance
* Distributed State Sharing
* Low Latency Access
* Horizontal Scalability

---

## Why Lua?

Without Lua:

```text
GET
CALCULATE
UPDATE
```

can create race conditions.

Lua executes everything atomically inside Redis.

Benefits:

* Atomic Execution
* Concurrency Safety
* Better Performance
* Consistent Results

---

# 🔑 Multi-Tenant Design

Every organization receives independent limits.

Example:

```text
Netflix
Bucket Size = 1000

Spotify
Bucket Size = 500

Amazon
Bucket Size = 2000
```

Traffic from one organization never affects another.

This architecture is suitable for:

* SaaS Platforms
* API Gateways
* Cloud Services
* Multi-Tenant Applications

---

# 📸 API Documentation

## Authentication Service Swagger


<img width="1897" height="907" alt="Screenshot 2026-06-08 153043" src="https://github.com/user-attachments/assets/17ca4d49-4783-4370-9ee9-d3b609015b92" />



### Available APIs

| Method | Endpoint       | Description          |
| ------ | -------------- | -------------------- |
| POST   | /signup        | Register User        |
| POST   | /genrate_token | Generate JWT         |
| GET    | /validate      | Validate JWT         |
| GET    | /refresh_token | Refresh Access Token |

---

## Limiting Service Swagger

<img width="1911" height="892" alt="Screenshot 2026-06-08 153708" src="https://github.com/user-attachments/assets/42645aa5-729a-4393-bfee-3df04a8df89d" />


### Available APIs

| Method | Endpoint             | Description                       |
| ------ | -------------------- | --------------------------------- |
| POST   | /api/owner           | Create Organization Configuration |
| PUT    | /api/owner/modify    | Update Rate Limit Policy          |
| GET    | /api/owner/{ownerId} | Fetch Configuration               |
| GET    | /api/limit/{user_id} | Execute Rate Limit Check          |

---

# 📸 Running Services

## Docker Containers



---

## Redis Cluster



---

## Gateway Logs



---



---

## Rate Limit Exceeded



---

# 🏗️ Project Structure

```bash
Centralized_ratelimiting
│
├── api-gateway
├── auth-service
├── limiting-service
├── analytics-service
│
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

# ⚙️ Tech Stack

## Backend

* Java
* Spring Boot
* Spring Security
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

# ▶️ Getting Started

## Clone Repository

```bash
git clone https://github.com/manish123-ui/Centralised_rl.git

cd Centralised_rl
```

---

## Start Services

```bash
docker-compose up --build
```

---

## Configure Redis Cluster

```yaml
spring:
  data:
    redis:
      cluster:
        nodes:
          - redis-7000:7000
          - redis-7001:7001
          - redis-7002:7002
```

---

## Access Swagger

Auth Service:

```text
http://localhost:4004/swagger-ui/index.html
```

Limiting Service:

```text
http://localhost:4001/swagger-ui/index.html
```

Gateway Swagger (if configured):

```text
http://localhost:4005/swagger-ui/index.html
```

---

# 📈 Real World Use Cases

* API Abuse Prevention
* Authentication Endpoint Protection
* SaaS Subscription Plans
* Public API Monetization
* Gateway Level Traffic Control
* Cloud Service Quotas
* Multi-Tenant Platforms

---

# 💡 Distributed Systems Concepts Demonstrated

* API Gateway Pattern
* Centralized Control Plane
* Redis Cluster Sharding
* Consistent Hashing
* Token Bucket Algorithm
* Multi-Tenant Architecture
* JWT Context Propagation
* Atomic Operations
* Distributed State Management
* High Concurrency Design

---

# 🔮 Future Improvements

* Free vs Premium Plans
* Per Route Rate Limits
* Dynamic Rule Updates
* Prometheus Integration
* Grafana Dashboard
* OpenTelemetry Tracing
* Redis Replication
* Sliding Window Algorithm
* Leaky Bucket Algorithm
* Kubernetes Deployment

---

# 👨‍💻 Author

**Manish Kumar**

GitHub:
https://github.com/manish123-ui

---

⭐ If you found this project useful, consider giving the repository a star.
