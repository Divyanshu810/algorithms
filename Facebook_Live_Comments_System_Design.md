# Facebook Live Comments - System Design Revision Notes

## Key Points to Remember

### 1. **High-Level Requirements**
- **Functional Requirements:**
  - Users can post comments on live videos
  - Real-time comment display to viewers
  - Comment moderation capabilities
  - Support for reactions (likes, hearts, etc.)
  - User authentication integration

- **Non-Functional Requirements:**
  - Low latency (~100ms for comment delivery)
  - High availability (99.9%+)
  - Scale to millions of concurrent users
  - Handle 100K+ comments per second
  - Global distribution support

### 2. **Core Architecture Components**

#### **Real-Time Messaging Layer**
- **WebSocket Connections** for bidirectional communication
- **Message Queues** (Apache Kafka/Amazon Kinesis)
- **Load Balancers** with sticky sessions
- **Connection Managers** for WebSocket lifecycle

#### **Data Storage**
- **Primary Database:** Distributed SQL (MySQL/PostgreSQL clusters)
- **Cache Layer:** Redis/Memcached for hot comments
- **Message Queues:** Kafka for real-time streams
- **CDN:** For static content and geographic distribution

#### **Microservices**
- **Comment Service:** CRUD operations
- **Real-time Service:** WebSocket management
- **Moderation Service:** Content filtering
- **Notification Service:** User alerts
- **Analytics Service:** Metrics collection

### 3. **System Design Diagrams**

```
┌─────────────┐    ┌──────────────┐    ┌─────────────┐
│   Mobile    │    │   Web App    │    │   Desktop   │
│   Client    │    │   Client     │    │   Client    │
└─────┬───────┘    └──────┬───────┘    └─────┬───────┘
      │                   │                  │
      └───────────────────┼──────────────────┘
                          │
                    ┌─────▼─────┐
                    │    CDN    │
                    │  (Global) │
                    └─────┬─────┘
                          │
              ┌───────────▼───────────┐
              │   Load Balancer       │
              │  (Sticky Sessions)    │
              └───────────┬───────────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
  ┌─────▼─────┐    ┌─────▼─────┐    ┌─────▼─────┐
  │WebSocket  │    │WebSocket  │    │WebSocket  │
  │Server 1   │    │Server 2   │    │Server N   │
  └─────┬─────┘    └─────┬─────┘    └─────┬─────┘
        │                │                │
        └─────────────────┼─────────────────┘
                          │
                    ┌─────▼─────┐
                    │  Message  │
                    │   Queue   │
                    │  (Kafka)  │
                    └─────┬─────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
  ┌─────▼─────┐    ┌─────▼─────┐    ┌─────▼─────┐
  │ Comment   │    │Real-time  │    │Moderation │
  │ Service   │    │ Service   │    │ Service   │
  └─────┬─────┘    └─────┬─────┘    └─────┬─────┘
        │                │                │
        └─────────────────┼─────────────────┘
                          │
                    ┌─────▼─────┐
                    │   Cache   │
                    │  (Redis)  │
                    └─────┬─────┘
                          │
                    ┌─────▼─────┐
                    │ Database  │
                    │ (Sharded) │
                    └───────────┘
```

### 4. **Database Schema Design**

```sql
-- Comments Table
CREATE TABLE comments (
    comment_id BIGINT PRIMARY KEY,
    live_video_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT NOW(),
    is_deleted BOOLEAN DEFAULT FALSE,
    parent_comment_id BIGINT NULL,
    INDEX idx_video_timestamp (live_video_id, timestamp),
    INDEX idx_user_id (user_id)
);

-- Live Videos Table
CREATE TABLE live_videos (
    video_id BIGINT PRIMARY KEY,
    broadcaster_id BIGINT NOT NULL,
    title VARCHAR(255),
    start_time TIMESTAMP,
    end_time TIMESTAMP NULL,
    status ENUM('live', 'ended') DEFAULT 'live'
);

-- Users Table
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    display_name VARCHAR(100),
    is_verified BOOLEAN DEFAULT FALSE
);
```

### 5. **Critical Design Decisions**

#### **WebSocket vs Server-Sent Events (SSE)**
- **WebSocket:** Chosen for bidirectional real-time communication
- **Fallback:** Long polling for older browsers
- **Connection Pooling:** Manage connection limits per server

#### **Message Ordering & Delivery**
- **At-least-once delivery** with idempotency keys
- **Timestamp-based ordering** on client side
- **Sequence numbers** for guaranteed ordering

#### **Scaling Strategies**
- **Horizontal scaling** of WebSocket servers
- **Database sharding** by video_id
- **Read replicas** for comment retrieval
- **Cache warming** for popular live streams

### 6. **Performance Optimizations**

#### **Caching Strategy**
- **L1 Cache:** Application-level (in-memory)
- **L2 Cache:** Redis cluster (hot comments)
- **L3 Cache:** CDN (static content)
- **Cache TTL:** 5-10 minutes for comment streams

#### **Rate Limiting**
- **Per-user limits:** 10 comments/minute
- **Per-IP limits:** 100 comments/minute
- **Burst handling:** Token bucket algorithm

#### **Connection Management**
- **Connection pooling:** Max 10K connections per server
- **Heartbeat mechanism:** 30-second intervals
- **Graceful degradation:** Fallback to polling

### 7. **Monitoring & Observability**

#### **Key Metrics**
- **Latency:** P95 < 100ms for comment delivery
- **Throughput:** Comments per second
- **Connection count:** Active WebSocket connections
- **Error rates:** Failed message deliveries

#### **Alerting**
- **High latency** alerts (>200ms P95)
- **Connection drops** monitoring
- **Database performance** metrics
- **Cache hit rate** monitoring

### 8. **Security Considerations**

#### **Content Moderation**
- **Real-time filtering:** Profanity/spam detection
- **Machine Learning:** Automated content classification
- **Human moderation:** Escalation workflows
- **Shadow banning:** Invisible comment filtering

#### **DDoS Protection**
- **Rate limiting** at multiple layers
- **IP-based blocking** for suspicious traffic
- **CAPTCHA integration** for suspected bots
- **Geographic filtering** if needed

### 9. **Disaster Recovery**

#### **Multi-Region Setup**
- **Active-passive** configuration
- **Data replication** across regions
- **Failover automation** within 30 seconds
- **Backup strategies** for comment data

### 10. **Capacity Planning**

#### **Traffic Patterns**
- **Peak hours:** 2-3x normal traffic
- **Viral events:** 10-50x traffic spikes
- **Geographic distribution:** Follow user timezones
- **Seasonal patterns:** Holidays, major events

#### **Scaling Triggers**
- **CPU utilization** > 70%
- **Memory usage** > 80%
- **Connection count** > 8K per server
- **Queue depth** > 1000 messages

## Quick Revision Checklist

- [ ] Understand WebSocket connection management
- [ ] Remember database sharding strategy
- [ ] Know caching layers and TTLs
- [ ] Recall rate limiting mechanisms
- [ ] Understand message ordering guarantees
- [ ] Remember monitoring metrics
- [ ] Know disaster recovery procedures
- [ ] Understand scaling triggers and thresholds