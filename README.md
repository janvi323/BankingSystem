# DebtHues - Banking System

A comprehensive banking and loan management system built with Spring Boot, featuring role-based authentication and modern web interface.

## 🌟 Features

- **User Authentication & Authorization**
  - Role-based access control (Customer/Admin)
  - Secure login/registration system
  - JWT token-based authentication

- **Customer Management**
  - Customer registration and profile management
  - View customer details and credit history
  - Admin controls for customer oversight

- **Loan Management**
  - Loan application system
  - Credit score evaluation
  - Loan approval workflow (Admin only)
  - Loan status tracking

- **Modern UI/UX**
  - Responsive design with yellow/ivory theme
  - Clean and intuitive interface
  - Mobile-friendly layout

## 🛠️ Technology Stack

- **Backend**: Spring Boot, Spring Security, Spring Data JPA
- **Frontend**: JSP, HTML5, CSS3, JavaScript
- **Database**: MySQL/H2 (configurable)
- **Build Tool**: Maven
- **Java Version**: 11+

## 📋 Prerequisites

- Java 11 or higher
- Maven 3.6+
- MySQL 8.0+ (or H2 for development)
- Git

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone <your-repository-url>
cd BankingSystem
```

### 2. Database Configuration
Create a file `application.properties` in `src/main/resources/` with your database configuration:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/banking_system
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Security Configuration
app.jwt.secret=your_jwt_secret_key
app.jwt.expiration=86400000

# Server Configuration
server.port=8080
```

### 3. Build and Run
```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

### 4. Access the Application
- Open your browser and navigate to: `http://localhost:8080`
- Register a new account or login with existing credentials

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/bankingsystem/bankingsystem/
│   │   ├── config/           # Security & App configuration
│   │   ├── controller/       # REST & Web controllers
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Data access layer
│   │   └── Service/         # Business logic
│   ├── resources/
│   │   └── application.properties  # App configuration
│   └── webapp/WEB-INF/views/       # JSP templates
└── test/                    # Unit tests
```

## 🔐 User Roles

### Customer
- Register and login
- Apply for loans
- View loan status
- Check credit score
- Update profile

### Admin
- All customer privileges
- Approve/reject loans
- View all customers
- Manage loan applications
- System administration

## 🌐 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Loans
- `POST /api/loans/apply` - Apply for loan (Customer)
- `GET /api/loans/my-loans` - View own loans (Customer)
- `PUT /api/loans/{id}/status` - Approve/reject loan (Admin)

### Customers
- `GET /api/customers` - View all customers (Admin)
- `GET /api/customers/{id}` - Get customer details

## 🎨 UI Theme

The application features a modern **DebtHues** theme with:
- **Primary Color**: Yellow (#ffc107)
- **Background**: Ivory (#f5f5dc)
- **Text**: Black (#000000)
- **Responsive Design**: Works on all devices

## 🔧 Development Setup

### Environment Variables
Instead of hardcoding sensitive data, use environment variables:

```bash
export DB_URL=jdbc:mysql://localhost:3306/banking_system
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your_secret_key
```

### Database Setup (MySQL)
```sql
CREATE DATABASE banking_system;
CREATE USER 'banking_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON banking_system.* TO 'banking_user'@'localhost';
FLUSH PRIVILEGES;
```

## 🧪 Testing

Run tests with Maven:
```bash
mvn test
```

## 📝 Configuration Files

### Important Files Not in Git
- `application.properties` - Contains sensitive database and security configurations
- `target/` - Build artifacts
- IDE-specific files (`.idea/`, `.vscode/`, etc.)

### Sample application.properties
A sample configuration file is available as `application.properties.example`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/new-feature`
3. Commit changes: `git commit -m 'Add some feature'`
4. Push to branch: `git push origin feature/new-feature`
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Team

- **Project Name**: DebtHues
- **Version**: 1.0.0
- **Last Updated**: September 2025

## 🆘 Support

If you encounter any issues:
1. Check the logs in `target/` directory
2. Verify database connection
3. Ensure all dependencies are installed
4. Check port availability (default: 8080)

## 🔮 Future Enhancements

- [ ] Email notifications for loan status
- [ ] Credit score calculation algorithms
- [ ] Payment gateway integration
- [ ] Advanced reporting dashboard
- [ ] Mobile application
- [ ] API documentation with Swagger

---

**DebtHues** - Simplifying debt and loan management with modern technology.
