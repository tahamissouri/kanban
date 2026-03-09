export interface Column {
  id: number;
  name: string;
  position: number;
}

export interface CreateColumnRequest {
  name: string;
  position: number;
}
