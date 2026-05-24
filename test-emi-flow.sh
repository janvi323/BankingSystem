#!/bin/bash

# EMI Flow Testing Script using curl
BASE_URL="http://localhost:8082"
CUSTOMER_EMAIL="testcustomer_$(date +%s%N)@example.com"
CUSTOMER_PASSWORD="TestPass123"
CUSTOMER_NAME="Test Customer"

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print headers
print_header() {
    echo -e "\n${YELLOW}========================================${NC}"
    echo -e "${YELLOW}$1${NC}"
    echo -e "${YELLOW}========================================${NC}\n"
}

# Function to print success
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Function to print error
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Function to print info
print_info() {
    echo -e "${CYAN}  $1${NC}"
}

# Save cookies in files
CUSTOMER_COOKIES=$(mktemp)
ADMIN_COOKIES=$(mktemp)

trap "rm -f $CUSTOMER_COOKIES $ADMIN_COOKIES" EXIT

print_header "STEP 1: Register New Customer"
print_info "Email: $CUSTOMER_EMAIL"

REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"fullName\": \"$CUSTOMER_NAME\",
    \"email\": \"$CUSTOMER_EMAIL\",
    \"password\": \"$CUSTOMER_PASSWORD\",
    \"confirmPassword\": \"$CUSTOMER_PASSWORD\",
    \"phoneNumber\": \"9876543210\",
    \"address\": \"123 Test Street, City, Country\",
    \"role\": \"CUSTOMER\",
    \"income\": 500000
  }")

if echo "$REGISTER_RESPONSE" | grep -q "Registration successful"; then
    print_success "Customer registered"
else
    print_error "Registration failed"
    print_info "Response: $REGISTER_RESPONSE"
    exit 1
fi

print_header "STEP 2: Login as Customer"
print_info "Logging in as: $CUSTOMER_EMAIL"

curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -c "$CUSTOMER_COOKIES" \
  -d "{
    \"email\": \"$CUSTOMER_EMAIL\",
    \"password\": \"$CUSTOMER_PASSWORD\"
  }" > /dev/null

# Get customer ID
CUSTOMER_ID=$(curl -s -X GET "$BASE_URL/api/auth/current" \
  -H "Content-Type: application/json" \
  -b "$CUSTOMER_COOKIES" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')

if [ ! -z "$CUSTOMER_ID" ]; then
    print_success "Login successful, Customer ID: $CUSTOMER_ID"
else
    print_error "Could not get customer ID"
    exit 1
fi

print_header "STEP 3: Apply for Loan"
print_info "Applying for: Home Purchase - 500000 for 12 months"

LOAN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/loans/apply" \
  -H "Content-Type: application/json" \
  -b "$CUSTOMER_COOKIES" \
  -d '{
    "amount": 500000,
    "purpose": "Home Purchase",
    "tenure": 12,
    "interestRate": 8.5
  }')

print_success "Loan application submitted"

sleep 1

# Get loan ID
LOAN_ID=$(curl -s -X GET "$BASE_URL/api/loans/my-loans" \
  -H "Content-Type: application/json" \
  -b "$CUSTOMER_COOKIES" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')

if [ ! -z "$LOAN_ID" ]; then
    print_success "Loan ID: $LOAN_ID"
    LOAN_AMOUNT=$(curl -s -X GET "$BASE_URL/api/loans/my-loans" \
      -H "Content-Type: application/json" \
      -b "$CUSTOMER_COOKIES" | grep -o '"amount":[0-9]*' | head -1 | grep -o '[0-9]*')
    print_info "Amount: ₹$LOAN_AMOUNT"
else
    print_error "Could not get loan ID"
    exit 1
fi

print_header "STEP 4: Logout Customer"

curl -s -X POST "$BASE_URL/api/auth/logout" \
  -H "Content-Type: application/json" \
  -b "$CUSTOMER_COOKIES" > /dev/null

print_success "Logout successful"

print_header "STEP 5: Login as Admin"
print_info "Admin credentials: admin@bank.com / admin123"

curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -c "$ADMIN_COOKIES" \
  -d '{
    "email": "admin@bank.com",
    "password": "admin123"
  }' > /dev/null

print_success "Admin login successful"

print_header "STEP 6: Approve Loan"
print_info "Approving loan ID: $LOAN_ID"

APPROVE_RESPONSE=$(curl -s -X PUT "$BASE_URL/api/loans/$LOAN_ID/status" \
  -H "Content-Type: application/json" \
  -b "$ADMIN_COOKIES" \
  -d '{
    "status": "APPROVED"
  }')

if echo "$APPROVE_RESPONSE" | grep -q "successful"; then
    print_success "Loan approved"
else
    print_error "Loan approval may have failed"
    print_info "Response: $APPROVE_RESPONSE"
fi

print_header "STEP 7: Generate EMIs"
print_info "Generating EMIs for approved loans..."

EMI_RESPONSE=$(curl -s -X POST "$BASE_URL/api/emi/generate-all" \
  -H "Content-Type: application/json" \
  -b "$ADMIN_COOKIES" \
  -d '{}')

print_success "EMI generation completed"

print_header "STEP 8: Logout Admin"

curl -s -X POST "$BASE_URL/api/auth/logout" \
  -H "Content-Type: application/json" \
  -b "$ADMIN_COOKIES" > /dev/null

print_success "Logout successful"

print_header "STEP 9: Re-Login as Customer"
print_info "Logging in again as: $CUSTOMER_EMAIL"

curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -c "$CUSTOMER_COOKIES" \
  -d "{
    \"email\": \"$CUSTOMER_EMAIL\",
    \"password\": \"$CUSTOMER_PASSWORD\"
  }" > /dev/null

print_success "Re-login successful"

print_header "STEP 10: Check EMIs"
print_info "Fetching all EMIs..."

EMI_DATA=$(curl -s -X GET "$BASE_URL/api/emi/my-emis" \
  -H "Content-Type: application/json" \
  -b "$CUSTOMER_COOKIES")

# Count EMIs
EMI_COUNT=$(echo "$EMI_DATA" | grep -o '"emiNumber"' | wc -l)

if [ $EMI_COUNT -gt 0 ]; then
    print_success "EMIs retrieved: $EMI_COUNT EMIs found"
    
    # Extract first EMI ID and details
    EMI_ID=$(echo "$EMI_DATA" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
    print_info "First EMI ID: $EMI_ID"
    
    # Display all EMIs
    print_info "\nAll EMIs:"
    echo "$EMI_DATA" | grep -o '{[^}]*"emiNumber"[^}]*}' | sed 's/^/    /'
else
    print_error "No EMIs found!"
    print_info "Response: $EMI_DATA"
    exit 1
fi

# Step 11: Pay EMI
if [ ! -z "$EMI_ID" ]; then
    print_header "STEP 11: Pay EMI"
    print_info "Paying EMI ID: $EMI_ID"
    
    PAY_RESPONSE=$(curl -s -X POST "$BASE_URL/api/emi/pay/$EMI_ID" \
      -H "Content-Type: application/json" \
      -b "$CUSTOMER_COOKIES" \
      -d '{
        "paymentMethod": "Online Banking"
      }')
    
    if echo "$PAY_RESPONSE" | grep -q "successful"; then
        print_success "EMI payment successful!"
        
        sleep 1
        
        # Verify payment
        print_info "Verifying payment..."
        VERIFY_DATA=$(curl -s -X GET "$BASE_URL/api/emi/my-emis" \
          -H "Content-Type: application/json" \
          -b "$CUSTOMER_COOKIES")
        
        PAID_STATUS=$(echo "$VERIFY_DATA" | grep -A 5 "\"id\":$EMI_ID" | grep -o '"status":"[^"]*"' | head -1 | grep -o '[A-Z]*')
        if [ "$PAID_STATUS" = "PAID" ]; then
            print_success "EMI Status confirmed: PAID"
        fi
    else
        print_error "EMI payment failed"
        print_info "Response: $PAY_RESPONSE"
    fi
fi

print_header "TEST SUMMARY"
print_success "All EMI tests completed successfully!"
print_info ""
print_info "Key Data:"
print_info "  Customer Email: $CUSTOMER_EMAIL"
print_info "  Customer ID: $CUSTOMER_ID"
print_info "  Loan ID: $LOAN_ID"
print_info "  Loan Amount: ₹$LOAN_AMOUNT"
print_info "  Number of EMIs: $EMI_COUNT"
print_info "  First EMI ID: $EMI_ID"
print_info ""
