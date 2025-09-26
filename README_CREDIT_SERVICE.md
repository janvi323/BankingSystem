# Credit Score Microservice Integration

This document describes how to set up and run the Credit Score Microservice alongside your Banking System.

## Architecture Overview

The system now consists of two microservices:

1. **Banking System** (Main Service) - Port 8082
   - Handles customer management, loans, authentication
   - Communicates with Credit Score Service for credit evaluations

2. **Credit Score Service** - Port 8083
   - Dedicated microservice for credit score calculations
   - Maintains detailed credit information and scoring algorithms
   - Provides REST APIs for credit score operations

## Prerequisites

- Java 21
- PostgreSQL installed and running
- Maven 3.6+

## Database Setup

You need to create two databases:

```sql
-- For main banking system (existing)
CREATE DATABASE loan_db;

-- For credit score service (new)
CREATE DATABASE credit_score_db;
```

## Running the Services

### 1. Start the Credit Score Microservice

```bash
cd credit-score-service
mvn clean install
mvn spring-boot:run
```

The service will start on port 8083.

### 2. Start the Main Banking System

```bash
# From the root directory
mvn clean install
mvn spring-boot:run
```

The main service will start on port 8082.

## API Endpoints

### Credit Score Service Direct APIs (Port 8083)

- `POST /api/credit-scores` - Calculate and store credit score
- `GET /api/credit-scores/customer/{customerId}` - Get credit score by customer ID
- `GET /api/credit-scores/email/{email}` - Get credit score by email
- `PUT /api/credit-scores/customer/{customerId}` - Update credit score
- `DELETE /api/credit-scores/customer/{customerId}` - Delete credit score
- `GET /api/credit-scores/health` - Health check

### Banking System Credit Score Integration APIs (Port 8082)

- `GET /api/banking/credit-score/{customerId}` - Get customer's credit score
- `POST /api/banking/credit-score/{customerId}/calculate` - Calculate credit score for customer
- `PUT /api/banking/credit-score/{customerId}` - Update customer's credit score
- `DELETE /api/banking/credit-score/{customerId}` - Delete customer's credit score
- `GET /api/banking/credit-score/{customerId}/exists` - Check if customer has credit score
- `GET /api/banking/credit-score/health` - Check credit score service health

## Example Usage

### 1. Calculate Credit Score for a Customer

```bash
curl -X POST http://localhost:8082/api/banking/credit-score/1/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "income": 75000,
    "debtToIncomeRatio": 0.3,
    "paymentHistoryScore": 85,
    "creditUtilizationRatio": 0.25,
    "creditAgeMonths": 60,
    "numberOfAccounts": 5
  }'
```

### 2. Get Customer's Credit Score

```bash
curl http://localhost:8082/api/banking/credit-score/1
```

### 3. Check Service Health

```bash
curl http://localhost:8082/api/banking/credit-score/health
```

## Credit Score Calculation Algorithm

The credit score is calculated using the following factors:

- **Income Factor** (max 200 points): Based on annual income
- **Payment History** (max 150 points): Historical payment performance
- **Debt-to-Income Ratio** (max 100 points): Lower ratio = higher score
- **Credit Utilization** (max 100 points): Lower utilization = higher score
- **Credit Age** (max 100 points): Length of credit history
- **Number of Accounts** (max 50 points): Diversity of credit accounts

**Score Ranges:**
- 800-850: Excellent
- 740-799: Very Good
- 670-739: Good
- 580-669: Fair
- 300-579: Poor

## Configuration

### Main Banking System
```properties
# Credit Score Service Configuration
credit.score.service.url=http://localhost:8083
```

### Credit Score Service
```properties
server.port=8083
spring.application.name=credit-score-service
spring.datasource.url=jdbc:postgresql://localhost:5432/credit_score_db
```

## Troubleshooting

1. **Connection Refused**: Ensure the Credit Score Service is running on port 8083
2. **Database Connection Issues**: Verify PostgreSQL is running and databases exist
3. **Service Health Check**: Use `/api/banking/credit-score/health` to verify connectivity

## Development Notes

- The Credit Score Service maintains its own database and entity lifecycle
- The main Banking System synchronizes the calculated credit score with its Customer entity
- Communication between services uses WebClient for reactive HTTP calls
- Both services include comprehensive error handling and validation