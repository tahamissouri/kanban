# 📋 miniKanban

A modern, full-stack Kanban board application built with Spring Boot and Angular. Manage your projects with an intuitive drag-and-drop interface, real-time collaboration, and enterprise-grade security.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-brightgreen)
![Angular](https://img.shields.io/badge/Angular-17-red)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)

## 🌟 Features

### Core Functionality
- ✅ **Board Management**: Create and manage multiple Kanban boards
- ✅ **Drag & Drop**: Intuitive card movement between columns
- ✅ **Collaboration**: Share boards with team members
- ✅ **User Authentication**: Secure JWT-based authentication
- ✅ **Responsive Design**: Works seamlessly on desktop and mobile

### Technical Highlights
- 🏗️ **Clean Architecture**: Multi-module Maven structure with clear separation of concerns
- 🔒 **Security**: JWT authentication with refresh tokens, role-based access control
- 🐳 **Containerized**: Full Docker support for easy deployment
- 📊 **Database Migrations**: Flyway for version-controlled schema changes
- 🧪 **Tested**: Comprehensive test coverage (backend & frontend)

## 🏗️ Architecture

```
miniKanban/
├── api/              # REST API layer (Controllers, Security)
├── business/         # Business logic layer (Services)
├── persistence/      # Data access layer (Entities, Repositories)
├── commons/          # Shared DTOs, utilities, exceptions
└── mini-kanban-ui/   # Angular frontend application
```

### Technology Stack

#### Backend
- **Framework**: Spring Boot 3.3.2
- **Language**: Java 17
- **Database**: PostgreSQL 15
- **ORM**: JPA/Hibernate
- **Migration**: Flyway
- **Security**: Spring Security + JWT
- **Build Tool**: Maven
- **Mapping**: MapStruct
- **Utilities**: Lombok

#### Frontend
- **Framework**: Angular 17
- **Language**: TypeScript 5.4
- **State Management**: Angular Signals
- **UI Components**: Angular CDK (Drag & Drop)
- **HTTP Client**: Angular HttpClient
- **Testing**: Jest
- **Build Tool**: Angular CLI

## 🚀 Quick Start

### Prerequisites

- **Java 17** or higher
- **Node.js 20** or higher
- **Docker & Docker Compose**
- **PostgreSQL 15** (if running without Docker)
- **Maven 3.8+** (if building without Docker)

### Option 1: Docker (Recommended)

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/miniKanban.git
   cd miniKanban
   ```

2. **Set up environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

3. **Start all services**
   ```bash
   docker-compose up -d
   ```

4. **Access the application**
   - Frontend: http://localhost:80
   - Backend API: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html

### Option 2: Local Development

#### Backend Setup

1. **Start PostgreSQL**
   ```bash
   docker run -d \
     --name miniKanban-postgres \
     -e POSTGRES_DB=minikanban \
     -e POSTGRES_USER=admin \
     -e POSTGRES_PASSWORD=admin \
     -p 5432:5432 \
     postgres:15
   ```

2. **Configure environment variables**
   ```bash
   export JWT_SECRET=your-secret-key-min-256-bits
   export JWT_EXPIRY_MS=900000
   export JWT_REFRESH_EXPIRY_DAYS=7
   export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/minikanban
   export SPRING_DATASOURCE_USERNAME=admin
   export SPRING_DATASOURCE_PASSWORD=admin
   ```

3. **Build and run backend**
   ```bash
   cd miniKanban
   mvn clean install
   cd api
   mvn spring-boot:run
   ```

#### Frontend Setup

1. **Install dependencies**
   ```bash
   cd mini-kanban-ui
   npm install
   ```

2. **Configure API endpoint**
   ```bash
   # Edit src/environments/environment.ts
   # Set apiUrl to http://localhost:8080
   ```

3. **Start development server**
   ```bash
   npm start
   ```

4. **Access the application**
   - Frontend: http://localhost:4200

## 📝 Configuration

### Environment Variables

#### Backend (miniKanban/api)

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `JWT_SECRET` | Secret key for JWT signing (min 256 bits) | - | ✅ |
| `JWT_EXPIRY_MS` | Access token expiry in milliseconds | 900000 (15 min) | ❌ |
| `JWT_REFRESH_EXPIRY_DAYS` | Refresh token expiry in days | 7 | ❌ |
| `SPRING_DATASOURCE_URL` | PostgreSQL connection URL | - | ✅ |
| `SPRING_DATASOURCE_USERNAME` | Database username | - | ✅ |
| `SPRING_DATASOURCE_PASSWORD` | Database password | - | ✅ |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | dev | ❌ |

#### Frontend (mini-kanban-ui)

| Variable | Description | Default |
|----------|-------------|---------|
| `API_URL` | Backend API base URL | http://localhost:8080 |

### Application Profiles

- **dev**: Development profile with debug logging
- **test**: Testing profile with H2 in-memory database
- **docker**: Docker deployment profile
- **prod**: Production profile with optimized settings

## 🧪 Testing

### Backend Tests

```bash
cd miniKanban
mvn test                    # Run all tests
mvn test -Dtest=ClassName   # Run specific test class
mvn verify                  # Run tests + integration tests
```

### Frontend Tests

```bash
cd mini-kanban-ui
npm test                    # Run tests
npm run test:watch          # Run tests in watch mode
npm run test:coverage       # Generate coverage report
```

## 📚 API Documentation

Once the backend is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Key Endpoints

#### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login and get JWT token
- `POST /api/v1/auth/refresh` - Refresh access token

#### Boards
- `GET /api/v1/boards` - Get all user's boards
- `POST /api/v1/boards` - Create new board
- `GET /api/v1/boards/{id}` - Get board details
- `PUT /api/v1/boards/{id}` - Update board
- `DELETE /api/v1/boards/{id}` - Delete board
- `POST /api/v1/boards/{id}/members` - Add member to board

#### Columns
- `GET /api/v1/boards/{boardId}/columns` - Get board columns
- `POST /api/v1/boards/{boardId}/columns` - Create column
- `PUT /api/v1/boards/{boardId}/columns/{id}` - Update column
- `DELETE /api/v1/boards/{boardId}/columns/{id}` - Delete column

#### Cards
- `GET /api/v1/boards/{boardId}/columns/{columnId}/cards` - Get column cards
- `POST /api/v1/boards/{boardId}/columns/{columnId}/cards` - Create card
- `PUT /api/v1/boards/{boardId}/columns/{columnId}/cards/{id}` - Update card
- `DELETE /api/v1/boards/{boardId}/columns/{columnId}/cards/{id}` - Delete card

## 🔒 Security

### Authentication Flow

1. User registers or logs in with credentials
2. Server validates and returns JWT access token + refresh token
3. Client stores tokens securely
4. Client includes access token in Authorization header for API requests
5. When access token expires, client uses refresh token to get new access token

### Security Features

- ✅ Password hashing with BCrypt
- ✅ JWT-based stateless authentication
- ✅ Refresh token rotation
- ✅ Role-based access control (Owner/Member)
- ✅ CORS configuration
- ✅ SQL injection prevention
- ✅ XSS protection

## 🐳 Docker Deployment

### Production Deployment

1. **Build images**
   ```bash
   docker-compose -f docker-compose.prod.yaml build
   ```

2. **Start services**
   ```bash
   docker-compose -f docker-compose.prod.yaml up -d
   ```

3. **View logs**
   ```bash
   docker-compose logs -f
   ```

4. **Stop services**
   ```bash
   docker-compose down
   ```

### Docker Compose Services

- **postgres**: PostgreSQL database
- **backend**: Spring Boot API
- **frontend**: Angular application (Nginx)

## 📊 Database Schema

```sql
users
├── id (PK)
├── user_name (UNIQUE)
├── email (UNIQUE)
├── password
└── created_at

board
├── id (PK)
├── name
├── created_at
└── owner_id (FK -> users)

board_members (Many-to-Many)
├── id_board (FK -> board)
└── id_member (FK -> users)

columnn
├── id (PK)
├── name
├── position
└── board_id (FK -> board)

card
├── id (PK)
├── title
├── description
├── position
└── column_id (FK -> columnn)
```

## 🛠️ Development

### Project Structure

```
miniKanban/
├── api/
│   └── src/main/java/com/example/api/v1/
│       ├── controller/      # REST controllers
│       ├── security/        # Security configuration
│       ├── config/          # Application configuration
│       └── advice/          # Global exception handling
├── business/
│   └── src/main/java/com/example/
│       ├── service/         # Business logic
│       └── mappers/         # DTO mappers
├── persistence/
│   └── src/main/java/com/example/persistence/
│       ├── entity/          # JPA entities
│       └── repository/      # Spring Data repositories
├── commons/
│   └── src/main/java/com/example/
│       ├── dtos/            # Data transfer objects
│       ├── exception/       # Custom exceptions
│       └── util/            # Utility classes
└── mini-kanban-ui/
    └── src/app/
        ├── core/            # Core services, guards, interceptors
        ├── features/        # Feature modules
        └── shared/          # Shared components
```

### Code Style

#### Backend
- Follow Java naming conventions
- Use Lombok to reduce boilerplate
- Write meaningful commit messages
- Add JavaDoc for public APIs
- Keep methods small and focused

#### Frontend
- Follow Angular style guide
- Use TypeScript strict mode
- Implement reactive patterns with RxJS
- Use Angular signals for state management
- Write unit tests for components and services

### Git Workflow

1. Create feature branch from `main`
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. Make changes and commit
   ```bash
   git add .
   git commit -m "feat: add new feature"
   ```

3. Push and create pull request
   ```bash
   git push origin feature/your-feature-name
   ```

## 🤝 Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details.

### Development Setup

1. Fork the repository
2. Clone your fork
3. Create a feature branch
4. Make your changes
5. Write/update tests
6. Submit a pull request

## 📈 Roadmap

See [ROADMAP.md](ROADMAP.md) for detailed improvement plans and future features.

### Upcoming Features
- 🔄 Real-time collaboration with WebSocket
- 🔍 Advanced search and filtering
- 📊 Analytics and reporting
- 🏷️ Card labels and tags
- 📎 File attachments
- 💬 Card comments
- 📧 Email notifications
- 📱 Mobile app

## 🐛 Troubleshooting

### Common Issues

**Issue**: Database connection failed
```bash
# Solution: Check PostgreSQL is running
docker ps | grep postgres
# Verify connection string in application.yaml
```

**Issue**: JWT token expired
```bash
# Solution: Use refresh token endpoint to get new access token
POST /api/v1/auth/refresh
```

**Issue**: CORS errors in browser
```bash
# Solution: Check CORS configuration in CorsConfig.java
# Ensure frontend URL is in allowed origins
```

**Issue**: Port already in use
```bash
# Solution: Change port in application.yaml (backend) or angular.json (frontend)
# Or stop the process using the port
lsof -ti:8080 | xargs kill -9  # macOS/Linux
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Your Name** - *Initial work* - [YourGitHub](https://github.com/yourusername)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Angular team for the modern frontend framework
- All contributors who help improve this project

## 📞 Support

- 📧 Email: support@minikanban.com
- 💬 Discord: [Join our community](https://discord.gg/minikanban)
- 🐛 Issues: [GitHub Issues](https://github.com/yourusername/miniKanban/issues)

---

**Built with ❤️ using Spring Boot and Angular**