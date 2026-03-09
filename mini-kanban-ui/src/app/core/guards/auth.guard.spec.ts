import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

function runGuard(): boolean | UrlTree {
  return TestBed.runInInjectionContext(() =>
    authGuard({} as any, {} as any)
  ) as boolean | UrlTree;
}

describe('authGuard', () => {
  let authService: AuthService;
  let router: Router;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    authService = TestBed.inject(AuthService);
    router      = TestBed.inject(Router);
  });

  afterEach(() => localStorage.clear());

  it('should return true when user is logged in', () => {
    jest.spyOn(authService, 'isLoggedIn').mockReturnValue(true);
    expect(runGuard()).toBe(true);
  });

  it('should redirect to /auth when not logged in', () => {
    jest.spyOn(authService, 'isLoggedIn').mockReturnValue(false);
    const result = runGuard();
    expect(result instanceof UrlTree).toBe(true);
    expect(router.serializeUrl(result as UrlTree)).toBe('/auth');
  });
});
