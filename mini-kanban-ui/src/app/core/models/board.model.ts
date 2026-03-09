import { User } from './user.model';

export interface Board {
  id: number;
  name: string;
  owner: User;
  members: User[];
}

export interface CreateBoardRequest {
  name: string;
}
