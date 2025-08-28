# PayPal-like Payment System Design

## Table of Contents
1. [System Overview](#system-overview)
2. [High-Level Architecture](#high-level-architecture)
3. [Core Services](#core-services)
4. [Database Design](#database-design)
5. [Payment Processing Flow](#payment-processing-flow)
6. [Risk Scoring System](#risk-scoring-system)
7. [Idempotency](#idempotency)
8. [Failure Handling & Retry Mechanisms](#failure-handling--retry-mechanisms)
9. [SAGA Pattern for Distributed Transactions](#saga-pattern-for-distributed-transactions)
10. [Scalability Considerations](#scalability-considerations)

## System Overview

### Requirements
**Functional Requirements:**
- User registration and authentication
- Money transfers between users
- Integration with banks and credit cards
- Transaction history and notifications
- Multi-currency support
- Merchant payment processing

**Non-Functional Requirements:**
- 99.99% availability
- Handle 10M+ daily active users
- Process 50K+ transactions per second at peak
- Sub-200ms response time for payments
- ACID compliance for financial transactions
- PCI DSS compliance

### Scale Estimates
- 500M registered users
- 50M daily active users
- 100M transactions per day
- Average transaction value: $50
- Peak QPS: 50,000
- Storage: ~100TB (transaction history, user data)

## High-Level Architecture

### Overall System Architecture
```
                    ┌─────────────────────────┐
                    │       CDN & WAF         │
                    └─────────┬───────────────┘
                              │
┌─────────────┐ ┌─────────────▼───────────────┐ ┌─────────────┐
│Mobile Apps  │ │       Load Balancer         │ │Web Frontend │
└─────────────┘ └─────────────┬───────────────┘ └─────────────┘
                              │
                    ┌─────────────────────────┐
                    │      API Gateway        │
                    │ (Auth, Rate Limiting,   │
                    │  Routing, Monitoring)   │
                    └─────────┬───────────────┘
                              │
┌─────────────────────────────┼─────────────────────────────┐
│                             │                             │
│  ┌─────────────┐    ┌───────▼───────┐    ┌─────────────┐ │
│  │User Service │    │Payment Service│    │Account      │ │
│  │(Profile &   │    │(Orchestration │    │Service      │ │
│  │Auth)        │    │& Validation)  │    │(Balances)   │ │ 
│  └─────────────┘    └───────┬───────┘    └─────────────┘ │
│                             │                             │
│  ┌─────────────┐    ┌───────▼───────┐    ┌─────────────┐ │
│  │Risk Service │    │Transaction    │    │Notification │ │
│  │(Fraud       │    │Service        │    │Service      │ │
│  │Detection)   │    │(Records)      │    │(Alerts)     │ │
│  └─────────────┘    └───────────────┘    └─────────────┘ │
│                                                           │
│                    Microservices Layer                    │
└───────────────────────────┬───────────────────────────────┘
                            │
              ┌─────────────▼─────────────┐
              │       Data Layer          │
              │                           │
              │ ┌─────────┐ ┌─────────┐  │
              │ │PostgreSQL│ │  Redis  │  │
              │ │(ACID DB) │ │ (Cache) │  │
              │ └─────────┘ └─────────┘  │
              │                           │
              │ ┌─────────┐ ┌─────────┐  │
              │ │Elasticsearch│ │Kafka  │ │
              │ │ (Search) │ │(Events) │  │
              │ └─────────┘ └─────────┘  │
              └───────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
┌───────▼───────┐  ┌────────▼────────┐  ┌──────▼──────┐
│  Bank APIs    │  │  Card Networks  │  │   KYC/AML   │
│  (ACH, Wire)  │  │ (Visa, Master)  │  │  Providers  │
└───────────────┘  └─────────────────┘  └─────────────┘
```

**Service Communication Flow:**
- Payment Service orchestrates the payment process
- It calls other services (doesn't mean direct arrows between all services)
- Each service has specific responsibilities and communicates via APIs
- Data flows through the Payment Service as the central coordinator

### Technology Stack
- **Load Balancer:** AWS ALB/NGINX
- **API Gateway:** Kong/AWS API Gateway  
- **Services:** Java/Go microservices
- **Databases:** PostgreSQL (primary), Redis (cache), Elasticsearch (search)
- **Message Queue:** Apache Kafka
- **Monitoring:** Prometheus, Grafana
- **Security:** OAuth 2.0, JWT tokens

## Core Services

### 1. User Service
**Responsibilities:**
- User registration and profile management
- Authentication and authorization
- KYC (Know Your Customer) verification
- Account settings and preferences

**Key APIs:**
- `POST /users/register` - Create new user account
- `POST /users/login` - Authenticate user
- `GET /users/{userId}/profile` - Get user profile
- `PUT /users/{userId}/kyc` - Submit KYC documents

### 2. Account Service
**Responsibilities:**
- Wallet balance management and updates
- Account linking (bank accounts, credit/debit cards)
- Currency conversion and multi-currency wallets
- Account freezing/unfreezing for security

**Key APIs:**
- `GET /accounts/{userId}/balance` - Check account balance
- `POST /accounts/{userId}/link-bank` - Link external bank account
- `POST /accounts/{userId}/add-card` - Add payment card
- `PUT /accounts/{userId}/reserve-funds` - Reserve funds for pending transaction

### 3. Payment Service (Central Orchestrator)
**Responsibilities:**
- Orchestrate the entire payment process from start to finish
- **Handle idempotency checks** for duplicate request prevention
- Coordinate between Risk, Account, and Transaction services
- **Manage auth-capture flow** for card payments
- Handle payment validation and business logic
- Manage payment state transitions
- Interface with external payment processors

**Key APIs:**
- `POST /payments/send` - Send money to another user
- `POST /payments/request` - Request money from user  
- `GET /payments/{paymentId}/status` - Check payment status
- `POST /payments/{paymentId}/cancel` - Cancel pending payment
- `POST /payments/{paymentId}/capture` - Capture authorized card payment
- `POST /payments/{paymentId}/void` - Void authorized card payment

**Auth-Capture Specific Operations:**
- `POST /payments/auth` - Authorize card payment (hold funds)
- `POST /payments/capture/{authId}` - Capture previously authorized payment
- `POST /payments/void/{authId}` - Cancel authorization before capture

### 4. Transaction Service
**Responsibilities:**
- Record all financial transactions in the ledger
- Maintain complete transaction history and audit trails
- Generate financial statements and reports
- Handle transaction disputes and chargebacks
- Ensure double-entry bookkeeping compliance

**Key APIs:**
- `GET /transactions/{userId}/history` - Get user's transaction history
- `POST /transactions/{transactionId}/dispute` - File transaction dispute
- `GET /transactions/{transactionId}/details` - Get detailed transaction info
- `POST /transactions/create` - Create new transaction record (called by Payment Service)

### 5. Risk Service
**Responsibilities:**
- Real-time fraud detection
- Risk scoring for transactions
- AML (Anti-Money Laundering) checks
- Suspicious activity monitoring

**Key APIs:**
- `POST /risk/evaluate-transaction` - Score transaction risk
- `GET /risk/{userId}/score` - Get user risk profile
- `POST /risk/report-suspicious` - Report suspicious activity

### 6. Notification Service
**Responsibilities:**
- Send transaction confirmations
- Account alerts and notifications
- Marketing communications
- Push notifications to mobile apps

**Key APIs:**
- `POST /notifications/send` - Send notification
- `GET /notifications/{userId}/preferences` - Notification settings

## Database Design

### User Database (PostgreSQL)
```sql
-- Users table
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    date_of_birth DATE,
    kyc_status ENUM('pending', 'verified', 'rejected'),
    account_status ENUM('active', 'suspended', 'closed'),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- User profiles
CREATE TABLE user_profiles (
    user_id UUID REFERENCES users(user_id),
    address_line1 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(3),
    postal_code VARCHAR(20),
    ssn_hash VARCHAR(255), -- Encrypted
    PRIMARY KEY (user_id)
);
```

### Account Database (PostgreSQL)
```sql
-- Wallets/Accounts
CREATE TABLE accounts (
    account_id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(user_id),
    account_type ENUM('wallet', 'bank', 'card'),
    currency_code VARCHAR(3),
    balance DECIMAL(15,2) DEFAULT 0.00,
    available_balance DECIMAL(15,2) DEFAULT 0.00,
    account_status ENUM('active', 'frozen', 'closed'),
    created_at TIMESTAMP DEFAULT NOW()
);

-- External account links
CREATE TABLE linked_accounts (
    link_id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(user_id),
    external_account_id VARCHAR(255), -- Encrypted
    account_type ENUM('bank', 'credit_card', 'debit_card'),
    bank_name VARCHAR(100),
    routing_number VARCHAR(20), -- Encrypted
    is_verified BOOLEAN DEFAULT FALSE,
    is_primary BOOLEAN DEFAULT FALSE
);
```

### Transaction Database (PostgreSQL - Partitioned)
```sql
-- Main transactions table (partitioned by date)
CREATE TABLE transactions (
    transaction_id UUID PRIMARY KEY,
    sender_user_id UUID REFERENCES users(user_id),
    receiver_user_id UUID REFERENCES users(user_id),
    amount DECIMAL(15,2) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    transaction_type ENUM('p2p_transfer', 'merchant_payment', 'withdrawal', 'deposit'),
    status ENUM('pending', 'completed', 'failed', 'cancelled'),
    description TEXT,
    reference_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW(),
    idempotency_key VARCHAR(255) UNIQUE
) PARTITION BY RANGE (created_at);

-- Ledger entries for double-entry bookkeeping
CREATE TABLE ledger_entries (
    entry_id UUID PRIMARY KEY,
    transaction_id UUID REFERENCES transactions(transaction_id),
    account_id UUID REFERENCES accounts(account_id),
    amount DECIMAL(15,2) NOT NULL,
    entry_type ENUM('debit', 'credit'),
    created_at TIMESTAMP DEFAULT NOW()
);
```

## Payment Processing Flow

### Complete Payment Flow with Idempotency and Auth-Capture
```
┌──────────┐  1. Payment Request   ┌──────────────┐
│  Client  │    + Idempotency-Key  │ API Gateway  │
└──────────┘ ────────────────────► └──────┬───────┘
                                          │ 2. Route & Validate
                                          ▼
                                  ┌──────────────┐
                                  │Payment Service│
                                  └──────┬───────┘
                                         │ 3. IDEMPOTENCY CHECK
                                         ▼
                               ┌─────────────────┐
                               │ Idempotency     │
                               │ Key Exists?     │
                               └──┬──────────┬───┘
                                  │          │
                             Yes  │          │ No
                                  ▼          ▼
                          ┌─────────────┐ ┌─────────────┐
                          │Return Cached│ │Create Record│
                          │Response     │ │Status=PROC  │
                          └─────────────┘ └──────┬──────┘
                                                 │ 4. Risk Evaluation
                                                 ▼
                                          ┌──────────────┐
                                          │Risk Service  │
                                          └──────┬───────┘
                                                 │ 5. Risk Score
                                                 ▼
                                       ┌─────────────────┐
                                       │ Risk Decision   │
                                       │ Low│Medium│High │
                                       └──┬───┬────┬───┘
                                          │   │    │
                                 Auto     │   │    │ Block &
                                Approve   │   │    │ Manual Review
                                          ▼   │    ▼
                                ┌──────────────┐ ┌──────────────┐
                         6.     │Account Service│ │Manual Review │
                       Reserve  └──────┬───────┘ │   Queue      │
                        Funds          │         └──────────────┘
                                       ▼              │
                                ┌──────────────┐      │ 
                                │   CARD       │      │ Manual
                                │ PAYMENT?     │      │ Approval
                                └──┬───────┬───┘      │
                                   │       │          │
                              Yes  │       │ No       │
                                   ▼       ▼          │
                           ┌─────────────────┐        │
                           │  AUTH-CAPTURE   │        │
                           │      FLOW       │        │
                           └──────┬──────────┘        │
                                  │                   │
               ┌──────────────────┼───────────────────┘
               │                  │    
               ▼                  ▼
        ┌─────────────┐    ┌──────────────┐
        │7a. AUTH     │    │7b. DIRECT    │
        │(Reserve on  │    │TRANSFER      │
        │Card/Bank)   │    │(Wallet/Bank) │
        └──────┬──────┘    └──────┬───────┘
               │                  │
               ▼                  ▼
        ┌─────────────┐    ┌──────────────┐
        │8a. CAPTURE  │    │8b. Execute   │
        │(Charge Card)│    │Transfer      │
        └──────┬──────┘    └──────┬───────┘
               │                  │
               └──────────────────┼──────────────────┐
                                  │                  │
                        ┌─────────▼─────────┐        │
                        │    Success?       │        │
                        ├─────────┬─────────┤        │
                        ▼         ▼         ▼        │
                   ┌─────────┐ ┌─────────┐ ┌─────────┐│
                   │SUCCESS  │ │TIMEOUT  │ │FAILURE  ││
                   │         │ │         │ │         ││
                   │9. Update│ │Retry    │ │Rollback ││ 
                   │Transaction│ │Logic   │ │& Notify ││
                   │Record   │ │         │ │         ││
                   │& Notify │ │         │ │         ││
                   └─────────┘ └─────────┘ └─────────┘│
                        │                             │
                        ▼                             │
                 ┌─────────────┐                      │
                 │10. Update   │                      │
                 │Idempotency  │◄─────────────────────┘
                 │Key Status   │
                 │=COMPLETED   │
                 └─────────────┘
```

### Auth-Capture Flow Details
```
Card Payment Auth-Capture Flow:

┌─────────────┐  1. Authorization Request  ┌─────────────┐
│Payment Svc  ├──────────────────────────►│Card Network │
│             │   (Amount + Card Details)  │(Visa/Master)│
└─────────────┘                            └──────┬──────┘
       │                                          │
       │ 2. Auth Response                         │ 3. Bank Check
       │   (Success + Auth Code)                  ▼
       │    ┌─────────────────────────────────────────┐
       │    │          Issuing Bank               │
       │    │   - Check card validity              │
       │    │   - Verify available credit         │
       │    │   - Place hold on funds             │
       │    └─────────────────┬───────────────────┘
       │                      │ 4. Auth Decision
       ▼                      ▼
┌─────────────┐  ◄─────────────────────────────────────┐
│Auth Success │                                        │
│- Store Auth │                                        │
│  Code       │                                        │
│- 7-day      │  ──── Wait for business logic ────     │
│  Expiry     │       (minutes to hours)               │
└─────┬───────┘                                        │
      │ 5. Capture Request                             │
      │   (Auth Code + Final Amount)                   │
      └─────────────────────────────────────────────── ┘
                              │
                              ▼
                     ┌─────────────────┐
                     │   Capture       │
                     │ - Charge card   │
                     │ - Release hold  │
                     │ - Final settle  │
                     └─────────────────┘

Alternative: VOID if business logic fails
- Cancel authorization before capture
- Release held funds immediately
- No charge to customer
```

### Payment Failure and Retry Flow
```
                        External Service Call
                              │
                              ▼
                        ┌─────────────┐
                        │  Response   │
                        │   Check     │
                        └──────┬──────┘
                               │
                ┌──────────────┼──────────────┐
                ▼              ▼              ▼
          ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
          │   SUCCESS   │ │   TIMEOUT   │ │   ERROR     │
          │             │ │             │ │             │ 
          │Complete     │ │Classify     │ │Classify     │
          │Transaction  │ │Failure      │ │Failure      │
          └─────────────┘ └──────┬──────┘ └──────┬──────┘
                                 │               │
                                 ▼               ▼
                         ┌──────────────────────────┐
                         │    Failure Analysis      │
                         │                          │
                         │ Retriable?  │ Non-Retriable?│
                         └─────┬───────┼──────┬──────┘
                               │       │      │
                      Yes      ▼       │      ▼ No
                         ┌─────────┐   │ ┌─────────┐
                         │ Retry   │   │ │ Fail    │
                         │ Logic   │   │ │ Fast    │
                         └────┬────┘   │ └─────────┘
                              │        │      │
                    ┌─────────▼─────────▼──────▼─────────┐
                    │        Attempt Counter             │
                    │                                    │
                    │ Attempt < Max?  │ Attempt >= Max?  │
                    └────┬────────────┼─────────┬────────┘
                         │            │         │
                      Yes▼         No │         ▼ 
                  ┌─────────────┐      │  ┌─────────────┐
                  │Exponential  │      │  │Send to Dead │
                  │Backoff      │      │  │Letter Queue │
                  │Wait & Retry │      │  │& Alert Ops  │
                  └─────────────┘      │  └─────────────┘
                         │             │         │
                         └─────────────┼─────────┘
                                       ▼
                                ┌─────────────┐
                                │   SAGA      │
                                │ Compensation│
                                │  Rollback   │
                                └─────────────┘
```