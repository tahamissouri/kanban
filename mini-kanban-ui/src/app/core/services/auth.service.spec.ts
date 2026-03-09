import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from './auth.service';

// A real (non-expired) JWT with sub=1, email=test@example.com
// Header: {"alg":"HS256","typ":"JWT"}
// Payload: {"sub":"1","email":"test@example.com","exp":9999999999}
const FAKE_TOKEN =
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9' +
  '.eyJzdWIiOiIxIiwiZW1haWwiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiZXhwIjo5OTk5OTk5OTk5fQ' +
  '.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c';

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
    });
    service = TestBed.inject(AuthService);
    http    = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
    localStorage.clear();
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  describe('login()', () => {
    it('should POST to /auth/login and store the token', done => {
      service.login({ email: 'test@example.com', password: 'pass' }).subscribe(() => {
        expect(localStorage.getItem('mk_token')).toBe(FAKE_TOKEN);
        expect(service.currentUserId()).toBe(1);
        expect(service.currentEmail()).toBe('test@example.com');
        done();
      });

      http.expectOne(r => r.url.endsWith('/auth/login')).flush({ token: FAKE_TOKEN });
    });
  });

  describe('register()', () => {
    it('should POST to /auth/signin and store the token', done => {
      service
        .register({ userName: 'taha', email: 'test@example.com', password: 'pass' })
        .subscribe(() => {
          expect(localStorage.getItem('mk_token')).toBe(FAKE_TOKEN);
          done();
        });

      http.expectOne(r => r.url.endsWith('/auth/signin')).flush({ token: FAKE_TOKEN });
    });
  });

  describe('isLoggedIn()', () => {
    it('should return false when no token', () => {
      expect(service.isLoggedIn()).toBe(false);
    });

    it('should return true when a valid non-expired token is present', () => {
      localStorage.setItem('mk_token', FAKE_TOKEN);
      expect(service.isLoggedIn()).toBe(true);
    });

    it('should return false for an expired token', () => {
      // exp: 1 (year 1970 — definitely expired)
      const expired =
        'eyJhbGciOiJIUzI1NiJ9' +
        '.eyJzdWIiOiIxIiwiZW1haWwiOiJ4QHguY29tIiwiZXhwIjoxfQ' +
        '.sig';
      localStorage.setItem('mk_token', expired);
      expect(service.isLoggedIn()).toBe(false);
    });
  });

  describe('logout()', () => {
    it('should clear token and reset signals', () => {
      localStorage.setItem('mk_token', FAKE_TOKEN);
      service.logout();
      expect(localStorage.getItem('mk_token')).toBeNull();
      expect(service.currentUserId()).toBeNull();
      expect(service.currentEmail()).toBeNull();
    });
  });

  describe('signal hydration on startup', () => {
    it('should read userId and email from stored token on init', () => {
      localStorage.setItem('mk_token', FAKE_TOKEN);
      // Re-create service so constructor runs with pre-existing token
      const fresh = new (AuthService as any)(
        TestBed.inject(require('@angular/common/http').HttpClient),
        TestBed.inject(require('@angular/router').Router)
      );
      expect(fresh.currentUserId()).toBe(1);
      expect(fresh.currentEmail()).toBe('test@example.com');
    });
  });
});
