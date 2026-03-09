import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Column, CreateColumnRequest } from '../models/column.model';

@Injectable({ providedIn: 'root' })
export class ColumnService {
  private readonly api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAll(boardId: number) {
    return this.http.get<Column[]>(`${this.api}/boards/${boardId}/columns`);
  }

  create(boardId: number, body: CreateColumnRequest) {
    return this.http.post<Column>(`${this.api}/boards/${boardId}/columns`, body);
  }

  update(boardId: number, columnId: number, body: CreateColumnRequest) {
    return this.http.put<Column>(`${this.api}/boards/${boardId}/columns/${columnId}`, body);
  }

  delete(boardId: number, columnId: number) {
    return this.http.delete(`${this.api}/boards/${boardId}/columns/${columnId}`);
  }
}
