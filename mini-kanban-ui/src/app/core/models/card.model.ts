export interface Card {
  id: number;
  title: string;
  description: string;
  position: number;
}

export interface CreateCardRequest {
  title: string;
  description: string;
  position: number;
  columnId: number;
}

export type UpdateCardRequest = CreateCardRequest;
