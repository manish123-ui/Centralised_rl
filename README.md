# Centralized Rate Limiting Microservice

A distributed and centralized rate limiting system built for microservice architectures to control and regulate API traffic efficiently.

This project implements a **centralized rate limiting service** using the **Token Bucket Algorithm**, backed by **Redis Cluster + Lua scripting**, and integrates with an **API Gateway** to enforce request limits across multiple microservices.

---

---


## 🏛️ High-Level Design

```mermaid
flowchart TD
    A[Client Request] --> B[API Gateway]

    B --> C[Rate Limit Filter / Limiting Service]

    C --> D[Redis Cluster]

    D --> D1[Redis Node 7000]
    D --> D2[Redis Node 7001]
    D --> D3[Redis Node 7002]

    C --> E{Token Available?}

    E -->|Yes| F[Forward Request]
    E -->|No| G[Return HTTP 429]

    F --> H[Auth Service]
    F --> I[Analytics Service]

## 🚀 Features

- Centralized request throttling for microservices
- Token Bucket based rate limiting
- Atomic request handling using **Redis + Lua scripting**
- Supports high concurrency and low latency
- Redis sharding using **3-node Redis setup**
- API Gateway integration for centralized enforcement
- Dockerized microservices setup
- Designed for scalable distributed systems

---

## 🏗️ Architecture Overview

The system sits between clients and downstream microservices through an API Gateway.

### Flow:
1. Client sends request to **API Gateway**
2. API Gateway forwards request metadata (user/IP/API key) to **Limiting Service**
3. Limiting Service checks request quota in **Redis Cluster**
4. Lua script atomically:
   - refills tokens if needed
   - checks availability
   - consumes token if allowed
5. If allowed → request reaches downstream service
6. If limit exceeded → request is rejected with **HTTP 429 Too Many Requests**

---

## 🧠 Core Idea

This project solves a common distributed systems problem:

> **How do multiple microservices enforce rate limits consistently under high traffic?**

Instead of implementing separate local rate limiting in every service, this project centralizes the logic into a dedicated service.

This provides:

- consistency
- easier control
- scalability
- better observability
- cleaner microservice boundaries

---

## ⚙️ Tech Stack

### Backend
- Java
- Spring Boot
- Spring Cloud Gateway

### Distributed Caching / Rate Limiting
- Redis Cluster
- Lua Scripting

### Containerization
- Docker
- Docker Compose

### Build / Tools
- Maven
- IntelliJ IDEA
- Git / GitHub

---

## 📌 Microservices in this Project

- **api-gateway** → Entry point for client requests
- **Limiting-service** → Centralized rate limiting logic
- **auth-service** → Example downstream service
- **Analytics-service** → Example service for future extensibility

---

## 🪣 Rate Limiting Algorithm

This project uses the **Token Bucket Algorithm**.

### How it works:
- Each user / API key / route gets a bucket
- Bucket has a maximum token capacity
- Tokens refill over time
- Each request consumes 1 token
- If no token is available → request is blocked

### Why Token Bucket?
- Allows burst traffic up to capacity
- Better than strict fixed window for real-world APIs
- Efficient for distributed environments

---

## ⚡ Why Redis + Lua?

### Redis is used for:
- ultra-fast in-memory access
- distributed state sharing across services
- handling rate limit counters centrally

### Lua scripting is used for:
- atomic read + update
- preventing race conditions under concurrency
- ensuring correctness under high request load

This avoids issues where multiple requests update the same bucket simultaneously.

---

## 🧩 Redis Cluster Setup

This project uses a **3-node Redis setup** to distribute load.

### Redis Nodes:
- `redis-7000`
- `redis-7001`
- `redis-7002`

### Benefits:
- load distribution
- better scalability
- improved throughput under heavy traffic

---

## 🔗 API Gateway Integration

The **API Gateway** acts as the enforcement layer.

Before forwarding requests to downstream services, it checks with the centralized rate limiter.

### Benefits:
- single point of enforcement
- no duplicate rate limiting logic in every service
- easier future scaling and policy updates

---

## 🐳 Dockerized Setup

All services are containerized using Docker for easier local development and deployment.

### Includes:
- API Gateway
- Limiting Service
- Redis Cluster
- Sample downstream microservices

---

## 📂 Project Structure

```bash
Centralized_ratelimiting/
│
├── api-gateway/
├── Limiting-service/
├── auth-service/
├── Analytics-service/
├── docker-compose.yml
├── pom.xml
└── README.md
▶️ How to Run
1) Clone the repository
git clone https://github.com/manish123-ui/Centralised_rl.git
cd Centralised_rl
2) Start Redis Cluster + Services
docker-compose up --build
3) Configure Redis cluster nodes

Example Spring config:

spring:
  data:
    redis:
      cluster:
        nodes:
          - redis-7000:7000
          - redis-7001:7001
          - redis-7002:7002
4) Run API requests through gateway

Example:

curl http://localhost:<gateway-port>/auth/hello

If rate limit is exceeded:

429 Too Many Requests
📈 Example Use Cases
API abuse prevention
protecting authentication endpoints
per-user or per-IP throttling
gateway-level request control
scalable distributed API governance
🛠️ Future Improvements
Role-based rate limiting (free vs premium users)
Dynamic rule configuration
Admin dashboard for rate limit monitoring
Prometheus + Grafana integration
Circuit breaker / resilience integration
Per-endpoint and per-user custom quotas
Sliding Window / Leaky Bucket support
Distributed tracing support
💡 Key Learnings

Through this project, I explored:

distributed systems design
API Gateway architecture
Redis clustering concepts
atomic operations with Lua
designing concurrency-safe systems
Dockerized microservice communication
centralized control in scalable backend systems
👨‍💻 Author

Manish Kumar
GitHub: manish123-ui

⭐ If you found this project useful

Feel free to star the repository.


---

# 🔥 This README is already good — but here’s how to make it look even better

