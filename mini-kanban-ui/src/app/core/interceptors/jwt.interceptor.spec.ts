import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import { jwtInterceptor } from './jwt.interceptor';

describe('jwtInterceptor', () => {
  let http: HttpClient;
  let controller: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        provideHttpClient(withInterceptors([jwtInterceptor])),
      ],
    });
    http       = TestBed.inject(HttpClient);
    controller = TestBed.inject(HttpTestingController);
  });

  afterEach(() => { controller.verify(); localStorage.clear(); });

  it('should not add Authorization header when no token', () => {
    http.get('/api/v1/boards').subscribe();
    const req = controller.expectOne('/api/v1/boards');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush([]);
  });

  it('should add Bearer Authorization header when token exists', () => {
    localStorage.setItem('mk_token', 'my.fake.token');
    http.get('/api/v1/boards').subscribe();
    const req = controller.expectOne('/api/v1/boards');
    expect(req.request.headers.get('Authorization')).toBe('Bearer my.fake.token');
    req.flush([]);
  });
});
