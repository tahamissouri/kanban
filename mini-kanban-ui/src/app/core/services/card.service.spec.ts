import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CardService } from './card.service';
import { Card } from '../models/card.model';

const MOCK_CARD: Card = { id: 42, title: 'Fix bug', description: 'Critical', position: 0 };

describe('CardService', () => {
  let service: CardService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(CardService);
    http    = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('should create', () => expect(service).toBeTruthy());

  it('getAll() should GET /boards/1/columns/5/cards', done => {
    service.getAll(1, 5).subscribe(cards => {
      expect(cards[0].title).toBe('Fix bug');
      done();
    });
    http.expectOne(r => r.url.endsWith('/boards/1/columns/5/cards') && r.method === 'GET')
      .flush([MOCK_CARD]);
  });

  it('create() should POST with title, description, position', done => {
    service.create(1, 5, { title: 'New card', description: '', position: 1 }).subscribe(c => {
      expect(c.id).toBe(42);
      done();
    });
    const req = http.expectOne(r =>
      r.url.endsWith('/boards/1/columns/5/cards') && r.method === 'POST'
    );
    expect(req.request.body).toEqual({ title: 'New card', description: '', position: 1 });
    req.flush(MOCK_CARD);
  });

  it('update() should PUT /boards/1/columns/5/cards/42', done => {
    service.update(1, 5, 42, { title: 'Updated', description: 'desc', position: 2 }).subscribe(() => done());
    const req = http.expectOne(r =>
      r.url.endsWith('/boards/1/columns/5/cards/42') && r.method === 'PUT'
    );
    expect(req.request.body.title).toBe('Updated');
    req.flush({ ...MOCK_CARD, title: 'Updated', position: 2 });
  });

  it('delete() should DELETE /boards/1/columns/5/cards/42', done => {
    service.delete(1, 5, 42).subscribe(() => done());
    http.expectOne(r =>
      r.url.endsWith('/boards/1/columns/5/cards/42') && r.method === 'DELETE'
    ).flush(null, { status: 204, statusText: 'No Content' });
  });
});
