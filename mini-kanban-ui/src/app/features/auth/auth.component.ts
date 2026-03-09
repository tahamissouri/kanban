import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

type AuthMode = 'login' | 'register';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss'],
})
export class AuthComponent {
  mode    = signal<AuthMode>('login');
  loading = signal(false);
  error   = signal<string | null>(null);

  loginEmail    = '';
  loginPassword = '';

  regUsername = '';
  regEmail    = '';
  regPassword = '';

  constructor(private auth: AuthService, private router: Router) {}

  switchMode(m: AuthMode): void {
    this.mode.set(m);
    this.error.set(null);
  }

  submit(): void {
    this.mode() === 'login' ? this.doLogin() : this.doRegister();
  }

  private doLogin(): void {
    if (!this.loginEmail || !this.loginPassword) {
      this.error.set('Email and password are required');
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    this.auth.login({ email: this.loginEmail, password: this.loginPassword }).subscribe({
      next: () => this.router.navigate(['/']),
      error: e => {
        this.error.set(e.error?.message || 'Invalid credentials');
        this.loading.set(false);
      },
    });
  }

  private doRegister(): void {
    if (!this.regUsername || !this.regEmail || !this.regPassword) {
      this.error.set('All fields are required');
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    this.auth
      .register({ userName: this.regUsername, email: this.regEmail, password: this.regPassword })
      .subscribe({
        next: () => this.router.navigate(['/']),
        error: e => {
          this.error.set(e.error?.message || 'Registration failed');
          this.loading.set(false);
        },
      });
  }
}
