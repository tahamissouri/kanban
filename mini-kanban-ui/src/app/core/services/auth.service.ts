import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { LoginRequest, RegisterRequest, TokenResponse } from '../models/auth.model';

interface JwtPayload {
  sub: string;
  email: string;
  exp: number;
}

function parseJwt(token: string): JwtPayload | null {
  try {
    return JSON.parse(atob(token.split('.')[1])) as JwtPayload;
  } catch {
    return null;
  }
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = environment.apiUrl;

  readonly currentUserId = signal<number | null>(this._loadUserId());
  readonly currentEmail  = signal<string | null>(this._loadEmail());

  constructor(private http: HttpClient, private router: Router) {}

  register(body: RegisterRequest) {
    return this.http
      .post<TokenResponse>(`${this.api}/auth/register`, body)
      .pipe(tap(r => this._storeToken(r.token)));
  }

  login(body: LoginRequest) {
    return this.http
      .post<TokenResponse>(`${this.api}/auth/login`, body)
      .pipe(tap(r => this._storeToken(r.token)));
  }

  logout(): void {
    localStorage.removeItem('mk_token');
    this.currentUserId.set(null);
    this.currentEmail.set(null);
    this.router.navigate(['/auth']);
  }

  isLoggedIn(): boolean {
    const token = localStorage.getItem('mk_token');
    if (!token) return false;
    const payload = parseJwt(token);
    if (!payload) return false;
    // check expiry
    return payload.exp * 1000 > Date.now();
  }

  getToken(): string | null {
    return localStorage.getItem('mk_token');
  }

  private _storeToken(token: string): void {
    localStorage.setItem('mk_token', token);
    const payload = parseJwt(token);
    if (payload) {
      this.currentUserId.set(Number(payload.sub));
      this.currentEmail.set(payload.email);
    }
  }

  private _loadUserId(): number | null {
    const t = localStorage.getItem('mk_token');
    if (!t) return null;
    const p = parseJwt(t);
    return p ? Number(p.sub) : null;
  }

  private _loadEmail(): string | null {
    const t = localStorage.getItem('mk_token');
    if (!t) return null;
    const p = parseJwt(t);
    return p?.email ?? null;
  }
}
