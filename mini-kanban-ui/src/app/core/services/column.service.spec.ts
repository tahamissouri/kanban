import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ColumnService } from './column.service';
import { Column } from '../models/column.model';

const MOCK_COL: Column = { id: 5, name: 'To Do', position: 0 };

describe('ColumnService', () => {
  let service: ColumnService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(ColumnService);
    http    = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('should create', () => expect(service).toBeTruthy());

  it('getAll() should GET /boards/1/columns', done => {
    service.getAll(1).subscribe(cols => {
      expect(cols[0].name).toBe('To Do');
      done();
    });
    http.expectOne(r => r.url.endsWith('/boards/1/columns') && r.method === 'GET')
      .flush([MOCK_COL]);
  });

  it('create() should POST /boards/1/columns with name and position', done => {
    service.create(1, { name: 'In Progress', position: 1 }).subscribe(c => {
      expect(c.id).toBe(5);
      done();
    });
    const req = http.expectOne(r => r.url.endsWith('/boards/1/columns') && r.method === 'POST');
    expect(req.request.body).toEqual({ name: 'In Progress', position: 1 });
    req.flush(MOCK_COL);
  });

  it('update() should PUT /boards/1/columns/5', done => {
    service.update(1, 5, { name: 'Done', position: 2 }).subscribe(() => done());
    const req = http.expectOne(r => r.url.endsWith('/boards/1/columns/5') && r.method === 'PUT');
    expect(req.request.body.name).toBe('Done');
    req.flush({ ...MOCK_COL, name: 'Done', position: 2 });
  });

  it('delete() should DELETE /boards/1/columns/5 and return null for 204', done => {
    service.delete(1, 5).subscribe(() => done());
    http.expectOne(r => r.url.endsWith('/boards/1/columns/5') && r.method === 'DELETE')
      .flush(null, { status: 204, statusText: 'No Content' });
  });
});
