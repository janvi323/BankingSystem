# 🔐 EMI & OAuth2 Configuration Guide

## Current Status: ✅ READY TO DEPLOY

All features implemented and ready for Google OAuth credentials.

---

## Step 1: Create .env File

**File Location:** Root of project (same directory as `pom.xml`)

**Create from template:**
```bash
cp .env.template .env
```

**Or create manually:**
```bash
# File: .env
GOOGLE_CLIENT_ID=YOUR_CLIENT_ID_HERE.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=YOUR_CLIENT_SECRET_HERE
APPLICATION_REDIRECT_URI=http://localhost:8082/login/oauth2/code/google
```

---

## Step 2: Get Google OAuth Credentials

### 2.1 Go to Google Cloud Console
- Visit: https://console.cloud.google.com
- Sign in with your Google account

### 2.2 Create a New Project
- Click project dropdown (top left)
- Click "NEW PROJECT"
- Project name: `Banking System OAuth`
- Click "CREATE"

### 2.3 Enable Google+ API
- Left menu → **APIs & Services** → **Library**
- Search: "Google+ API"
- Click on result
- Click "ENABLE"

### 2.4 Create OAuth2 Credentials
1. Left menu → **APIs & Services** → **OAuth consent screen**
2. User type: **External** → **CREATE**
3. Fill in:
   - App name: `Banking System`
   - User support email: your-email@gmail.com
   - Developer contact: your-email@gmail.com
4. **SAVE AND CONTINUE** through all pages

5. Go to **APIs & Services** → **Credentials**
6. **+ CREATE CREDENTIALS** → **OAuth client ID**
7. Application type: **Web application**
8. Name: `Banking System`
9. **Authorized JavaScript origins:**
   - Add URI: `http://localhost:8082`
10. **Authorized redirect URIs:**
    - Add URI: `http://localhost:8082/login/oauth2/code/google`
11. **CREATE**
12. Copy **Client ID** and **Client Secret**

---

## Step 3: Fill in .env File

```bash
# Open .env and update:
GOOGLE_CLIENT_ID=123456789-abcdefghijk.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=GOCSPX-aBcDeFgHiJkLmNoPqRsT
APPLICATION_REDIRECT_URI=http://localhost:8082/login/oauth2/code/google
```

**Save the file.**

---

## Step 4: Run Application

### Option A: Maven Command
```bash
cd /path/to/BankingSystem
mvn clean install -DskipTests
mvn spring-boot:run
```

### Option B: IDE
- Right-click project → **Run As** → **Spring Boot App**

### Option C: Docker
```bash
docker build -t banking-system .
docker run -p 8082:8082 --env-file .env banking-system
```

---

## Step 5: Test the Application

### 5.1 Open Application
```
http://localhost:8082
```

### 5.2 Login with Google
- Click "Sign in with Google"
- Enter your Google email
- Grant permissions
- **Automatically redirected to dashboard**

### 5.3 Apply for Loan
1. Click "Apply Loan"
2. Enter:
   - Amount: 100,000
   - Purpose: Home Purchase
   - Tenure: 12 months
3. See **real-time EMI calculation**
4. Click "Submit"
5. **Success modal shows:**
   - Monthly EMI: ₹8,645.88
   - Total EMIs: 12
   - Total Interest: ₹3,750.56
   - Total Amount: ₹103,750.56

### 5.4 Test Full EMI Flow (Optional)
```bash
# From project root
bash test-emi-flow.sh
```

This will:
- ✅ Register new customer
- ✅ Apply for loan
- ✅ Login as admin
- ✅ Approve loan
- ✅ Generate EMIs
- ✅ Pay first EMI
- ✅ Verify payment

---

## 📊 EMI Calculation Examples

### Example 1: Home Purchase
- Amount: ₹1,00,000
- Tenure: 12 months  
- Interest Rate: 8.5%
- **Monthly EMI: ₹8,645.88**
- **Total Interest: ₹3,750.56**

### Example 2: Auto Loan
- Amount: ₹5,00,000
- Tenure: 60 months
- Interest Rate: 9.5%
- **Monthly EMI: ₹9,966.44**
- **Total Interest: ₹98,785.90**

### Example 3: Business Loan
- Amount: ₹10,00,000
- Tenure: 24 months
- Interest Rate: 11%
- **Monthly EMI: ₹46,736.92**
- **Total Interest: ₹1,12,085.91**

---

## 🎯 User Flow Diagram

```
Google OAuth Login
       ↓
Auto Account Creation
       ↓
Dashboard
       ↓
Apply for Loan
       ↓
See Real-time EMI Calculation
       ↓
Submit Application
       ↓
Success: View EMI Details
       ↓
[PENDING APPROVAL]
       ↓
Admin Approves
       ↓
EMIs Generated
       ↓
Customer Pays EMI
       ↓
Payment Confirmed ✅
```

---

## 🔄 API Endpoints for EMI

### Get My EMIs
```
GET /api/emi/my-emis
Headers: Session with loggedInCustomer
Response: List of EMI records
```

### Pay EMI
```
POST /api/emi/pay/{emiId}
Body: { "paymentMethod": "Online Banking" }
Response: Payment confirmation
```

### EMI Statistics
```
GET /api/emi/stats
Response: Stats like total pending, paid, overdue
```

---

## 🚀 Deployment Checklist

- [ ] Google OAuth credentials obtained
- [ ] .env file created with credentials
- [ ] .env added to .gitignore
- [ ] Application built successfully
- [ ] Local testing completed
- [ ] EMI flow tested end-to-end
- [ ] Admin functions tested
- [ ] Payment flow verified

---

## 🔐 Production Checklist

- [ ] Update .env with production Google OAuth credentials
- [ ] Update `APPLICATION_REDIRECT_URI` to production domain
- [ ] Enable HTTPS
- [ ] Use environment variables instead of .env file
- [ ] Configure database for production
- [ ] Set up SSL certificates
- [ ] Test OAuth with production URLs
- [ ] Review security settings

---

## 📝 Sample Loan Purposes (with default rates)

| Purpose | Interest Rate | Tenure Range |
|---------|--------------|--------------|
| Home Purchase | 8.5% | 60-240 months |
| Auto Loan | 9.5% | 24-84 months |
| Business | 11% | 12-60 months |
| Personal | 13% | 12-60 months |
| Education | 7.5% | 84-120 months |

---

## 💾 Database Schema for EMI

```sql
-- EMI Table Structure
CREATE TABLE emi (
    id BIGSERIAL PRIMARY KEY,
    loan_id BIGINT REFERENCES loan(id),
    emi_number INTEGER,
    due_date DATE,
    amount DECIMAL(10,2),
    status VARCHAR(20), -- PENDING, PAID, OVERDUE
    payment_date TIMESTAMP,
    payment_method VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for performance
CREATE INDEX idx_emi_loan_id ON emi(loan_id);
CREATE INDEX idx_emi_status ON emi(status);
CREATE INDEX idx_emi_due_date ON emi(due_date);
```

---

## 🆘 Common Issues & Solutions

### Issue: "Invalid Client ID"
**Solution:** 
- Check .env file has correct Client ID
- No extra spaces in .env
- Restart application after updating .env

### Issue: "Redirect URI mismatch"
**Solution:**
- Ensure Google Cloud Console has exact URI: `http://localhost:8082/login/oauth2/code/google`
- Check for trailing slashes or extra characters

### Issue: "EMI details not showing"
**Solution:**
- Check browser console for errors
- Verify loan calculation service is running
- Ensure tenure and amount are valid

### Issue: ".env file not loading"
**Solution:**
- File must be in project root (same as pom.xml)
- Restart Spring Boot application
- Check for permission issues on .env file

---

## 📞 Support Resources

- **Google OAuth Documentation:** https://developers.google.com/identity/protocols/oauth2
- **Spring Security OAuth2:** https://spring.io/projects/spring-security-oauth2  
- **Application Documentation:** See IMPLEMENTATION_SUMMARY.md
- **Setup Guide:** See GOOGLE_OAUTH_SETUP.md
- **Quick Reference:** See QUICK_START.md

---

**Everything is ready! Add your Google OAuth credentials to `.env` and start using the Banking System!** 🎉
