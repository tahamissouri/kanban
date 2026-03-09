import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Card, CreateCardRequest, UpdateCardRequest } from '../models/card.model';

@Injectable({ providedIn: 'root' })
export class CardService {
  private readonly api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAll(boardId: number, columnId: number) {
    return this.http.get<Card[]>(`${this.api}/boards/${boardId}/columns/${columnId}/cards`);
  }

  create(boardId: number, columnId: number, body: CreateCardRequest) {
    return this.http.post<Card>(
      `${this.api}/boards/${boardId}/columns/${columnId}/cards`,
      body
    );
  }

  update(boardId: number, columnId: number, cardId: number, body: UpdateCardRequest) {
    return this.http.put<Card>(
      `${this.api}/boards/${boardId}/columns/${columnId}/cards/${cardId}`,
      body
    );
  }

  delete(boardId: number, columnId: number, cardId: number) {
    return this.http.delete(
      `${this.api}/boards/${boardId}/columns/${columnId}/cards/${cardId}`
    );
  }
}
