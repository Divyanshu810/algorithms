# Payment Gateway System Design

## 1. System Requirements and Scale Estimation

### Functional Requirements
- Process payments (credit/debit cards, digital wallets, bank transfers)
- Handle refunds and chargebacks
- Support multiple currencies and payment methods
- Real-time transaction processing
- Transaction history and reporting
- Merchant onboarding and management
- Fraud detection and prevention
- Webhook notifications for transaction status

### Non-Functional Requirements
- **Availability**: 99.99% uptime (4.32 minutes downtime/month)
- **Latency**: <200ms for payment processing
- **Consistency**: Strong consistency for financial transactions
- **Security**: PCI DSS Level 1 compliance
- **Scalability**: Handle 10,000+ TPS at peak
- **Durability**: Zero data loss tolerance

### Scale Estimation
- **Daily Active Merchants**: 100,000
- **Daily Transactions**: 50 million
- **Peak TPS**: 10,000 transactions/second
- **Average Transaction Size**: 2KB
- **Data Storage**: 
  - Daily: 100GB
  - Annual: 36TB
  - With 3-year retention: 108TB
- **Bandwidth**: 20MB/s average, 100MB/s peak

## 2. Technology Stack

### Backend Services
- **Language**: Java 17+ with Spring Boot
- **Framework**: Spring Security, Spring Data JPA
- **API Gateway**: Kong or AWS API Gateway
- **Message Queue**: Apache Kafka for event streaming
- **Cache**: Redis for session management and rate limiting

### Databases
- **Primary**: PostgreSQL (ACID compliance for transactions)
- **Analytics**: ClickHouse for real-time analytics
- **Search**: Elasticsearch for transaction search
- **Cache**: Redis for frequently accessed data

### Infrastructure
- **Container**: Docker + Kubernetes
- **Cloud**: AWS/GCP with multi-region deployment
- **CDN**: CloudFlare for global distribution
- **Monitoring**: Prometheus + Grafana + ELK stack

### Security & Compliance
- **Encryption**: AES-256 for data at rest, TLS 1.3 for transit
- **Key Management**: AWS KMS or HashiCorp Vault
- **Tokenization**: For sensitive card data
- **HSM**: Hardware Security Modules for key operations

## 3. High-Level Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web/Mobile    │    │   Merchant      │    │   Admin         │
│   Applications  │    │   Dashboard     │    │   Portal        │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────┴─────────────┐
                    │      Load Balancer        │
                    │     (AWS ALB/NGINX)       │
                    └─────────────┬─────────────┘
                                 │
                    ┌─────────────┴─────────────┐
                    │      API Gateway          │
                    │   (Rate Limiting, Auth)   │
                    └─────────────┬─────────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                       │                        │
┌───────▼────────┐    ┌─────────▼────────┐    ┌─────────▼────────┐
│  Payment       │    │  Merchant        │    │  Notification    │
│  Service       │    │  Service         │    │  Service         │
└───────┬────────┘    └─────────┬────────┘    └─────────┬────────┘
        │                       │                        │
┌───────▼────────┐    ┌─────────▼────────┐    ┌─────────▼────────┐
│  Fraud         │    │  Reporting       │    │  Webhook         │
│  Detection     │    │  Service         │    │  Service         │
└───────┬────────┘    └─────────┬────────┘    └─────────┬────────┘
        │                       │                        │
        └───────────────────────┼────────────────────────┘
                               │
                    ┌─────────▼─────────┐
                    │   Message Queue   │
                    │     (Kafka)       │
                    └─────────┬─────────┘
                             │
    ┌────────────────────────┼────────────────────────┐
    │                       │                        │
┌───▼────┐    ┌────────▼──────────┐    ┌─────────▼─────────┐
│ Redis  │    │   PostgreSQL      │    │   ClickHouse      │
│ Cache  │    │   (Primary DB)    │    │   (Analytics)     │
└────────┘    └───────────────────┘    └───────────────────┘
```

### External Integrations
```
┌─────────────────────────────────────────────────────────┐
│                External Services                        │
├─────────────┬─────────────┬─────────────┬───────────────┤
│   Visa/     │   PayPal/   │   Bank      │   Fraud       │
│ Mastercard  │   Stripe    │   APIs      │   Services    │
│   APIs      │             │             │   (Kount)     │
└─────────────┴─────────────┴─────────────┴───────────────┘
```

## 4. Low-Level Design and Database Schemas

### Core Database Tables

```sql
-- Merchants table
CREATE TABLE merchants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    api_key VARCHAR(255) UNIQUE NOT NULL,
    secret_key VARCHAR(255) NOT NULL,
    webhook_url VARCHAR(500),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Payment methods table
CREATE TABLE payment_methods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID REFERENCES merchants(id),
    type VARCHAR(50) NOT NULL, -- CARD, BANK_TRANSFER, WALLET
    provider VARCHAR(50), -- VISA, MASTERCARD, PAYPAL
    is_active BOOLEAN DEFAULT true,
    config JSONB, -- Provider-specific configuration
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transactions table (main)
CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID REFERENCES merchants(id),
    merchant_transaction_id VARCHAR(255),
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL, -- PENDING, SUCCESS, FAILED, REFUNDED
    payment_method VARCHAR(50) NOT NULL,
    gateway_response JSONB,
    customer_info JSONB,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(merchant_id, merchant_transaction_id)
);

-- Transaction events for audit trail
CREATE TABLE transaction_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id UUID REFERENCES transactions(id),
    event_type VARCHAR(50) NOT NULL, -- CREATED, AUTHORIZED, CAPTURED, FAILED, REFUNDED
    status VARCHAR(20) NOT NULL,
    gateway_reference VARCHAR(255),
    response_code VARCHAR(10),
    response_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Refunds table
CREATE TABLE refunds (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id UUID REFERENCES transactions(id),
    amount DECIMAL(15,2) NOT NULL,
    reason VARCHAR(500),
    status VARCHAR(20) NOT NULL, -- PENDING, SUCCESS, FAILED
    gateway_reference VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Fraud scores and rules
CREATE TABLE fraud_checks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id UUID REFERENCES transactions(id),
    risk_score INTEGER NOT NULL, -- 0-100
    rules_triggered JSONB,
    action VARCHAR(20) NOT NULL, -- ALLOW, REVIEW, BLOCK
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Webhooks delivery tracking
CREATE TABLE webhook_deliveries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID REFERENCES merchants(id),
    transaction_id UUID REFERENCES transactions(id),
    event_type VARCHAR(50) NOT NULL,
    url VARCHAR(500) NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(20) NOT NULL, -- PENDING, SUCCESS, FAILED
    attempts INTEGER DEFAULT 0,
    last_attempt_at TIMESTAMP,
    next_attempt_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Indexes for Performance
```sql
-- Transaction queries
CREATE INDEX idx_transactions_merchant_id ON transactions(merchant_id);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_transactions_merchant_status ON transactions(merchant_id, status);

-- Fraud detection
CREATE INDEX idx_fraud_checks_transaction_id ON fraud_checks(transaction_id);
CREATE INDEX idx_fraud_checks_risk_score ON fraud_checks(risk_score);

-- Webhook deliveries
CREATE INDEX idx_webhook_deliveries_status ON webhook_deliveries(status);
CREATE INDEX idx_webhook_deliveries_next_attempt ON webhook_deliveries(next_attempt_at) 
WHERE status = 'PENDING';
```

## 5. Java Implementation - Core Operations

### Payment Service Implementation

```java
@Service
@Transactional
public class PaymentService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private FraudDetectionService fraudDetectionService;
    
    @Autowired
    private PaymentGatewayService gatewayService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            // 1. Validate request
            validatePaymentRequest(request);
            
            // 2. Create transaction record
            Transaction transaction = createTransaction(request);
            
            // 3. Fraud detection
            FraudResult fraudResult = fraudDetectionService.checkFraud(request, transaction);
            
            if (fraudResult.getAction() == FraudAction.BLOCK) {
                transaction.setStatus(TransactionStatus.FRAUD_REJECTED);
                transactionRepository.save(transaction);
                return PaymentResponse.rejected("Transaction blocked by fraud detection");
            }
            
            // 4. Process with payment gateway
            GatewayResponse gatewayResponse = gatewayService.processPayment(request);
            
            // 5. Update transaction status
            transaction.setStatus(gatewayResponse.isSuccess() ? 
                TransactionStatus.SUCCESS : TransactionStatus.FAILED);
            transaction.setGatewayResponse(gatewayResponse.toJson());
            transaction = transactionRepository.save(transaction);
            
            // 6. Send async notifications
            publishTransactionEvent(transaction);
            
            return PaymentResponse.from(transaction, gatewayResponse);
            
        } catch (Exception e) {
            log.error("Payment processing failed for request: {}", request, e);
            throw new PaymentProcessingException("Payment processing failed", e);
        }
    }
    
    private void validatePaymentRequest(PaymentRequest request) {
        if (request.getAmount() <= 0) {
            throw new InvalidRequestException("Amount must be positive");
        }
        
        if (StringUtils.isBlank(request.getCurrency())) {
            throw new InvalidRequestException("Currency is required");
        }
        
        // Additional validations...
    }
    
    private Transaction createTransaction(PaymentRequest request) {
        Transaction transaction = new Transaction();
        transaction.setMerchantId(request.getMerchantId());
        transaction.setMerchantTransactionId(request.getMerchantTransactionId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setCustomerInfo(request.getCustomerInfo());
        transaction.setMetadata(request.getMetadata());
        
        return transactionRepository.save(transaction);
    }
    
    private void publishTransactionEvent(Transaction transaction) {
        TransactionEvent event = new TransactionEvent(
            transaction.getId(),
            transaction.getStatus(),
            transaction.getMerchantId(),
            Instant.now()
        );
        
        kafkaTemplate.send("transaction-events", event);
    }
}
```

### Fraud Detection Service

```java
@Service
public class FraudDetectionService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private FraudRuleEngine ruleEngine;
    
    public FraudResult checkFraud(PaymentRequest request, Transaction transaction) {
        FraudContext context = buildFraudContext(request, transaction);
        
        // Rate limiting check
        if (isRateLimitExceeded(context)) {
            return FraudResult.block("Rate limit exceeded");
        }
        
        // Velocity checks
        if (isVelocityRuleViolated(context)) {
            return FraudResult.block("Velocity rule violated");
        }
        
        // ML-based risk scoring
        int riskScore = calculateRiskScore(context);
        
        // Apply business rules
        List<String> triggeredRules = ruleEngine.evaluate(context);
        
        FraudAction action = determineAction(riskScore, triggeredRules);
        
        // Save fraud check result
        saveFraudCheck(transaction.getId(), riskScore, triggeredRules, action);
        
        return new FraudResult(riskScore, triggeredRules, action);
    }
    
    private boolean isRateLimitExceeded(FraudContext context) {
        String key = "rate_limit:" + context.getClientIP();
        String currentCount = redisTemplate.opsForValue().get(key);
        
        if (currentCount == null) {
            redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(1));
            return false;
        }
        
        int count = Integer.parseInt(currentCount);
        if (count >= 100) { // 100 requests per minute
            return true;
        }
        
        redisTemplate.opsForValue().increment(key);
        return false;
    }
    
    private int calculateRiskScore(FraudContext context) {
        int score = 0;
        
        // High amount transactions
        if (context.getAmount().compareTo(BigDecimal.valueOf(1000)) > 0) {
            score += 20;
        }
        
        // New customer
        if (context.isNewCustomer()) {
            score += 15;
        }
        
        // Suspicious location
        if (context.isSuspiciousLocation()) {
            score += 25;
        }
        
        // Time-based checks (late night transactions)
        LocalTime time = LocalTime.now();
        if (time.isBefore(LocalTime.of(6, 0)) || time.isAfter(LocalTime.of(23, 0))) {
            score += 10;
        }
        
        return Math.min(score, 100);
    }
}
```

### Payment Gateway Interface

```java
public interface PaymentGatewayService {
    GatewayResponse processPayment(PaymentRequest request);
    GatewayResponse refundPayment(String transactionId, BigDecimal amount);
    TransactionStatus getTransactionStatus(String gatewayTransactionId);
}

@Service
public class StripeGatewayService implements PaymentGatewayService {
    
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    
    private final Stripe stripe;
    
    public StripeGatewayService() {
        this.stripe = new Stripe();
    }
    
    @Override
    public GatewayResponse processPayment(PaymentRequest request) {
        try {
            Stripe.apiKey = stripeSecretKey;
            
            Map<String, Object> params = new HashMap<>();
            params.put("amount", request.getAmount().multiply(BigDecimal.valueOf(100)).intValue()); // cents
            params.put("currency", request.getCurrency().toLowerCase());
            params.put("source", request.getPaymentToken());
            params.put("description", "Payment for merchant: " + request.getMerchantId());
            
            Charge charge = Charge.create(params);
            
            return GatewayResponse.builder()
                .success(charge.getPaid())
                .transactionId(charge.getId())
                .responseCode(charge.getStatus())
                .responseMessage(charge.getOutcome().getSellerMessage())
                .build();
                
        } catch (StripeException e) {
            return GatewayResponse.builder()
                .success(false)
                .responseCode(e.getCode())
                .responseMessage(e.getMessage())
                .build();
        }
    }
}
```

## 6. Security and Compliance Features

### PCI DSS Compliance
- **Data Encryption**: AES-256 encryption for card data at rest
- **Tokenization**: Replace sensitive card data with tokens
- **Access Control**: Role-based access with principle of least privilege
- **Audit Logging**: Complete audit trail for all operations
- **Network Security**: TLS 1.3, network segmentation
- **Vulnerability Management**: Regular security scans and penetration testing

### Implementation Examples

```java
@Component
public class CardTokenizer {
    
    @Autowired
    private VaultService vaultService;
    
    public String tokenizeCard(CardDetails cardDetails) {
        // Generate token
        String token = "tok_" + UUID.randomUUID().toString();
        
        // Encrypt and store card details
        String encryptedData = encrypt(cardDetails.toJson());
        vaultService.store(token, encryptedData);
        
        return token;
    }
    
    public CardDetails detokenize(String token) {
        String encryptedData = vaultService.retrieve(token);
        String decryptedData = decrypt(encryptedData);
        return CardDetails.fromJson(decryptedData);
    }
    
    private String encrypt(String data) {
        // AES-256 encryption implementation
        return AESUtil.encrypt(data, getEncryptionKey());
    }
}

@Component
public class AuditLogger {
    
    @EventListener
    public void logTransactionEvent(TransactionEvent event) {
        AuditLog auditLog = AuditLog.builder()
            .eventType(event.getType())
            .entityId(event.getTransactionId())
            .userId(SecurityContextHolder.getContext().getAuthentication().getName())
            .timestamp(Instant.now())
            .ipAddress(RequestContextHolder.getClientIpAddress())
            .userAgent(RequestContextHolder.getUserAgent())
            .details(event.toJson())
            .build();
            
        auditLogRepository.save(auditLog);
    }
}

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/v1/health").permitAll()
                .requestMatchers("/api/v1/payments/**").hasRole("MERCHANT")
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt())
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .headers(headers -> headers
                .frameOptions().deny()
                .contentTypeOptions()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubdomains(true)
                )
            );
            
        return http.build();
    }
}
```

### Rate Limiting and DDoS Protection

```java
@Component
public class RateLimitingFilter implements Filter {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String clientId = extractClientId(httpRequest);
        
        if (isRateLimited(clientId)) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(429);
            httpResponse.getWriter().write("Rate limit exceeded");
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    private boolean isRateLimited(String clientId) {
        String key = "rate_limit:" + clientId;
        String currentCount = redisTemplate.opsForValue().get(key);
        
        if (currentCount == null) {
            redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(1));
            return false;
        }
        
        int count = Integer.parseInt(currentCount);
        if (count >= 1000) { // 1000 requests per minute per client
            return true;
        }
        
        redisTemplate.opsForValue().increment(key);
        return false;
    }
}
```

### Monitoring and Alerting

```java
@Component
public class PaymentMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter successfulPayments;
    private final Counter failedPayments;
    private final Timer paymentProcessingTime;
    
    public PaymentMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.successfulPayments = Counter.builder("payments.successful")
            .description("Number of successful payments")
            .register(meterRegistry);
        this.failedPayments = Counter.builder("payments.failed")
            .description("Number of failed payments")
            .register(meterRegistry);
        this.paymentProcessingTime = Timer.builder("payments.processing.time")
            .description("Payment processing time")
            .register(meterRegistry);
    }
    
    public void recordSuccessfulPayment() {
        successfulPayments.increment();
    }
    
    public void recordFailedPayment() {
        failedPayments.increment();
    }
    
    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }
}
```

## 7. Deployment and Operations

### Docker Configuration
```dockerfile
FROM openjdk:17-jre-slim

COPY target/payment-gateway-*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Kubernetes Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: payment-gateway
spec:
  replicas: 3
  selector:
    matchLabels:
      app: payment-gateway
  template:
    metadata:
      labels:
        app: payment-gateway
    spec:
      containers:
      - name: payment-gateway
        image: payment-gateway:latest
        ports:
        - containerPort: 8080
        env:
        - name: DB_HOST
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: host
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: password
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /health/ready
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
```

## 8. API Documentation

### Authentication
All API endpoints require JWT Bearer token authentication.
```
Authorization: Bearer <jwt_token>
```

### Base URL
```
https://api.paymentgateway.com/v1
```

### Core Payment APIs

#### 1. Process Payment
**POST** `/payments`

Creates and processes a new payment transaction.

```json
{
  "merchant_transaction_id": "order_12345",
  "amount": 100.00,
  "currency": "USD",
  "payment_method": "CARD",
  "payment_details": {
    "card_token": "tok_abcd1234",
    "cvv": "123"
  },
  "customer_info": {
    "email": "customer@example.com",
    "phone": "+1234567890",
    "name": "John Doe"
  },
  "billing_address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "postal_code": "10001",
    "country": "US"
  },
  "metadata": {
    "order_id": "12345",
    "product_name": "Premium Subscription"
  }
}
```

**Response 200 OK:**
```json
{
  "transaction_id": "txn_abcd1234efgh5678",
  "status": "SUCCESS",
  "amount": 100.00,
  "currency": "USD",
  "gateway_transaction_id": "ch_1234567890",
  "merchant_transaction_id": "order_12345",
  "created_at": "2024-01-15T10:30:00Z",
  "processing_time_ms": 150,
  "fraud_score": 25,
  "payment_method": "CARD",
  "card_last_four": "4242"
}
```

**Response 400 Bad Request:**
```json
{
  "error": "INVALID_REQUEST",
  "message": "Amount must be positive",
  "details": {
    "field": "amount",
    "code": "INVALID_VALUE"
  }
}
```

#### 2. Get Transaction Status
**GET** `/payments/{transaction_id}`

Retrieves the current status of a transaction.

**Response 200 OK:**
```json
{
  "transaction_id": "txn_abcd1234efgh5678",
  "merchant_transaction_id": "order_12345",
  "status": "SUCCESS",
  "amount": 100.00,
  "currency": "USD",
  "payment_method": "CARD",
  "created_at": "2024-01-15T10:30:00Z",
  "updated_at": "2024-01-15T10:30:00Z",
  "events": [
    {
      "event_type": "CREATED",
      "timestamp": "2024-01-15T10:30:00Z",
      "status": "PENDING"
    },
    {
      "event_type": "AUTHORIZED",
      "timestamp": "2024-01-15T10:30:01Z",
      "status": "SUCCESS"
    }
  ]
}
```

#### 3. Process Refund
**POST** `/payments/{transaction_id}/refunds`

Creates a refund for a successful transaction.

```json
{
  "amount": 50.00,
  "reason": "Customer requested partial refund",
  "metadata": {
    "refund_reason": "customer_request"
  }
}
```

**Response 200 OK:**
```json
{
  "refund_id": "ref_abcd1234efgh5678",
  "transaction_id": "txn_abcd1234efgh5678",
  "amount": 50.00,
  "status": "SUCCESS",
  "gateway_refund_id": "re_1234567890",
  "created_at": "2024-01-15T11:30:00Z"
}
```

#### 4. List Transactions
**GET** `/payments`

Retrieves a paginated list of transactions.

**Query Parameters:**
- `page` (int): Page number (default: 1)
- `limit` (int): Items per page (default: 20, max: 100)
- `status` (string): Filter by status (SUCCESS, FAILED, PENDING, REFUNDED)
- `from_date` (string): Start date (ISO 8601)
- `to_date` (string): End date (ISO 8601)
- `amount_min` (decimal): Minimum amount
- `amount_max` (decimal): Maximum amount

**Response 200 OK:**
```json
{
  "data": [
    {
      "transaction_id": "txn_abcd1234efgh5678",
      "merchant_transaction_id": "order_12345",
      "amount": 100.00,
      "currency": "USD",
      "status": "SUCCESS",
      "payment_method": "CARD",
      "created_at": "2024-01-15T10:30:00Z"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "total_pages": 5,
    "total_count": 89,
    "has_next": true,
    "has_previous": false
  }
}
```

### Merchant Management APIs

#### 5. Get Merchant Details
**GET** `/merchants/me`

Retrieves current merchant information.

**Response 200 OK:**
```json
{
  "merchant_id": "mer_abcd1234efgh5678",
  "name": "ACME Store",
  "email": "admin@acmestore.com",
  "status": "ACTIVE",
  "webhook_url": "https://acmestore.com/webhooks/payments",
  "payment_methods": [
    {
      "type": "CARD",
      "provider": "STRIPE",
      "is_active": true,
      "supported_currencies": ["USD", "EUR", "GBP"]
    },
    {
      "type": "BANK_TRANSFER",
      "provider": "PLAID",
      "is_active": true,
      "supported_currencies": ["USD"]
    }
  ],
  "created_at": "2024-01-01T00:00:00Z"
}
```

#### 6. Update Webhook URL
**PUT** `/merchants/webhook`

Updates the webhook URL for transaction notifications.

```json
{
  "webhook_url": "https://mystore.com/webhooks/payments"
}
```

**Response 200 OK:**
```json
{
  "message": "Webhook URL updated successfully",
  "webhook_url": "https://mystore.com/webhooks/payments"
}
```

### Reporting APIs

#### 7. Transaction Summary
**GET** `/reports/summary`

Retrieves transaction summary statistics.

**Query Parameters:**
- `from_date` (string): Start date (ISO 8601)
- `to_date` (string): End date (ISO 8601)
- `group_by` (string): GROUP BY dimension (day, week, month)

**Response 200 OK:**
```json
{
  "period": {
    "from": "2024-01-01T00:00:00Z",
    "to": "2024-01-31T23:59:59Z"
  },
  "summary": {
    "total_transactions": 1250,
    "successful_transactions": 1180,
    "failed_transactions": 70,
    "total_amount": 125000.00,
    "successful_amount": 118000.00,
    "average_transaction_amount": 100.00,
    "success_rate": 94.4,
    "total_refunds": 15,
    "refund_amount": 1500.00
  },
  "daily_breakdown": [
    {
      "date": "2024-01-01",
      "transactions": 45,
      "amount": 4500.00,
      "success_rate": 95.6
    }
  ]
}
```

#### 8. Fraud Analysis
**GET** `/reports/fraud`

Retrieves fraud detection statistics.

**Response 200 OK:**
```json
{
  "fraud_summary": {
    "total_checks": 1250,
    "blocked_transactions": 25,
    "flagged_for_review": 50,
    "average_risk_score": 15.2,
    "block_rate": 2.0
  },
  "top_triggered_rules": [
    {
      "rule_name": "high_amount_new_customer",
      "triggers": 15,
      "block_rate": 60.0
    },
    {
      "rule_name": "velocity_check",
      "triggers": 10,
      "block_rate": 30.0
    }
  ]
}
```

### Webhook APIs

#### 9. Webhook Events
Payment gateway sends webhook notifications for transaction events.

**Webhook URL:** `POST {merchant_webhook_url}`

**Headers:**
```
Content-Type: application/json
X-Signature: sha256=<hmac_signature>
X-Event-Type: transaction.success
```

**Payload:**
```json
{
  "event_id": "evt_abcd1234efgh5678",
  "event_type": "transaction.success",
  "timestamp": "2024-01-15T10:30:00Z",
  "data": {
    "transaction_id": "txn_abcd1234efgh5678",
    "merchant_transaction_id": "order_12345",
    "amount": 100.00,
    "currency": "USD",
    "status": "SUCCESS",
    "payment_method": "CARD",
    "customer_info": {
      "email": "customer@example.com"
    }
  }
}
```

**Event Types:**
- `transaction.created`
- `transaction.success`
- `transaction.failed`
- `transaction.refunded`
- `fraud.high_risk_detected`

#### 10. Retry Failed Webhook
**POST** `/webhooks/{delivery_id}/retry`

Manually retries a failed webhook delivery.

**Response 200 OK:**
```json
{
  "message": "Webhook retry scheduled",
  "delivery_id": "whd_abcd1234efgh5678",
  "next_attempt_at": "2024-01-15T10:35:00Z"
}
```

### Administration APIs

#### 11. Health Check
**GET** `/health`

System health check endpoint.

**Response 200 OK:**
```json
{
  "status": "healthy",
  "timestamp": "2024-01-15T10:30:00Z",
  "version": "1.0.0",
  "checks": {
    "database": "healthy",
    "redis": "healthy",
    "kafka": "healthy",
    "external_gateways": "healthy"
  }
}
```

### Error Codes

| Code | Description |
|------|-------------|
| `INVALID_REQUEST` | Request validation failed |
| `AUTHENTICATION_FAILED` | Invalid or missing authentication |
| `INSUFFICIENT_FUNDS` | Customer has insufficient funds |
| `CARD_DECLINED` | Card was declined by issuer |
| `FRAUD_DETECTED` | Transaction blocked by fraud rules |
| `GATEWAY_ERROR` | Payment gateway returned error |
| `DUPLICATE_TRANSACTION` | Merchant transaction ID already exists |
| `TRANSACTION_NOT_FOUND` | Transaction does not exist |
| `REFUND_NOT_ALLOWED` | Refund not possible for this transaction |
| `RATE_LIMIT_EXCEEDED` | Too many requests |

### Rate Limits

| Endpoint | Rate Limit |
|----------|------------|
| `/payments` (POST) | 1000/minute |
| `/payments` (GET) | 5000/minute |
| `/payments/{id}` (GET) | 5000/minute |
| `/reports/*` | 100/minute |
| `/merchants/*` | 200/minute |

### Java REST Controller Implementation

```java
@RestController
@RequestMapping("/api/v1/payments")
@Validated
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request,
            @RequestHeader("Authorization") String authHeader) {
        
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable String transactionId) {
        
        TransactionResponse response = paymentService.getTransaction(transactionId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{transactionId}/refunds")
    public ResponseEntity<RefundResponse> processRefund(
            @PathVariable String transactionId,
            @Valid @RequestBody RefundRequest request) {
        
        RefundResponse response = paymentService.processRefund(transactionId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<PaginatedResponse<TransactionSummary>> listTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime toDate) {
        
        TransactionFilter filter = TransactionFilter.builder()
            .page(page)
            .limit(limit)
            .status(status)
            .fromDate(fromDate)
            .toDate(toDate)
            .build();
            
        PaginatedResponse<TransactionSummary> response = paymentService.listTransactions(filter);
        return ResponseEntity.ok(response);
    }
}

@RestController
@RequestMapping("/api/v1/merchants")
public class MerchantController {
    
    @Autowired
    private MerchantService merchantService;
    
    @GetMapping("/me")
    public ResponseEntity<MerchantResponse> getCurrentMerchant(Authentication auth) {
        String merchantId = auth.getName();
        MerchantResponse response = merchantService.getMerchant(merchantId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/webhook")
    public ResponseEntity<WebhookResponse> updateWebhook(
            @Valid @RequestBody WebhookUpdateRequest request,
            Authentication auth) {
        
        String merchantId = auth.getName();
        WebhookResponse response = merchantService.updateWebhook(merchantId, request);
        return ResponseEntity.ok(response);
    }
}
```

This comprehensive payment gateway design covers all requirements with production-ready architecture, security measures, scalable implementation, and complete API documentation.