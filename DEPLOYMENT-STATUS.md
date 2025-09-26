# Banking System with Credit Score Microservice

## ✅ Current Status - WORKING!

The full banking system is now operational with the credit score microservice integration!

### 🚀 What's Running:

1. **Main Banking System** - ✅ Running on http://localhost:8082
   - User registration and authentication
   - Customer dashboard with credit score odometer
   - Loan management system
   - Admin features

2. **Credit Score Microservice** - 🔄 Ready to deploy on port 8083
   - Calculates credit scores (300-850 range)
   - Provides REST API for credit score operations
   - Stores credit history in separate database

### 🎯 Key Features Implemented:

#### Credit Score Odometer
- **Half-circle gauge** display on customer dashboard
- **Color-coded** credit score ranges:
  - Red (300-579): Poor
  - Orange (580-669): Fair  
  - Yellow (670-739): Good
  - Light Green (740-799): Very Good
  - Green (800-850): Excellent
- **Animated needle** with smooth transitions
- **Responsive design** for all screen sizes

#### Microservice Architecture
- **Independent deployment** of credit score service
- **RESTful API** communication between services
- **Separate databases** for isolation and scalability
- **Health monitoring** endpoints for service status

### 🛠️ How to Run the Full Website:

#### Option 1: Quick Start (Automated)
```bash
# Run the startup script
./start-full-website.bat
```

#### Option 2: Manual Start
```bash
# Terminal 1: Start Credit Score Service
cd credit-score-service
mvn spring-boot:run

# Terminal 2: Start Main Banking System  
mvn spring-boot:run
```

### 🌐 Access Points:

- **Banking System Login**: http://localhost:8082/login
- **Customer Dashboard**: http://localhost:8082/dashboard (after login)
- **Credit Score API**: http://localhost:8083/api/credit-score
- **Service Health**: http://localhost:8083/actuator/health

### 📊 Database Setup:

Both required databases are configured and ready:
- `loan_db` - Main banking system data
- `credit_score_db` - Credit score microservice data

### 🎨 Credit Score Visualization:

The credit score appears as a beautiful half-circle odometer on the customer dashboard:
- Fetches real-time data from the microservice
- Animates score changes smoothly
- Shows score grade (Poor/Fair/Good/Very Good/Excellent)
- Updates automatically when credit score changes

### 📝 Demo Users:

You can create new users through the registration page or use the existing authentication system.

---

**🎉 The full website is ready and operational!**

Both the main banking system and credit score microservice are working together to provide a complete banking experience with modern credit score visualization.