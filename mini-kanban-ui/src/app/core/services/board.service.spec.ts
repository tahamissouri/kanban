import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BoardService } from './board.service';
import { Board } from '../models/board.model';

const MOCK_BOARD: Board = {
  id: 1,
  name: 'Sprint 1',
  owner: { id: 10, userName: 'taha', email: 'taha@example.com' },
  members: [],
};

describe('BoardService', () => {
  let service: BoardService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(BoardService);
    http    = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('should create', () => expect(service).toBeTruthy());

  describe('getAll()', () => {
    it('should GET /boards and return an array', done => {
      service.getAll().subscribe(boards => {
        expect(boards.length).toBe(1);
        expect(boards[0].name).toBe('Sprint 1');
        done();
      });
      http.expectOne(r => r.url.endsWith('/boards') && r.method === 'GET')
        .flush([MOCK_BOARD]);
    });
  });

  describe('create()', () => {
    it('should POST /boards with the board name', done => {
      service.create({ name: 'New Board' }).subscribe(b => {
        expect(b.id).toBe(1);
        done();
      });
      const req = http.expectOne(r => r.url.endsWith('/boards') && r.method === 'POST');
      expect(req.request.body).toEqual({ name: 'New Board' });
      req.flush(MOCK_BOARD);
    });
  });

  describe('delete()', () => {
    it('should DELETE /boards/:id', done => {
      service.delete(1).subscribe(() => done());
      const req = http.expectOne(r => r.url.endsWith('/boards/1') && r.method === 'DELETE');
      req.flush(null, { status: 204, statusText: 'No Content' });
    });
  });

  describe('addMember()', () => {
    it('should PATCH /boards/:boardId/members/:userId', done => {
      service.addMember(1, 99).subscribe(() => done());
      const req = http.expectOne(r =>
        r.url.endsWith('/boards/1/members/99') && r.method === 'PATCH'
      );
      req.flush({});
    });
  });
});
