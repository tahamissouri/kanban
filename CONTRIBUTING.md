# Contributing to miniKanban

Thank you for your interest in contributing to miniKanban! This document provides guidelines and instructions for contributing.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Commit Messages](#commit-messages)
- [Pull Request Process](#pull-request-process)
- [Issue Reporting](#issue-reporting)

## Code of Conduct

### Our Pledge

We are committed to providing a welcoming and inspiring community for all. Please be respectful and constructive in your interactions.

### Expected Behavior

- Use welcoming and inclusive language
- Be respectful of differing viewpoints
- Accept constructive criticism gracefully
- Focus on what is best for the community
- Show empathy towards other community members

## Getting Started

### Prerequisites

- Java 17 or higher
- Node.js 20 or higher
- Docker and Docker Compose
- Git
- Maven 3.8+
- PostgreSQL 15 (for local development)

### Fork and Clone

1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/miniKanban.git
   cd miniKanban
   ```

3. Add upstream remote:
   ```bash
   git remote add upstream https://github.com/ORIGINAL_OWNER/miniKanban.git
   ```

### Local Setup

1. **Start development database:**
   ```bash
   docker-compose -f docker-compose.dev.yaml up -d postgres
   ```

2. **Backend setup:**
   ```bash
   cd miniKanban
   cp ../.env.example ../.env
   # Edit .env with your configuration
   mvn clean install
   cd api
   mvn spring-boot:run
   ```

3. **Frontend setup:**
   ```bash
   cd mini-kanban-ui
   npm install
   npm start
   ```

## Development Workflow

### Branch Strategy

- `main` - Production-ready code
- `develop` - Integration branch for features
- `feature/*` - New features
- `bugfix/*` - Bug fixes
- `hotfix/*` - Urgent production fixes

### Creating a Feature Branch

```bash
git checkout develop
git pull upstream develop
git checkout -b feature/your-feature-name
```

### Keeping Your Branch Updated

```bash
git checkout develop
git pull upstream develop
git checkout feature/your-feature-name
git rebase develop
```

## Coding Standards

### Backend (Java/Spring Boot)

#### Code Style

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use 4 spaces for indentation
- Maximum line length: 120 characters
- Use meaningful variable and method names

#### Best Practices

```java
// Good
public class BoardService {
    private final BoardRepository boardRepository;
    
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }
    
    public BoardResponseDto createBoard(CreateBoardRequestDto dto, Long ownerId) {
        // Implementation
    }
}

// Bad
public class BoardService {
    @Autowired
    private BoardRepository repo;
    
    public BoardResponseDto create(CreateBoardRequestDto d, Long id) {
        // Implementation
    }
}
```

#### Naming Conventions

- Classes: `PascalCase` (e.g., `BoardService`)
- Methods: `camelCase` (e.g., `createBoard`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_ATTEMPTS`)
- Packages: `lowercase` (e.g., `com.example.service`)

#### Documentation

- Add JavaDoc for public APIs
- Include `@param`, `@return`, and `@throws` tags
- Explain complex logic with inline comments

```java
/**
 * Creates a new board with the specified owner.
 *
 * @param dto the board creation request
 * @param ownerId the ID of the board owner
 * @return the created board response
 * @throws AppException if the owner is not found
 */
public BoardResponseDto createBoard(CreateBoardRequestDto dto, Long ownerId) {
    // Implementation
}
```

### Frontend (Angular/TypeScript)

#### Code Style

- Follow [Angular Style Guide](https://angular.io/guide/styleguide)
- Use 2 spaces for indentation
- Maximum line length: 120 characters
- Use TypeScript strict mode

#### Component Structure

```typescript
// Good
@Component({
  selector: 'app-board',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.scss']
})
export class BoardComponent implements OnInit {
  board = signal<Board | null>(null);
  loading = signal(false);

  constructor(
    private boardService: BoardService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loadBoard();
  }

  private loadBoard(): void {
    // Implementation
  }
}
```

#### Naming Conventions

- Components: `PascalCase` with `Component` suffix
- Services: `PascalCase` with `Service` suffix
- Interfaces: `PascalCase` (e.g., `Board`, `User`)
- Methods: `camelCase`
- Constants: `UPPER_SNAKE_CASE`

## Testing Guidelines

### Backend Testing

#### Unit Tests

```java
@Test
void shouldCreateBoard() {
    // Given
    CreateBoardRequestDto dto = new CreateBoardRequestDto("Test Board");
    Long ownerId = 1L;
    
    // When
    BoardResponseDto result = boardService.createBoard(dto, ownerId);
    
    // Then
    assertNotNull(result);
    assertEquals("Test Board", result.name());
}
```

#### Integration Tests

```java
@SpringBootTest
@AutoConfigureMockMvc
class BoardControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldCreateBoardViaApi() throws Exception {
        mockMvc.perform(post("/api/v1/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test Board\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Board"));
    }
}
```

### Frontend Testing

#### Component Tests

```typescript
describe('BoardComponent', () => {
  let component: BoardComponent;
  let fixture: ComponentFixture<BoardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BoardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(BoardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load board on init', () => {
    component.ngOnInit();
    expect(component.loading()).toBe(true);
  });
});
```

### Test Coverage Requirements

- Backend: Minimum 80% coverage
- Frontend: Minimum 70% coverage
- All new features must include tests
- Bug fixes should include regression tests

## Commit Messages

### Format

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

### Examples

```
feat(board): add drag and drop support

Implement drag and drop functionality for cards using Angular CDK.
Cards can now be moved between columns with visual feedback.

Closes #123
```

```
fix(auth): resolve token expiration issue

Fixed bug where refresh token was not being used correctly,
causing users to be logged out prematurely.

Fixes #456
```

## Pull Request Process

### Before Submitting

1. **Update your branch:**
   ```bash
   git checkout develop
   git pull upstream develop
   git checkout your-branch
   git rebase develop
   ```

2. **Run tests:**
   ```bash
   # Backend
   cd miniKanban && mvn test
   
   # Frontend
   cd mini-kanban-ui && npm test
   ```

3. **Check code style:**
   ```bash
   # Backend
   mvn checkstyle:check
   
   # Frontend
   npm run lint
   ```

### PR Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No new warnings generated
- [ ] Tests pass locally
```

### Review Process

1. At least one approval required
2. All CI checks must pass
3. No merge conflicts
4. Code coverage maintained or improved

## Issue Reporting

### Bug Reports

Use the bug report template:

```markdown
**Describe the bug**
Clear description of the bug

**To Reproduce**
Steps to reproduce:
1. Go to '...'
2. Click on '...'
3. See error

**Expected behavior**
What should happen

**Screenshots**
If applicable

**Environment:**
- OS: [e.g., Windows 11]
- Browser: [e.g., Chrome 120]
- Version: [e.g., 1.0.0]

**Additional context**
Any other relevant information
```

### Feature Requests

```markdown
**Is your feature request related to a problem?**
Description of the problem

**Describe the solution you'd like**
Clear description of desired functionality

**Describe alternatives you've considered**
Other solutions considered

**Additional context**
Mockups, examples, etc.
```

## Questions?

- 💬 Join our [Discord](https://discord.gg/minikanban)
- 📧 Email: dev@minikanban.com
- 📖 Check the [documentation](https://docs.minikanban.com)

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to miniKanban! 🎉