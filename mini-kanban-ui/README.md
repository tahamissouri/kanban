# miniKanban — Angular 17 Frontend

Full project management Kanban board built with Angular 17 standalone components, Angular Signals, and Angular CDK drag-and-drop.

## Prerequisites

| Tool | Version |
|------|---------|
| Node.js | >= 18 |
| npm | >= 9 |
| Angular CLI | >= 17 (`npm install -g @angular/cli`) |
| Backend | Running on `localhost:8080` |

## Quick start

```bash
# Install dependencies
npm install

# Start dev server (proxies /api → localhost:8080)
ng serve

# Open browser
open http://localhost:4200
```

## Run tests

```bash
# All tests with coverage
npm test

# Watch mode
npm run test:watch
```

## Production build

```bash
npm run build:prod
# Output: dist/mini-kanban/browser/
```

## Docker (full stack)

Add this service to your backend's `docker-compose.yaml`:

```yaml
frontend:
  build:
    context: ./mini-kanban
    dockerfile: Dockerfile
  ports:
    - "80:80"
  depends_on:
    - api
```

Then: `docker compose up --build`

## Project structure

```
src/app/
  core/
    models/          TypeScript interfaces for all backend DTOs
    services/        AuthService, BoardService, ColumnService, CardService
    interceptors/    JWT Bearer token injected on every request
    guards/          Auth route guard — redirects to /auth if not logged in
  features/
    auth/            Login + register (tabbed, single component)
    shell/           Topbar + router-outlet wrapper for authenticated pages
    dashboard/       Board grid with create/delete
    board/           Full Kanban: columns, cards, drag-and-drop, members
  shared/
    components/
      toast/         ToastService (signals) + ToastComponent overlay
```

## Architecture decisions

- **Standalone components** throughout — no NgModules anywhere
- **Angular Signals** for local component state — no RxJS BehaviorSubjects for UI state
- **Lazy-loaded routes** — auth, dashboard, and board are all separate chunks
- **Functional interceptor** — `jwtInterceptor` attaches `Authorization: Bearer <token>` to every outgoing request and handles 401 by redirecting to login
- **Functional guard** — `authGuard` checks token expiry (not just presence)
- **Optimistic drag-and-drop** — CDK moves the card in the UI immediately, then persists to backend. If the save fails, a toast shows and the user can retry
- **forkJoin** to load all columns' cards in parallel on board open
