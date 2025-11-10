# 🚢 Cruisely - Enterprise Cruise Management Platform

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]() 
[![Java](https://img.shields.io/badge/Java-11-blue)]()
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue)]()
[![React](https://img.shields.io/badge/React-18+-61DAFB)]()
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)]()

> **A comprehensive, enterprise-grade cruise booking and management platform** built with modern Java EE architecture, featuring advanced authentication, role-based access control, and a responsive React frontend.

## Overview

Cruisely is a full-stack enterprise application designed for cruise line companies to manage their operations efficiently. The platform provides comprehensive booking management, customer relationship tools, and administrative dashboards with multi-language support and robust security features.

### Key Features

- **Advanced Authentication**: JWT-based security with role-based access control (RBAC)
- **Multi-Role Architecture**: Admin, Client, Business Worker, and Moderator interfaces  
- **Cruise Management**: Complete CRUD operations for cruises, companies, and reservations
- **Responsive Design**: Modern React frontend with TypeScript support
- **Internationalization**: Multi-language support (English/Polish)
- **Analytics Dashboard**: Real-time business intelligence and reporting
- **Email Integration**: Automated notifications and verification system
- **CI/CD Ready**: Automated testing and deployment pipelines

---

## Architecture

### Backend Architecture (Java EE)

```
com.cruisely/
├── 🏢 entities/
│   ├── common/          # Shared domain entities & wrappers
│   ├── auth/            # Authentication entities (Account, AccessLevel)
│   └── cruise/          # Business entities (Cruise, Company, Reservation)
├── common/           # Application-wide utilities & DTOs
├── auth/             # Authentication module (business logic)
├── cruise/           # Cruise management module (business logic)
├── controllers/      # REST API controllers
├── services/         # Application services layer
├── security/         # Security configuration & filters
├── config/           # Application configuration
├── exceptions/       # Custom exception handling
├── validators/       # Input validation logic
└── utils/           # Utility classes
```

### Frontend Architecture (React + TypeScript)

```
src/main/ui/src/
├── components/          # Reusable UI components
├── pages/              # Route-based page components
├── layouts/            # Layout wrappers
├── Services/           # API service layer
├── redux/              # State management
├── interfaces/         # TypeScript interfaces
├── styles/             # CSS modules & styling
└── images/             # Static assets
```

### Technology Stack

| Layer | Technology | Version | Purpose |
|-------|-----------|---------|---------|
| **Backend** | Java EE | 8+ | Enterprise application framework |
| **Security** | JWT + Custom Auth | - | Authentication & authorization |
| **Database** | JPA/Hibernate | 5.4+ | Object-relational mapping |
| **Build Tool** | Maven | 3.6+ | Dependency management & builds |
| **Frontend** | React + TypeScript | 18+ | Modern user interface |
| **Styling** | CSS Modules + MUI | - | Component styling |
| **State** | Redux Toolkit | - | Application state management |
| **Server** | Payara/GlassFish | 5+ | Java EE application server |

---

## Quick Start

### Prerequisites

- **Java 11+** (Oracle JDK or OpenJDK)
- **Maven 3.6+**
- **Node.js 16+** & **npm 8+**
- **Payara Server 5+** or **GlassFish 5+**
- **PostgreSQL 12+** (or compatible database)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/cruisely.git
   cd cruisely
   ```

2. **Backend Setup**
   ```bash
   # Install dependencies and compile
   mvn clean compile
   
   # Run tests
   mvn test
   
   # Package for deployment
   mvn clean package -Pprod
   ```

3. **Frontend Setup**
   ```bash
   cd src/main/ui
   npm install
   npm run build
   ```

4. **Database Setup**
   ```bash
   # Execute database initialization scripts
   psql -h localhost -U username -d cruisely_db -f database/db_init.sql
   psql -h localhost -U username -d cruisely_db -f database/db_structure.sql
   ```

### Development Environment

```bash
# Start development with live reload
mvn clean package -Pdev

# Frontend development server
cd src/main/ui && npm start

# Backend development server
# Deploy the generated WAR to your application server
```

### Production Deployment

```bash
# Build production package
mvn clean package -Pprod

# Deploy to Payara/GlassFish
asadmin deploy target/cruisely.war
```

---

## API Documentation

### Authentication Endpoints

| Method | Endpoint | Description | Role Required |
|--------|----------|-------------|---------------|
| `POST` | `/api/auth/login` | User authentication | - |
| `POST` | `/api/auth/register` | User registration | - |
| `POST` | `/api/auth/refresh` | Token refresh | Authenticated |
| `POST` | `/api/auth/logout` | User logout | Authenticated |

### Cruise Management

| Method | Endpoint | Description | Role Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/cruises` | List all cruises | Client+ |
| `POST` | `/api/cruises` | Create new cruise | Business Worker+ |
| `PUT` | `/api/cruises/{id}` | Update cruise | Business Worker+ |
| `DELETE` | `/api/cruises/{id}` | Delete cruise | Admin |
| `GET` | `/api/cruises/{id}/reservations` | Get cruise reservations | Business Worker+ |

### User Management

| Method | Endpoint | Description | Role Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/accounts` | List user accounts | Moderator+ |
| `PUT` | `/api/accounts/{id}/block` | Block user account | Moderator+ |
| `PUT` | `/api/accounts/{id}/access-level` | Grant access level | Admin |
| `GET` | `/api/accounts/profile` | Get user profile | Authenticated |

---

## Security Features

### Authentication & Authorisation

- **JWT Token-based Authentication**: Secure stateless authentication
- **Role-based Access Control (RBAC)**: Fine-grained permissions system
- **Account Verification**: Email-based account activation
- **Password Security**: Bcrypt hashing with salt
- **Session Management**: Configurable token expiration

### Access Levels

| Role | Permissions | Description |
|------|-------------|-------------|
| **Admin** | Full system access | System administration and user management |
| **Moderator** | User moderation | Account management and content moderation |
| **Business Worker** | Business operations | Cruise and reservation management |
| **Client** | Customer features | Booking and profile management |

### Security Headers & Filters

- **CORS Configuration**: Cross-origin resource sharing
- **Input Validation**: Comprehensive data validation
- **XSS Protection**: Cross-site scripting prevention
- **Rate Limiting**: API request throttling
- **Audit Logging**: Security event tracking

---

## Testing

### Backend Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Generate test coverage report
mvn jacoco:report
```

### Frontend Testing

```bash
cd src/main/ui

# Run unit tests
npm test

# Run e2e tests
npm run test:e2e

# Generate coverage report
npm run test:coverage
```

### Test Structure

```
src/test/java/
├── unit/                # Unit tests
├── integration/         # Integration tests
├── fixtures/           # Test data fixtures
└── utils/              # Testing utilities
```

---

## 📊 Database Schema

### Core Entities

```sql
-- User Management
Account (id, login, email, password_hash, language_type, dark_mode, ...)
AccessLevel (id, account_id, level_type, enabled, ...)
Address (id, country, city, street, house_number, apartment_number, postal_code)

-- Cruise Management  
Company (id, name, nip, phone_number, address_id, ...)
CruiseGroup (id, name, company_id, number_of_cruises, ...)
Cruise (id, cruise_group_id, name, start_date, end_date, price, ...)
Reservation (id, cruise_id, client_id, number_of_people, price, ...)

-- System
AlterType (id, type) -- Audit trail
TokenWrapper (id, token, used, account_id, ...)
Rating (id, cruise_id, client_id, rating, comment, ...)
```

### Relationships

- **Account** → **AccessLevel** (One-to-Many)
- **Company** → **CruiseGroup** (One-to-Many)  
- **CruiseGroup** → **Cruise** (One-to-Many)
- **Cruise** → **Reservation** (One-to-Many)
- **Client** → **Rating** (One-to-Many)

---

## 🌐 Configuration

### Environment Variables

```bash
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/cruisely_db
DB_USERNAME=cruisely_user  
DB_PASSWORD=secure_password

# JWT Configuration
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000

# Email Configuration
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
EMAIL_USERNAME=noreply@cruisely.com
EMAIL_PASSWORD=app_password

# Application Configuration
APP_BASE_URL=https://localhost:8181
FRONTEND_URL=http://localhost:3000
```

### Maven Profiles

- **`dev`**: Development with UI build
- **`dev-without-ui`**: Development backend only  
- **`prod`**: Production build with optimizations

---

## Monitoring & Observability

### Health Checks

- **Application Health**: `/health`
- **Database Connection**: `/health/db`
- **External Services**: `/health/external`

### Metrics & Logging

- **Application Metrics**: Custom business metrics
- **Performance Monitoring**: Response time tracking
- **Error Logging**: Structured logging with levels
- **Audit Trail**: User action tracking

---

### Development Workflow

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Code Standards

- **Java**: Google Java Style Guide
- **TypeScript/React**: ESLint + Prettier configuration
- **Git**: Conventional Commits specification
- **Testing**: Minimum 80% code coverage

---

## Changelog

### v1.0.0 (Current)
- ✅ Complete package refactoring from academic to enterprise structure
- ✅ English documentation and internationalization
- ✅ Modern authentication with JWT
- ✅ Role-based access control system
- ✅ Responsive React frontend with TypeScript
- ✅ Comprehensive test coverage
- ✅ Production-ready deployment configuration

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**Built with love for the cruise industry**

[🌟 Give a star](https://github.com/reignu/cruisely) • [🐛 Report Bug](ungier.adam@gmail.com)

</div>
