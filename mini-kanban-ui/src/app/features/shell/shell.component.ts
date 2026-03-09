import { Component } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="app-shell">
      <nav class="topbar">
        <span class="topbar-logo font-serif" (click)="goHome()" role="button" tabindex="0">
          mk<em> /</em>
        </span>
        <span class="topbar-spacer"></span>
        <span class="topbar-email font-mono">{{ auth.currentEmail() }}</span>
        <div class="avatar" aria-hidden="true">{{ initials() }}</div>
        <button class="btn btn-ghost topbar-signout" (click)="auth.logout()">
          Sign out
        </button>
      </nav>
      <main class="shell-main">
        <router-outlet/>
      </main>
    </div>
  `,
  styles: [`
    .app-shell   { height: 100vh; display: flex; flex-direction: column; }
    .topbar {
      height: 52px;
      background: var(--surface);
      border-bottom: 1px solid var(--border);
      display: flex;
      align-items: center;
      padding: 0 20px;
      gap: 14px;
      flex-shrink: 0;
      z-index: 100;
    }
    .topbar-logo {
      font-size: 20px;
      color: var(--accent);
      cursor: pointer;
      flex-shrink: 0;
      user-select: none;
      em { color: var(--text3); font-style: italic; }
    }
    .topbar-spacer  { flex: 1; }
    .topbar-email   { font-size: 12px; color: var(--text3); }
    .topbar-signout { font-size: 12px; padding: 5px 12px; }
    .shell-main     { flex: 1; overflow: hidden; display: flex; }
  `],
})
export class ShellComponent {
  constructor(public auth: AuthService, private router: Router) {}

  goHome(): void { this.router.navigate(['/']); }

  initials(): string {
    return (this.auth.currentEmail() ?? '??').slice(0, 2).toUpperCase();
  }
}
