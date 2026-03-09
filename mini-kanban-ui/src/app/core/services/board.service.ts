import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Board, CreateBoardRequest } from '../models/board.model';

@Injectable({ providedIn: 'root' })
export class BoardService {
  private readonly api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<Board[]>(`${this.api}/boards`);
  }

  create(body: CreateBoardRequest) {
    return this.http.post<Board>(`${this.api}/boards`, body);
  }

  delete(id: number) {
    return this.http.delete(`${this.api}/boards/${id}`);
  }

  addMember(boardId: number, userId: number) {
    return this.http.patch(`${this.api}/boards/${boardId}/members/${userId}`, {});
  }
}
