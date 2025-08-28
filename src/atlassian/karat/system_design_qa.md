# System Design Questions & Answers

## 1. Music Streaming with Consistent Hashing

### Question
Multiple servers uploading and streaming music with load distributed using consistent hashing based on the number of files on each server equally. Any concerns? How would you improve this system?

### Concerns
- **File count ≠ actual load**: Small vs large files, popularity differences create uneven resource usage
- **Hot spots**: Popular files may concentrate on fewer servers, creating bottlenecks
- **Rebalancing complexity**: Adding/removing servers requires significant reshuffling
- **Server capacity ignored**: Treats all servers as equal regardless of actual capacity
- **No real-time adaptation**: Static distribution doesn't account for changing usage patterns

### Improvements
- **Weighted consistent hashing**: Factor in server capacity and current load metrics
- **Request-based load balancing**: Use actual bandwidth/CPU usage rather than file counts
- **CDN integration**: Cache popular content closer to users
- **Virtual nodes**: Improve hash ring distribution granularity
- **Dynamic monitoring**: Real-time load monitoring with automatic rebalancing
- **Content popularity analytics**: Predictive caching based on usage patterns

---

## 2. URL Parsing ML System Estimation

### Question
What are the estimation requirements for a system which accepts web URLs from users, parses them and derives information using machine learning models over the next 6 months?

### Required Information

#### Volume Metrics
- Expected URL submissions (daily average and peak)
- URL processing time requirements (real-time vs batch)
- Concurrent user estimates
- Geographic distribution of requests

#### ML Model Specifications
- Model types and computational complexity
- Training vs inference workload ratio
- Model update frequency and retraining cycles
- Input/output data sizes
- Accuracy and latency requirements

#### Infrastructure Requirements
- Data storage needs (URLs, parsed content, model outputs)
- Network bandwidth for URL fetching
- Database capacity for storing results
- Backup and disaster recovery requirements

#### Operational Considerations
- Development team size and timeline
- Compliance requirements (data retention, privacy)
- Monitoring and logging infrastructure
- Cost constraints and budget planning

---

## 3. Large File Handling

### Question
How to handle a large file that cannot fit on a single machine?

### Solutions

#### File Chunking
- Split file into manageable segments (e.g., 64MB-1GB chunks)
- Process chunks in parallel across multiple machines
- Implement checksums for data integrity verification
- Use content-aware splitting when possible (record boundaries, frame boundaries)

#### Distributed Storage Systems
- **HDFS**: Hadoop Distributed File System for big data workloads
- **Object Storage**: Amazon S3, Google Cloud Storage with multipart uploads
- **Distributed File Systems**: GlusterFS, Ceph for scalable storage

#### Processing Strategies
- **Streaming processing**: Process data as it flows, don't load entirely into memory
- **Map-Reduce paradigm**: Distribute computation across cluster nodes
- **Pipeline processing**: Chain operations to minimize memory usage
- **Lazy loading**: Load only required portions on-demand

#### Optimization Techniques
- **Data compression**: Reduce effective file size before processing
- **Tiered storage**: Keep active data in fast storage, archive rest
- **Memory mapping**: Use virtual memory for large file access
- **Parallel I/O**: Utilize multiple disk channels and network connections

---

## 4. International Expansion

### Question
What changes would you make when your app is going from single country to multiple countries internationally?

### Infrastructure Changes

#### Content Delivery
- **CDN deployment**: Regional content delivery networks for reduced latency
- **Edge computing**: Process data closer to users
- **Regional data centers**: Distribute infrastructure globally
- **Load balancing**: Geographic routing based on user location

#### Data Management
- **Data residency**: Comply with local data protection laws (GDPR, CCPA, etc.)
- **Database strategy**: Regional replicas vs global consistency trade-offs
- **Backup strategies**: Multi-region disaster recovery
- **Data synchronization**: Manage consistency across regions

#### Regulatory Compliance
- **Privacy laws**: Different data protection requirements per region
- **Content regulations**: Local content restrictions and requirements
- **Business compliance**: Tax, licensing, and operational requirements
- **Security standards**: Region-specific security certifications

#### Localization
- **Multi-currency support**: Payment processing in local currencies
- **Language localization**: UI/UX translation and cultural adaptation
- **Time zone handling**: Proper scheduling and display of time-sensitive data
- **Cultural customization**: Region-specific features and content

#### Operational Considerations
- **Monitoring**: Multi-region observability and alerting systems
- **Support**: Local customer support and business hours
- **Performance optimization**: Region-specific SLA requirements
- **Cost optimization**: Regional pricing and resource optimization

---

## 5. Pre-loading vs Server Loading (Puzzle Game)

### Question
What are advantages and disadvantages of pre-loading hints vs loading from the server for a puzzle game?

### Pre-loading Approach

#### ✅ Advantages
- **Instant responsiveness**: No loading delays between levels
- **Offline capability**: Game works without internet connection
- **Reduced server load**: Lower bandwidth and compute costs over time
- **Predictable performance**: Consistent user experience regardless of network
- **Better user engagement**: Seamless gameplay transitions

#### ❌ Disadvantages
- **Large initial download**: Higher app store size and installation time
- **Storage constraints**: Limited device storage, especially on mobile
- **Update challenges**: Difficult to modify content without app updates
- **Wasted resources**: Users may never access all pre-loaded content
- **Version management**: Complex handling of content versioning

### Server Loading Approach

#### ✅ Advantages
- **Smaller app size**: Faster downloads and installations
- **Dynamic content**: Easy updates and A/B testing
- **Storage efficiency**: No local storage constraints
- **Fresh content**: Always deliver latest puzzles and hints
- **Analytics**: Better tracking of content usage patterns

#### ❌ Disadvantages
- **Network dependency**: Requires stable internet connection
- **Loading delays**: Potential wait times between levels
- **Higher server costs**: Ongoing bandwidth and infrastructure expenses
- **Poor offline experience**: Unusable without connectivity
- **Inconsistent performance**: Varies with network quality

### Hybrid Recommendation
- Pre-load essential game mechanics and first few levels
- Dynamically load additional content based on user progression
- Implement intelligent caching with content expiration
- Provide offline mode with pre-loaded backup content

---

## 6. Sports News Bias Detection System

### Question
You are tasked with building a sports news classification service that downloads articles and applies machine learning to detect bias. What information would you require to estimate the resources needed?

### Volume and Scale Estimates
- **Article volume**: Daily/hourly article ingestion rates
- **Source diversity**: Number of news sources and their publishing frequencies
- **Peak loads**: Traffic spikes during major sporting events
- **Geographic scope**: Regional vs global news coverage
- **Language requirements**: Multilingual support needs

### Content Characteristics
- **Article length**: Average word count and content complexity
- **Media types**: Text-only vs multimedia content processing
- **Update frequency**: Real-time vs batch processing requirements
- **Historical data**: Training dataset size and retention needs
- **Content categories**: Sports types and bias classification granularity

### ML Model Requirements
- **Model complexity**: Deep learning vs traditional ML approaches
- **Training frequency**: Model retraining schedules and triggers
- **Inference latency**: Real-time vs batch processing needs
- **Accuracy targets**: Precision/recall requirements for bias detection
- **Model versioning**: A/B testing and rollback capabilities

### Infrastructure Specifications
- **Compute resources**: CPU/GPU requirements for training and inference
- **Storage needs**: Raw articles, processed data, and model storage
- **Database requirements**: Metadata storage and query patterns
- **Network bandwidth**: Article downloading and API serving capacity
- **Caching strategy**: Hot data access patterns

### Operational Requirements
- **Team size**: ML engineers, data scientists, DevOps personnel
- **Development timeline**: MVP vs full-featured system delivery
- **Monitoring needs**: Model performance and system health tracking
- **Compliance requirements**: Content moderation and audit trails
- **Integration complexity**: External APIs and downstream systems
- **Cost constraints**: Budget limitations and cost optimization priorities

---

## 7. Recipe App Performance Issues

### Question
A recipe web app is seeing massive growth due to adoption by restaurants, and performance is degrading. What are some possible reasons for the slowdown and improvements?

### Possible Performance Issues

#### Database Problems
- **Query inefficiency**: Complex joins becoming slower with larger datasets
- **Missing indexes**: Unoptimized database queries
- **Lock contention**: High write volume causing read delays
- **Connection pooling**: Insufficient database connections for concurrent users

#### Application Layer Issues
- **N+1 query problems**: Restaurant features triggering multiple database calls
- **Memory leaks**: Application memory usage growing over time
- **Inefficient algorithms**: Code not optimized for scale
- **Session management**: Poor handling of user sessions and state

#### Infrastructure Bottlenecks
- **Single points of failure**: Database or application server limitations
- **Network latency**: Slow external API calls or database connections
- **Resource constraints**: CPU, memory, or disk I/O limitations
- **Load balancing**: Uneven traffic distribution

### Performance Improvements

#### Caching Strategies
- **Application caching**: Redis/Memcached for frequently accessed data
- **Database query caching**: Cache expensive query results
- **CDN implementation**: Static asset delivery optimization
- **Page caching**: Full page caching for popular recipes

#### Database Optimization
- **Read replicas**: Distribute read load across multiple database instances
- **Query optimization**: Analyze and improve slow queries
- **Database indexing**: Proper indexing strategy for search and filtering
- **Database sharding**: Horizontal partitioning for massive datasets

#### Application Architecture
- **Microservices**: Break monolith into scalable services
- **Async processing**: Move heavy operations to background queues
- **Connection pooling**: Optimize database connection management
- **Code profiling**: Identify and fix performance bottlenecks

#### Infrastructure Scaling
- **Horizontal scaling**: Add more application servers
- **Load balancing**: Distribute traffic effectively
- **Auto-scaling**: Dynamic resource allocation based on demand
- **Performance monitoring**: Real-time metrics and alerting

---

## 8. Animation Server Farm Optimization

### Question
A mobile app with processor-intensive character animation uses a server farm for processing. After processing, the server sends animated results back to the app. What are some ways to minimize server farm costs?

### Cost Optimization Strategies

#### Infrastructure Optimization
- **Spot instances**: Use cheaper preemptible computing for non-urgent animations
- **Auto-scaling**: Automatically scale down during low-usage periods
- **Right-sizing**: Match instance types to specific workload requirements
- **Reserved instances**: Long-term commitments for predictable workloads
- **Regional optimization**: Deploy in cost-effective geographic regions

#### Processing Efficiency
- **Request batching**: Group similar animation requests for efficient processing
- **Caching strategies**: Cache common animation sequences and assets
- **Pre-computation**: Generate popular animations during off-peak hours
- **Algorithm optimization**: Improve animation algorithms for faster processing
- **Parallel processing**: Utilize multi-core processors effectively

#### Resource Management
- **Queue management**: Intelligent prioritization and load balancing
- **Resource pooling**: Share resources across different animation types
- **Compression**: Optimize animation file formats and transmission
- **Progressive loading**: Send low-quality previews while processing full quality
- **Content delivery**: Use CDN for serving completed animations

#### Demand Management
- **Usage analytics**: Understand peak usage patterns for better planning
- **User education**: Guide users toward less resource-intensive options
- **Tiered service**: Offer different quality levels at different costs
- **Scheduling**: Encourage off-peak usage through incentives

---

## 9. Centralized to Edge Computing (Smart Freezer)

### Question
You're moving from a centralized, networked smart freezer to an autonomous, embedded system. What are important considerations for porting the functionality?

### Hardware Constraints

#### Processing Limitations
- **Limited CPU**: Optimize algorithms for embedded processors
- **Memory constraints**: Efficient use of RAM and storage
- **Power consumption**: Battery life and energy efficiency considerations
- **Real-time requirements**: Local decision-making without network delays

#### Storage Optimization
- **Local data storage**: Efficient use of limited flash storage
- **Data compression**: Minimize storage footprint
- **Data retention**: Smart purging of old data
- **Firmware updates**: Over-the-air update capabilities

### Software Architecture

#### Offline Capability
- **Autonomous operation**: System must function without network connectivity
- **Local intelligence**: Edge AI for temperature monitoring and anomaly detection
- **Fallback mechanisms**: Backup systems when primary functions fail
- **Data buffering**: Store critical data until connectivity is restored

#### Connectivity Management
- **Intermittent connectivity**: Handle network outages gracefully
- **Data synchronization**: Efficient sync when connectivity is available
- **Bandwidth optimization**: Minimize data transmission
- **Protocol efficiency**: Use lightweight communication protocols

### Security and Reliability

#### Security Considerations
- **Local security**: Robust security without cloud dependency
- **Secure boot**: Prevent unauthorized firmware modifications
- **Data encryption**: Protect sensitive data stored locally
- **Access control**: Local authentication and authorization

#### Reliability Requirements
- **Fault tolerance**: Continue operation despite component failures
- **Watchdog systems**: Automatic recovery from software failures
- **Redundancy**: Critical system backups
- **Diagnostics**: Local troubleshooting capabilities

### Development Challenges
- **Testing complexity**: Simulate various edge conditions
- **Debugging difficulties**: Limited debugging tools on embedded systems
- **Update mechanisms**: Safe and reliable firmware updates
- **Monitoring**: Remote monitoring with limited connectivity

---

## 10. Resource Cost Prediction for Scaling

### Question
The system has been extremely popular in its first year, so we need to scale for the coming year. What information would you require to predict resource costs for the next year?

### Historical Usage Analysis

#### Current Usage Patterns
- **Peak and average loads**: CPU, memory, storage, and bandwidth utilization
- **Growth trajectory**: Monthly/quarterly user acquisition and engagement trends
- **Seasonal variations**: Holiday spikes, business cycles, and usage patterns
- **Geographic distribution**: Regional usage patterns and expansion plans

#### Performance Metrics
- **Current bottlenecks**: Identify existing performance limitations
- **SLA compliance**: Current service level achievement rates
- **Error rates**: System reliability and failure patterns
- **Response times**: Latency trends under different load conditions

### Future Requirements

#### Business Growth Projections
- **User growth**: Expected user acquisition rates and retention
- **Feature roadmap**: New functionality impact on resource requirements
- **Market expansion**: Geographic expansion and localization needs
- **Business model changes**: Monetization impact on usage patterns

#### Technical Evolution
- **Architecture improvements**: Planned efficiency optimizations
- **Technology upgrades**: Migration to more efficient systems
- **Security enhancements**: Additional security requirements
- **Compliance needs**: Regulatory requirements affecting infrastructure

### Resource Planning

#### Infrastructure Scaling
- **Compute requirements**: CPU, GPU, and memory scaling needs
- **Storage growth**: Data storage and backup requirements
- **Network capacity**: Bandwidth and CDN requirements
- **Database scaling**: Read/write capacity and replication needs

#### Cost Modeling
- **Pricing trends**: Cloud provider pricing changes and negotiations
- **Reserved vs on-demand**: Optimization strategies for cost efficiency
- **Multi-cloud strategy**: Vendor diversification and cost optimization
- **Budget constraints**: Financial limitations and cost targets

### Forecasting Methodology

#### Scenario Planning
- **Conservative growth**: Minimum expected growth scenario
- **Expected growth**: Most likely growth trajectory
- **Aggressive growth**: Maximum expected growth scenario
- **Black swan events**: Viral growth or major setbacks

#### Risk Management
- **Buffer planning**: Over-provisioning for unexpected spikes
- **Phased scaling**: Incremental scaling with decision points
- **Cost controls**: Automatic scaling limits and budget alerts
- **Performance monitoring**: Early warning systems for capacity issues

### Implementation Strategy
- **Milestone planning**: Quarterly resource scaling checkpoints
- **Automation**: Auto-scaling and cost optimization automation
- **Monitoring dashboards**: Real-time resource utilization tracking
- **Review processes**: Regular cost and performance reviews